package org.pcu.platform.server.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.pcu.connectors.index.PcuIndex;
import org.pcu.connectors.index.PcuIndexException;
import org.pcu.platform.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class DocumentService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentService.class);

	@Autowired
	private PcuIndex pcuIndex;

//	@KafkaListener(topics = "${kafka.topic.addDocument}")
//	public void receive(Document payload) throws PcuIndexException {
//		LOGGER.info("received kafka payload addDocument='{}'", payload);
//		createDocument(payload.getDocument(), payload.getIndex(), payload.getType(), payload.getId());
//	}

	@KafkaListener(topics = "${kafka.topic.addDocument}")
	public void listen(ConsumerRecord<String, Document> cr) throws Exception {
		LOGGER.info("received kafka ='{}'", cr.key());
		LOGGER.info(cr.toString());
		createDocument(cr.value().getDocument(), cr.value().getIndex(), cr.value().getType(), cr.value().getId());
		// latch.countDown();
	}

	public JsonNode search(JsonNode searchQuery) throws PcuIndexException {
		return pcuIndex.getDocuments(searchQuery);

	}

	public JsonNode get(String indexId, String type, String documentId) throws PcuIndexException {
		return pcuIndex.getDocument(indexId, type, documentId);
	}

	public void deleteDocument(String indexId, String type, String documentId) throws PcuIndexException {
		boolean deleted = pcuIndex.deleteDocument(indexId, type, documentId);
		if (!deleted) {
			throw new PcuIndexException("could not delete document");
		}
	}

	public void createDocument(JsonNode document, String indexId, String type, String documentId)
			throws PcuIndexException {
		boolean created = pcuIndex.createDocument(document, indexId, type, documentId);
		if (!created) {
			throw new PcuIndexException("could not create document");
		}
	}

}
