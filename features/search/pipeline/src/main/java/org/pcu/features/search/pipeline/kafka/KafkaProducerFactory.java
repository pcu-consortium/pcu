package org.pcu.features.search.pipeline.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaProducerFactory {

	private final static String TOPIC = "test";

	private final static String BOOTSTRAP_SERVERS = "localhost:9092";
	// ,localhost:9093,localhost:9094";

	public static Producer<String, String> createProducer() {

		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // LongSerializer
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		return new KafkaProducer<>(props);
	}

	public static Producer<String, String> createProducer(String urlServers) {

		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, urlServers);
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // LongSerializer
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		return new KafkaProducer<>(props);
	}

}
