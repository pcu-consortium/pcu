package org.pcu.platform.server.service;

import org.pcu.connectors.index.PcuIndexException;
import org.pcu.platform.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class IngestService {

	private static final Logger LOGGER = LoggerFactory.getLogger(IngestService.class);

	@Autowired
	private KafkaTemplate<String, Document> kafkaTemplate;

	@Value("${kafka.topic.ingest}")
	private String ingestTopic;

	public void ingestDocument(Document document) throws PcuIndexException {
		LOGGER.debug("Add document in kafka ingestTopic");
		kafkaTemplate.send(ingestTopic, document);
	}

}
