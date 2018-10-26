package org.pcu.platform.server.service;

import org.pcu.connectors.index.PcuIndexException;
import org.pcu.platform.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class IngestService {

	@Autowired
	private KafkaTemplate<String, Document> kafkaTemplate;

	private static final String TOPIC = "Ingest";

	// TODO replace with real ingests
	public void ingestDocument(Document document) throws PcuIndexException {

		kafkaTemplate.send(TOPIC, document);

//		documentService.createDocument(ingestRequest.getDocument(), ingestRequest.getIndex(), ingestRequest.getType(),
//				ingestRequest.getId());

	}

}
