package org.pcu.features.search.pipeline.kafka;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.pcu.providers.search.api.PcuDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


@Component
public class ProducerKafka {

   /** avro mapper so there is a schema to allow parsing even JSON in spark (required in streaming mode)
    * (and for checking consistency) */
   ///@Autowired @Qualifier("pcuApiAvroMapper")
   private ObjectMapper pcuApiAvroMapper;
   /** in case of schema-less / emerging schema without parsing JSON in spark ???
    * TODO dynamic field template in ES, ex match_mapping_type:date => format:epoch_millis for avro json or date_time for pcu api json... */
   @Autowired @Qualifier("pcuApiMapper")
   private ObjectMapper pcuApiMapper;
   @PostConstruct
   private void init() {
      pcuApiAvroMapper = pcuApiMapper.copy().enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
   }

	public void runProducer(Producer<String, String> producer, String topic, String text, String key) throws Exception {

		ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, text);

		RecordMetadata metadata = producer.send(record).get();
		System.out.println("sent record(key=" + record.key() + " value=" + record.value() + ") meta(partition="
				+ metadata.partition() + ", offset=" + metadata.offset() + ")");

	}

	public RecordMetadata runProducer(Producer<String, String> producer, String topic, PcuDocument doc, String key) throws Exception {
		String docJSON = pcuApiMapper.writeValueAsString(doc);

		ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, docJSON);

		RecordMetadata metadata = producer.send(record).get();
		System.out.println("sent record(key=" + record.key() + " value=" + record.value() + ") meta(partition="
				+ metadata.partition() + ", offset=" + metadata.offset() + ")");
		return metadata;
	}
}
