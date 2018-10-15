package org.pcu.connectors.index.elasticsearch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.IOException;
import java.util.UUID;

import org.junit.After;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pcu.connectors.index.PcuIndex;
import org.pcu.connectors.index.PcuIndexException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PcuESIndexIT {

	private static PcuIndex pcuIndex;
	private String indexName;

	@BeforeAll
	public static void beforeClass() {
		pcuIndex = new PcuESIndex.Builder("http://localhost:9200/").build();
	}

	@BeforeEach
	public void before() {
		indexName = UUID.randomUUID().toString();
	}

	@Test
	public void indexOnElasticsearchOK() throws IOException, InterruptedException {

		assertThat(pcuIndex).isNotNull();

		// temp test
		assertThatCode(() -> {
			pcuIndex.deleteIndex(indexName);
		}).doesNotThrowAnyException();

		assertThatCode(() -> {
			boolean createdIndex = pcuIndex.createIndex(indexName);
			assertThat(createdIndex).isTrue();
		}).doesNotThrowAnyException();

		assertThatCode(() -> {
			JsonFactory JSONFACTORY = new JsonFactory();
			ObjectMapper objectMapper = new ObjectMapper(JSONFACTORY);
			ObjectNode object = objectMapper.createObjectNode();
			object.put("field1", 10);
			object.put("field2", true);
			object.put("field3", "test");

			boolean createdDocument = pcuIndex.createDocument(object, indexName, "myType", "id_0");
			assertThat(createdDocument).isTrue();
		}).doesNotThrowAnyException();

		assertThatCode(() -> {
			JsonNode document = pcuIndex.getDocument(indexName, "myType", "id_0");
			assertThat(document.get("_id").asText()).isEqualTo("id_0");
		}).doesNotThrowAnyException();

		assertThatCode(() -> {
			boolean deletedDocument = pcuIndex.deleteDocument(indexName, "myType", "id_0");
			assertThat(deletedDocument).isTrue();
		}).doesNotThrowAnyException();

		assertThatCode(() -> {
			JsonNode document = pcuIndex.getDocument(indexName, "myType", "id_0");
			assertThat(document.get("found").asBoolean()).isFalse();
		}).doesNotThrowAnyException();

		assertThatCode(() -> {
			boolean deletedIndex = pcuIndex.deleteIndex(indexName);
			assertThat(deletedIndex).isTrue();
		}).doesNotThrowAnyException();
	}

	@After
	public void after() {
		try {
			pcuIndex.deleteIndex(indexName);
		} catch (PcuIndexException e) {
			// quiet
		}
	}

}
