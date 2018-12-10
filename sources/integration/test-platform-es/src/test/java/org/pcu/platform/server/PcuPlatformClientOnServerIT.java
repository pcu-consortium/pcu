package org.pcu.platform.server;

/*-
 * #%L
 * PCU Integration test : platform es
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





import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.pcu.platform.Document;
import org.pcu.platform.client.PcuPlatformClient;
import org.pcu.platform.client.PcuPlatformClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PcuPlatformServerApplication.class, properties = { "pcu.index.type=ES6",
		"pcu.index.file=pcuindex.json", "pcu.storage.type=VFS2",
		"pcu.storage.file=PcuPlatformClientOnServerIT_pcustorage.json",
		"ingest.topic.metadata=PcuPlatformClientOnServerIT-Ingest-Metadata",
		"ingest.topic.file=PcuPlatformClientOnServerIT-Ingest-File",
		"index.topic.metadata=PcuPlatformClientOnServerIT-Ingest-Metadata",
		"spring.kafka.consumer.group-id=pcu-platform", "spring.kafka.bootstrap-servers=localhost:29092",
		"spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer",
		"spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer",
		"spring.kafka.consumer.properties.spring.json.trusted.packages=org.pcu.platform",
		"ingest.container.name.metadata=ingestMetadata",
		"ingest.container.name.file=ingestFile" }, webEnvironment = WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(partitions = 1, topics = { "PcuPlatformClientOnServerIT-Ingest-File",
		"PcuPlatformClientOnServerIT-Ingest-Metadata" }, brokerProperties = { "auto.create.topics.enable=true",
				"listeners=PLAINTEXT://localhost:29092", "port=29092" })
public class PcuPlatformClientOnServerIT {

	@Value("${local.server.port}")
	int port;

	@BeforeAll
	private static void beforeAll() throws IOException {
		FileUtils.deleteQuietly(new File("/tmp/PcuPlatformClientOnServerIT"));
		Files.createDirectories(Paths.get("/tmp/PcuPlatformClientOnServerIT"));
	}

	@AfterAll
	private static void afterAll() {
		FileUtils.deleteQuietly(new File("/tmp/PcuPlatformClientOnServerIT"));
	}

	@Test
	public void testStatus() throws IOException {
		PcuPlatformClient pcuPlatformClient = PcuPlatformClient.connect("http://localhost:" + port);
		assertThatCode(() -> {
			pcuPlatformClient.status();
		}).doesNotThrowAnyException();
	}

	@Test
	public void testScenario() throws IOException, InterruptedException {

		PcuPlatformClient pcuPlatformClient = PcuPlatformClient.connect("http://localhost:" + port);

		String indexId = "pcu-index-test" + UUID.randomUUID().toString();
		String documentId = UUID.randomUUID().toString();
		String type = UUID.randomUUID().toString();

		assertThatCode(() -> {
			pcuPlatformClient.createIndex(indexId);
		}).doesNotThrowAnyException();
		assertThatThrownBy(() -> {
			pcuPlatformClient.createIndex(indexId);
		}).isInstanceOf(PcuPlatformClientException.class).hasMessageContaining("500");

		InputStream fileContent = new ByteArrayInputStream(Charset.forName("UTF-8").encode("test file").array());

		assertThatCode(() -> {
			pcuPlatformClient.ingest(documentId, fileContent);
		}).doesNotThrowAnyException();

		File fileIngested = new File("/tmp/PcuPlatformClientOnServerIT/ingestFile/" + documentId);
		assertThat(fileIngested.exists()).isTrue();
		List<String> lines = Files.readAllLines(Paths.get("/tmp/PcuPlatformClientOnServerIT/ingestFile/" + documentId),
				Charset.forName("UTF-8"));
		assertThat(lines.size()).isEqualTo(1);
		assertThat(lines.get(0)).isEqualTo("test file");

		ObjectNode content = new ObjectMapper().createObjectNode();
		content.put("author", "testAuthor");
		Document createRequest = new Document();
		createRequest.setId(documentId);
		createRequest.setType(type);
		createRequest.setIndex(indexId);
		createRequest.setDocument(content);

		assertThatCode(() -> {
			pcuPlatformClient.ingest(createRequest);
		}).doesNotThrowAnyException();

		TimeUnit.SECONDS.sleep(4);

		assertThatCode(() -> {
			pcuPlatformClient.getDocument(indexId, type, documentId);
		}).doesNotThrowAnyException();
		assertThatThrownBy(() -> {
			pcuPlatformClient.getDocument(indexId, type, "documentId");
		}).isInstanceOf(PcuPlatformClientException.class).hasMessageContaining("500");

		assertThatCode(() -> {
			pcuPlatformClient.deleteDocument(indexId, type, documentId);
		}).doesNotThrowAnyException();
		assertThatThrownBy(() -> {
			pcuPlatformClient.deleteDocument(indexId, type, documentId);
		}).isInstanceOf(PcuPlatformClientException.class).hasMessageContaining("500");

		assertThatCode(() -> {
			pcuPlatformClient.deleteIndex(indexId);
		}).doesNotThrowAnyException();
		assertThatThrownBy(() -> {
			pcuPlatformClient.deleteIndex(indexId);
		}).isInstanceOf(PcuPlatformClientException.class).hasMessageContaining("500");
	}

	private JsonNode getSearchQuery(String indexId, String type) throws IOException {
		String searchQuery = "{\"index\":\"{indexId}\",\"type\":\"{type}\",\"query\":{\"query\":{\"match\":{\"author\":\"testAuthor\"}}}}";
		searchQuery = searchQuery.replace("{indexId}", indexId);
		searchQuery = searchQuery.replace("{type}", type);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readTree(searchQuery);
	}

}
