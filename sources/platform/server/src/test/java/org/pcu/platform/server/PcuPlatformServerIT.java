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
import org.pcu.platform.server.model.CreateDocumentRequest;
import org.pcu.platform.server.model.DocumentRequest;
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

		final ObjectMapper mapper = new ObjectMapper();
		final ObjectNode document = mapper.createObjectNode();
		document.put("author", "testAuthor");

		post("/index/" + indexId).then().assertThat().statusCode(HttpStatus.CREATED.value());
		post("/index/" + indexId).then().assertThat().statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

		CreateDocumentRequest createRequest = new CreateDocumentRequest();
		createRequest.setId(documentId);
		createRequest.setType(type);
		createRequest.setIndex(indexId);
		createRequest.setDocument(document);

		DocumentRequest deleteRequest = new DocumentRequest();
		deleteRequest.setId(documentId);
		deleteRequest.setType(type);
		deleteRequest.setIndex(indexId);

		given().body(createRequest).contentType(ContentType.JSON).when().post("/document").then().assertThat()
				.statusCode(HttpStatus.CREATED.value());
		given().body(createRequest).contentType(ContentType.JSON).when().post("/document").then().assertThat()
				.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

		given().body(deleteRequest).contentType(ContentType.JSON).when().delete("/document").then().assertThat()
				.statusCode(HttpStatus.NO_CONTENT.value());
		given().body(deleteRequest).contentType(ContentType.JSON).when().delete("/document").then().assertThat()
				.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

		delete("/index/" + indexId).then().assertThat().statusCode(HttpStatus.NO_CONTENT.value());
		delete("/index/" + indexId).then().assertThat().statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

	@Before
	public void setBaseUri() {

		RestAssured.port = port;
		RestAssured.baseURI = "http://localhost";
	}
}
