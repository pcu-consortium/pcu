package org.pcu.platform.server;

import static com.jayway.restassured.RestAssured.delete;
import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.post;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.pcu.platform.Document;
import org.pcu.platform.DocumentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PcuPlatformServerApplication.class, properties = { "pcu.index.type=ES6",
		"pcu.index.file=pcuindex.json" , "bootstrap.servers=kafka:9092"}, webEnvironment = WebEnvironment.DEFINED_PORT)
public class PcuPlatformServerIT {

	@Value("${local.server.port}")
	int port;

	private String indexId;
	private String documentId;
	private String type;

	@Test
	public void testStatus() throws IOException {
		get("/status").then().assertThat().statusCode(HttpStatus.OK.value());
		Response response = get("/configuration");
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getBody().asString()).contains("bootstrap.servers");
	}

	@Test
	public void testScenario() throws IOException {

		post("/indexes/" + indexId).then().assertThat().statusCode(HttpStatus.CREATED.value());
		post("/indexes/" + indexId).then().assertThat().statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

		ObjectNode content = new ObjectMapper().createObjectNode();
		content.put("author", "testAuthor");
		Document createRequest = new Document();
		createRequest.setId(documentId);
		createRequest.setType(type);
		createRequest.setIndex(indexId);
		createRequest.setDocument(content);
		given().body(createRequest).contentType(ContentType.JSON).when().post("/ingest").then().assertThat()
				.statusCode(HttpStatus.CREATED.value());
		given().body(createRequest).contentType(ContentType.JSON).when().post("/ingest").then().assertThat()
				.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

		ObjectNode searchRequest = new ObjectMapper().createObjectNode();
		ObjectNode esQuery = new ObjectMapper().createObjectNode();
		esQuery.set("query", new ObjectMapper().createObjectNode());
		searchRequest.put("index", indexId);
		searchRequest.put("type", type);
		searchRequest.set("query", esQuery);

		// TODO check result
		given().contentType(ContentType.JSON).body(searchRequest).when().post("/search").then().assertThat()
				.statusCode(HttpStatus.OK.value());

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
}
