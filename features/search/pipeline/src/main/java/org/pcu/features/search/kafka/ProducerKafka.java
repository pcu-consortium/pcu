package org.pcu.features.search.kafka;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.pcu.providers.search.api.PcuDocument;

import com.fasterxml.jackson.databind.ObjectMapper;


public class ProducerKafka {

	static void runProducer(Producer<Long, String> producer) throws Exception {

		ProducerRecord<Long, String> record = new ProducerRecord<>("test", (long) 0, "test");

		RecordMetadata meta = producer.send(record).get();
		System.out.println(meta.toString());
	}

	static void runProducer(Producer<Long, String> producer, String topic, String text) throws Exception {

		ProducerRecord<Long, String> record = new ProducerRecord("test", (long) 0, text);

		RecordMetadata meta = producer.send(record).get();
		System.out.println(meta.toString());
	}

	static void runProducer(Producer<Long, String> producer, String topic, String text, long key) throws Exception {

		ProducerRecord<Long, String> record = new ProducerRecord<>(topic, key, text);

		RecordMetadata metadata = producer.send(record).get();
		System.out.println("sent record(key=" + record.key() + " value=" + record.value() + ") meta(partition="
				+ metadata.partition() + ", offset=" + metadata.offset() + ")");

	}

	static void runProducer(Producer<Long, String> producer, String topic, PcuDocument doc, long key) throws Exception {

		ObjectMapper mapper = new ObjectMapper();

		String docJSON = mapper.writeValueAsString(doc);

		ProducerRecord<Long, String> record = new ProducerRecord<>(topic, key, docJSON);

		RecordMetadata metadata = producer.send(record).get();
		System.out.println("sent record(key=" + record.key() + " value=" + record.value() + ") meta(partition="
				+ metadata.partition() + ", offset=" + metadata.offset() + ")");

	}
}
