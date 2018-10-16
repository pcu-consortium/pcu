package org.pcu.platform.server;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.pcu.platform.IngestDocumentRequest;
import org.pcu.platform.client.PcuPlatformClient;
import org.pcu.platform.client.PcuPlatformClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PcuPlatformServerApplication.class, properties = { "pcu.index.type=ES6",
		"pcu.index.file=pcuindex.json" }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class PcuPlatformClientOnServerIT {

	@Value("${local.server.port}")
	int port;

	@Test
	public void testStatus() throws IOException {
		PcuPlatformClient pcuPlatformClient = PcuPlatformClient.connect("http://localhost:" + port);
		assertThatCode(() -> {
			pcuPlatformClient.status();
		}).doesNotThrowAnyException();
	}

	@Test
	public void testScenario() throws IOException {

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

		ObjectNode document = new ObjectMapper().createObjectNode();
		document.put("author", "testAuthor");
		IngestDocumentRequest ingestDocumentRequest = new IngestDocumentRequest();
		ingestDocumentRequest.setId(documentId);
		ingestDocumentRequest.setType(type);
		ingestDocumentRequest.setIndex(indexId);
		ingestDocumentRequest.setDocument(document);
		assertThatCode(() -> {
			pcuPlatformClient.ingest(ingestDocumentRequest);
		}).doesNotThrowAnyException();
		assertThatThrownBy(() -> {
			pcuPlatformClient.ingest(ingestDocumentRequest);
		}).isInstanceOf(PcuPlatformClientException.class).hasMessageContaining("500");

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

}
