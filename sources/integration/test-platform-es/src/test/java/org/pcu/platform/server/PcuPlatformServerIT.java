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

import static com.jayway.restassured.RestAssured.delete;
import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.head;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.post;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.pcu.platform.Document;
import org.pcu.platform.DocumentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PcuPlatformServerApplication.class, properties = {
		"pcu.index.class=org.pcu.connectors.index.elasticsearch.PcuESIndex", "pcu.index.file=pcuindex.json",
		"pcu.storage.class=org.pcu.connectors.storage.vfs2.PcuVfs2Storage", "pcu.storage.file=PcuPlatformServerIT_pcustorage.json",
		"ingest.topic.metadata=PcuPlatformServerIT-Ingest-Metadata",
		"ingest.topic.file=PcuPlatformServerIT-Ingest-File", "index.topic.metadata=PcuPlatformServerIT-Ingest-Metadata",
		"spring.kafka.consumer.group-id=pcu-platform", "spring.kafka.bootstrap-servers=localhost:29093",
		"spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer",
		"spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer",
		"spring.kafka.consumer.properties.spring.json.trusted.packages=org.pcu.platform",
		"ingest.container.name.metadata=ingestMetadata",
		"ingest.container.name.file=ingestFile" }, webEnvironment = WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(partitions = 1, topics = { "PcuPlatformServerIT-Ingest-File",
		"PcuPlatformServerIT-Ingest-Metadata" }, brokerProperties = { "auto.create.topics.enable=true",
				"listeners=PLAINTEXT://localhost:29093", "port=29093" })
public class PcuPlatformServerIT {

	@Value("${local.server.port}")
	int port;

	private String indexId;
	private String documentId;
	private String type;

	@BeforeAll
	private static void beforeAll() throws IOException {
		FileUtils.deleteQuietly(new File("/tmp/PcuPlatformServerIT"));
		Files.createDirectories(Paths.get("/tmp/PcuPlatformServerIT"));
	}

	@AfterAll
	private static void afterAll() {
		FileUtils.deleteQuietly(new File("/tmp/PcuPlatformServerIT"));
	}

	@Test
	public void testStatus() throws IOException {
		head("/status").then().assertThat().statusCode(HttpStatus.NO_CONTENT.value());
		Response response = get("/status");
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getBody().asString()).contains("pipeline").contains("bootstrap.servers").contains("storage")
				.contains("index");
	}

	@Test
	public void testScenario() throws IOException, InterruptedException {

		post("/indexes/" + indexId).then().assertThat().statusCode(HttpStatus.CREATED.value());
		post("/indexes/" + indexId).then().assertThat().statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

		byte[] fileContent = Charset.forName("UTF-8").encode("test file").array();

		given().body(fileContent).contentType(ContentType.BINARY).when().post("/ingest/" + documentId).then()
				.assertThat().statusCode(HttpStatus.CREATED.value());

		ObjectNode content = new ObjectMapper().createObjectNode();
		content.put("author", "testAuthor");
		Document createRequest = new Document();
		createRequest.setId(documentId);
		createRequest.setType(type);
		createRequest.setIndex(indexId);
		createRequest.setDocument(content);
		given().body(createRequest).contentType(ContentType.JSON).when().post("/ingest").then().assertThat()
				.statusCode(HttpStatus.CREATED.value());

		Response searchResponse = null;
		int numtries = 10;
		while (numtries-- != 0) {
			JsonNode searchRequest = getSearchQuery();
			searchResponse = given().contentType(ContentType.JSON).body(searchRequest).when().post("/search");
			assertThat(searchResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());
			assertThat(searchResponse.getBody().asString()).contains("hits");
			if (searchResponse.getBody().asString().contains(documentId)) {
				break;
			} else {
				searchResponse = null;
				TimeUnit.SECONDS.sleep(10);
				continue;
			}
		}

		assertThat(searchResponse).isNotNull();

		DocumentRequest deleteRequest = new DocumentRequest();
		deleteRequest.setId(documentId);
		deleteRequest.setType(type);
		deleteRequest.setIndex(indexId);

		given().contentType(ContentType.JSON).when()
				.delete("/indexes/" + indexId + "/types/" + type + "/documents/" + documentId).then().assertThat()
				.statusCode(HttpStatus.NO_CONTENT.value());
		given().contentType(ContentType.JSON).when()
				.delete("/indexes/" + indexId + "/types/" + type + "/documents/" + documentId).then().assertThat()
				.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

		delete("/indexes/" + indexId).then().assertThat().statusCode(HttpStatus.NO_CONTENT.value());
		delete("/indexes/" + indexId).then().assertThat().statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

	@BeforeEach
	public void setUp() {
		RestAssured.port = port;
		RestAssured.baseURI = "http://localhost";

		indexId = UUID.randomUUID().toString();
		documentId = UUID.randomUUID().toString();
		type = UUID.randomUUID().toString();
	}

	@AfterEach
	public void setDown() {
		delete("/indexes/" + indexId);
	}

	private JsonNode getSearchQuery() throws IOException {
		String searchQuery = "{\"index\":\"{indexId}\",\"type\":\"{type}\",\"query\":{\"query\":{\"match\":{\"author\":\"testAuthor\"}}}}";
		searchQuery = searchQuery.replace("{indexId}", indexId);
		searchQuery = searchQuery.replace("{type}", type);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readTree(searchQuery);
	}
}
