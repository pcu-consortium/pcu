package org.pcu.connectors.index.elasticsearch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.pcu.connectors.index.PcuIndex;
import org.pcu.connectors.index.PcuIndexException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PcuESIndexIT {

	private static PcuIndex pcuIndex;
	private String indexId;
	private String type;
	private String documentId;

	@BeforeAll
	public static void beforeClass() {
		pcuIndex = new PcuESIndex.Builder("http://localhost:9200/").build();
	}

	@BeforeEach
	public void before() {
		indexId = "pcu-index-test" + UUID.randomUUID().toString();
		type = UUID.randomUUID().toString();
		documentId = UUID.randomUUID().toString();
	}

	@Test
	public void indexOnElasticsearchOK() throws IOException, InterruptedException {

		assertThat(pcuIndex).isNotNull();

		// temp test
		assertThatCode(() -> {
			pcuIndex.deleteIndex(indexId);
		}).doesNotThrowAnyException();

		assertThatCode(() -> {
			boolean createdIndex = pcuIndex.createIndex(indexId);
			assertThat(createdIndex).isTrue();
		}).doesNotThrowAnyException();

		assertThatCode(() -> {
			JsonFactory JSONFACTORY = new JsonFactory();
			ObjectMapper objectMapper = new ObjectMapper(JSONFACTORY);
			ObjectNode object = objectMapper.createObjectNode();
			object.put("field1", 10);
			object.put("field2", true);
			object.put("field3", "test");

			boolean createdDocument = pcuIndex.createDocument(object, indexId, type, documentId);
			assertThat(createdDocument).isTrue();
		}).doesNotThrowAnyException();

		assertThatCode(() -> {
			JsonNode document = pcuIndex.getDocument(indexId, type, documentId);
			assertThat(document.get("_id").asText()).isEqualTo(documentId);
		}).doesNotThrowAnyException();

		assertThatCode(() -> {
			boolean deletedDocument = pcuIndex.deleteDocument(indexId, type, documentId);
			assertThat(deletedDocument).isTrue();
		}).doesNotThrowAnyException();

		assertThatThrownBy(() -> {
			pcuIndex.getDocument(indexId, type, documentId);
		}).isInstanceOf(PcuIndexException.class).hasMessageContaining("404");

		assertThatCode(() -> {
			boolean deletedIndex = pcuIndex.deleteIndex(indexId);
			assertThat(deletedIndex).isTrue();
		}).doesNotThrowAnyException();
	}

	@AfterEach
	public void after() {
		try {
			pcuIndex.deleteIndex(indexId);
		} catch (PcuIndexException e) {
			// quiet
		}
	}

}
