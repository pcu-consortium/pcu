package org.pcu.platform.server;

import static com.jayway.restassured.RestAssured.delete;
import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.post;

import java.io.IOException;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pcu.platform.DocumentRequest;
import org.pcu.platform.IngestDocumentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PcuPlatformServerApplication.class, properties = { "pcu.index.type=ES6",
		"pcu.index.file=pcuindex.json" }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class PcuPlatformServerIT {

	@Value("${local.server.port}")
	int port;

	@Test
	public void testStatus() throws IOException {
		get("/status").then().assertThat().statusCode(HttpStatus.OK.value());
	}

	@Test
	public void testScenario() throws IOException {
		String indexId = UUID.randomUUID().toString();
		String documentId = UUID.randomUUID().toString();
		String type = UUID.randomUUID().toString();

		post("/indexes/" + indexId).then().assertThat().statusCode(HttpStatus.CREATED.value());
		post("/indexes/" + indexId).then().assertThat().statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

		ObjectNode document = new ObjectMapper().createObjectNode();
		document.put("author", "testAuthor");
		IngestDocumentRequest createRequest = new IngestDocumentRequest();
		createRequest.setId(documentId);
		createRequest.setType(type);
		createRequest.setIndex(indexId);
		createRequest.setDocument(document);
		given().body(createRequest).contentType(ContentType.JSON).when().post("/ingest").then().assertThat()
				.statusCode(HttpStatus.CREATED.value());
		given().body(createRequest).contentType(ContentType.JSON).when().post("/ingest").then().assertThat()
				.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

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

	@Before
	public void setBaseUri() {

		RestAssured.port = port;
		RestAssured.baseURI = "http://localhost";
	}
}
