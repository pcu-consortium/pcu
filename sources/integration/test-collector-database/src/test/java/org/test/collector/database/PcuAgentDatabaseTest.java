package org.test.collector.database;

/*-
 * #%L
 * PCU Integration test : collector database
 * %%
 * Copyright (C) 2017 - 2018 PCU Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.pcu.connectors.collectors.PcuAgent;
import org.pcu.connectors.collectors.api.PcuCollectorConfig;
import org.pcu.integration.TemporaryFolderExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

import ru.lanwen.wiremock.ext.WiremockResolver;
import ru.lanwen.wiremock.ext.WiremockResolver.Wiremock;
import ru.lanwen.wiremock.ext.WiremockUriResolver;
import ru.lanwen.wiremock.ext.WiremockUriResolver.WiremockUri;

@ExtendWith({ TemporaryFolderExtension.class, WiremockResolver.class, WiremockUriResolver.class })
@RunWith(JUnitPlatform.class)
public class PcuAgentDatabaseTest {
	@RegisterExtension
	public TemporaryFolderExtension temporaryFolder = new TemporaryFolderExtension();

	@Test
	public void databaseCollectorOnDatabaseSampleOk(@Wiremock WireMockServer server, @WiremockUri String uri)
			throws IOException, URISyntaxException {

		URL createSQL = PcuAgentDatabaseTest.class.getClassLoader().getResource("create.sql");

		// wiremock stub
		assertThat(server.isRunning()).isTrue();
		server.stubFor(post("/ingest").willReturn(ok()));

		// pcu agent configuration
		String confFileName = UUID.randomUUID().toString();
		File confFile = temporaryFolder.newFile(confFileName + ".json");
		ObjectMapper mapper = new ObjectMapper();
		PcuCollectorConfig config = new PcuCollectorConfig();
		config.setCollectorId("collectorId");
		config.setDatasourceId("datasourceId");
		config.setPcuPlatformUrl(uri);
		config.set("pcuIndex", "documents");
		config.set("pcuType", "document");

		config.set("query", "SELECT TITLE AS title, DESCRIPTION AS description, * FROM TEST");

		config.set("url",
				"jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1;INIT=runscript from '" + createSQL.getPath() + "'");
		config.set("driver", "org.h2.Driver");
		config.set("username", "root");
		config.set("password", "123");

		mapper.writeValue(confFile, config);

		// first run pcu agent
		String[] args = new String[] { confFile.getAbsolutePath() };
		PcuAgent.main(args);

		// check stub calls
		List<LoggedRequest> ingestRequests = server.findAll(postRequestedFor(urlMatching("/ingest")));
		assertThat(ingestRequests).hasSize(3);
		assertThat(ingestRequests.get(0).getUrl()).isEqualTo("/ingest");
		assertThat(ingestRequests.get(0).getBodyAsString()).contains("ID1");
		assertThat(ingestRequests.get(1).getUrl()).isEqualTo("/ingest");
		assertThat(ingestRequests.get(1).getBodyAsString()).contains("ID2");
		assertThat(ingestRequests.get(2).getUrl()).isEqualTo("/ingest");
		assertThat(ingestRequests.get(2).getBodyAsString()).contains("ID3");

	}

}
