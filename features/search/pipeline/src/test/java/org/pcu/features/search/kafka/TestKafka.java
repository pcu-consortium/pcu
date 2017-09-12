package org.pcu.features.search.kafka;

import org.apache.kafka.clients.producer.Producer;
import org.junit.Test;
import org.pcu.features.search.kafka.KafkaProducerFactory;
import org.pcu.features.search.kafka.ProducerKafka;
import org.pcu.providers.search.api.PcuDocument;



public class TestKafka {

	// @Test
	public void testString() throws Exception {
		Producer producer = KafkaProducerFactory.createProducer();

		for (int i = 0; i < 10; i++)
			ProducerKafka.runProducer(producer, "test6", "test", (long) 1);
		producer.flush();
		producer.close();

	}

	@Test
	public void testPcuDoc() throws Exception {

		Producer producer2 = KafkaProducerFactory.createProducer();
		PcuDocument doc = new PcuDocument();
		doc.setGlobal_version((long) 6);
		doc.setType("typeTest");
		doc.setVersion((long) 1);
		System.out.println(doc.toString());
		for (int i = 0; i < 10; i++) {
			doc.setId(i + "");
			ProducerKafka.runProducer(producer2, "test", doc, (long) 2);
		}
		producer2.flush();
		producer2.close();
	}

}
