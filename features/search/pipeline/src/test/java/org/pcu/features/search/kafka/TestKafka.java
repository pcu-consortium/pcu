package org.pcu.features.search.kafka;

import org.apache.kafka.clients.producer.Producer;
import org.junit.Ignore;
import org.junit.Test;
import org.pcu.features.search.pipeline.kafka.KafkaProducerFactory;
import org.pcu.features.search.pipeline.kafka.ProducerKafka;
import org.pcu.providers.search.api.PcuDocument;



public class TestKafka {
   
   private ProducerKafka producerKafka = new ProducerKafka();

	// @Test
	public void testString() throws Exception {
		Producer<String, String> producer = KafkaProducerFactory.createProducer();

		for (int i = 0; i < 10; i++)
			producerKafka.runProducer(producer, "test6", "test", i + "");
		producer.flush();
		producer.close();

	}

	@Test
	@Ignore // disabled until this test becomes meaningful & its topic is set up
	public void testPcuDoc() throws Exception {

		Producer<String, String> producer2 = KafkaProducerFactory.createProducer();
		PcuDocument doc = new PcuDocument();
		doc.setGlobal_version((long) 6);
		doc.setType("typeTest");
		doc.setVersion((long) 1);
		System.out.println(doc.toString());
		for (int i = 0; i < 10; i++) {
			doc.setId(i + "");
			producerKafka.runProducer(producer2, "test", doc, doc.getId());
		}
		producer2.flush();
		producer2.close();
	}

}
