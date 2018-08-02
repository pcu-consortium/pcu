package org.pcu.connectors.indexer.elasticsearch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

@ExtendWith(RandomPortExtension.class)
public class PcuESIndexerTest {

	@RegisterExtension
	public RandomPortExtension randomPort = new RandomPortExtension();

	private EmbeddedElastic startElastic(String esVersion, String clusterName, Integer port)
			throws IOException, InterruptedException {
		return EmbeddedElastic.builder().withElasticVersion(esVersion)
				.withSetting(PopularProperties.TRANSPORT_TCP_PORT, port)
				.withSetting(PopularProperties.CLUSTER_NAME, clusterName).withEsJavaOpts("-Xms128m -Xmx512m")
				.withStartTimeout(1, TimeUnit.MINUTES).build().start();
	}

	@Test
	@Ignore
	public void indexerOnElasticsearch6Ok() throws IOException, InterruptedException {
//		String esVersion = "6.0.1";
//		String clusterName = UUID.randomUUID().toString();
//		Integer port = randomPort.getPort();
//		if (port == null) {
//			throw new InterruptedException("invalid port");
//		}
//		EmbeddedElastic embeddedElastic = startElastic(esVersion, clusterName, port);
		
		PcuESIndexer pcuIndexer = new PcuESIndexer();
		pcuIndexer.init();
		
		assertThat(pcuIndexer).isNotNull();
		System.out.println("DO THE THING");

		assertThatCode(() -> {
			pcuIndexer.createIndex("testIndex");
		}).doesNotThrowAnyException();

		assertThatCode(() -> {
			JsonFactory JSONFACTORY = new JsonFactory();
			ObjectMapper objectMapper = new ObjectMapper(JSONFACTORY);
			ObjectNode object = objectMapper.createObjectNode();
			object.put("field1", 10);
			object.put("field2", true);
			object.put("field3", "test");

			byte[] result = objectMapper.writeValueAsBytes(object);
			pcuIndexer.createDocument(result, "testIndex", "myType", "id_0");
		}).doesNotThrowAnyException();

		//embeddedElastic.stop();
	}

	@Test
	public void indexerOnElasticsearch5Ok() throws IOException {
	}

	@Test
	public void indexerOnElasticsearch2Ok() throws IOException {
	}

}
