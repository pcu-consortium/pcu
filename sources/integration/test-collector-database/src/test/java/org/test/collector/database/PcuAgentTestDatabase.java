package org.test.collector.database;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.noContent;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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
import com.google.common.base.Charsets;

import ru.lanwen.wiremock.ext.WiremockResolver;
import ru.lanwen.wiremock.ext.WiremockUriResolver;
import ru.lanwen.wiremock.ext.WiremockResolver.Wiremock;
import ru.lanwen.wiremock.ext.WiremockUriResolver.WiremockUri;


@ExtendWith({ TemporaryFolderExtension.class, WiremockResolver.class, WiremockUriResolver.class })
@RunWith(JUnitPlatform.class)
public class PcuAgentTestDatabase {
	@RegisterExtension
	public TemporaryFolderExtension temporaryFolder = new TemporaryFolderExtension();

	@Test
	public void norconexCollectorOnFileSampleOk(@Wiremock WireMockServer server, @WiremockUri String uri)
			throws IOException, URISyntaxException {
        
		
		
		// wiremock stub
		assertThat(server.isRunning()).isTrue();
		server.stubFor(post("/ingest").willReturn(ok()));
		server.stubFor(delete(urlPathMatching("/indexes/(.*)")).willReturn(noContent()));

		// pcu agent configuration
		String confFileName = UUID.randomUUID().toString();
		File confFile = temporaryFolder.newFile(confFileName + ".json");
		String workDirFolderName = UUID.randomUUID().toString();
		File workDir = temporaryFolder.newFolder(workDirFolderName);
		File fileFolder = createTemporaryFolderWithFileSample();
		ObjectMapper mapper = new ObjectMapper();
		PcuCollectorConfig config = new PcuCollectorConfig();
		config.setCollectorId("collectorId");
		config.setDatasourceId("datasourceId");
     	config.setPcuPlatformUrl(uri);
		config.set("username", "root");
		config.set("driver", "com.mysql.jdbc.Driver");
		config.set("password", "123");
		config.set("typequery", "select");
		//config.set("query", "insert into  tutorial (id,name) values (4,'mysql') ");
		config.set("query", "select * from tutorial ");
		config.set("url", "jdbc:mysql://127.0.0.1:3306/testdatabase");
		config.setDatasourceId("datasourceId");
		
		mapper.writeValue(confFile, config);

		// first run pcu agent
		String[] args = new String[] { confFile.getAbsolutePath() };
		PcuAgent.main(args);
		List<String> workDirFileNames = Arrays.asList(workDir.listFiles()).stream().map(File::getAbsolutePath)
				.collect(Collectors.toList());
	// second run pcu agent after deleting a file
		PcuAgent.main(args);

		// check stub calls
		List<LoggedRequest> ingestRequests = server.findAll(postRequestedFor(urlMatching("/ingest")));
		List<LoggedRequest> deleteRequests = server.findAll(deleteRequestedFor(urlPathMatching("/indexes/(.*)")));

		//assertThat(ingestRequests).hasSize(3);
		assertThat(ingestRequests.get(0).getUrl()).isEqualTo("/ingest");
		assertThat(ingestRequests.get(1).getUrl()).isEqualTo("/ingest");
	
	}
	private String createTempVariables(String workDir, String targetPath) throws IOException {
		String filename = UUID.randomUUID().toString();
		File tmpFile = File.createTempFile(filename, ".variables");
		InputStream is = PcuAgentTestDatabase.class.getClassLoader()
				.getResourceAsStream("norconex-database.variables");
		String content = IOUtils.toString(is, Charsets.UTF_8);
		content = content.replace("${workDir}", workDir).replace("${targetPath}", targetPath);
		FileUtils.writeStringToFile(tmpFile, content, Charsets.UTF_8);
		return tmpFile.getAbsolutePath();
	}


	private String createTempXml() throws IOException {
		String filename = UUID.randomUUID().toString();
		File tmpFile = File.createTempFile(filename, ".xml");
		InputStream is = PcuAgentTestDatabase.class.getClassLoader().getResourceAsStream("norconex-database.xml");
		FileUtils.copyInputStreamToFile(is, tmpFile);
		return tmpFile.getAbsolutePath();
	}

	private File createTemporaryFolderWithFileSample() throws IOException, URISyntaxException {
		File newFileFolder = temporaryFolder.newFolder("file");
		File existingFileFolder = new File(PcuAgentTestDatabase.class.getClassLoader().getResource("file").toURI());
		FileUtils.copyDirectory(existingFileFolder, newFileFolder);
		return newFileFolder;
	}

}