package org.pcu.connectors.indexer.elasticsearch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.pcu.connectors.indexer.PcuIndexer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PcuESIndexerIT {

	@Test
	public void indexerOnElasticsearchOK() throws IOException, InterruptedException {

		PcuIndexer pcuIndexer = new PcuESIndexer.Builder("http://localhost:9200/").build();

		assertThat(pcuIndexer).isNotNull();

		
		assertThatCode(() -> {
			boolean createdIndex = pcuIndexer.createIndex("test");
			assertThat(createdIndex).isTrue();
		}).doesNotThrowAnyException();
		
		assertThatCode(() -> {
			JsonFactory JSONFACTORY = new JsonFactory();
			ObjectMapper objectMapper = new ObjectMapper(JSONFACTORY);
			ObjectNode object = objectMapper.createObjectNode();
			object.put("field1", 10);
			object.put("field2", true);
			object.put("field3", "test");

			byte[] result = objectMapper.writeValueAsBytes(object);
			boolean createdDocument = pcuIndexer.createDocument(result, "test", "myType", "id_0");
			assertThat(createdDocument).isTrue();
		}).doesNotThrowAnyException();
		
		assertThatCode(() -> {
			boolean deletedIndex = pcuIndexer.deleteIndex("test");
			assertThat(deletedIndex).isTrue();
		}).doesNotThrowAnyException();
	}

}
