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

	@KafkaListener(topics = "${kafka.topic.addDocument}")
	public void createDocumentFromKafka(ConsumerRecord<String, Document> cr) throws Exception {
		LOGGER.debug("add document in index from kafka topic");
		createDocument(cr.value().getDocument(), cr.value().getIndex(), cr.value().getType(), cr.value().getId());
	}

	public JsonNode search(JsonNode searchQuery) throws PcuIndexException {
		LOGGER.debug("search document");
		return pcuIndex.getDocuments(searchQuery);

	}

	public JsonNode get(String indexId, String type, String documentId) throws PcuIndexException {
		LOGGER.debug("get document");
		return pcuIndex.getDocument(indexId, type, documentId);
	}

	public void deleteDocument(String indexId, String type, String documentId) throws PcuIndexException {
		LOGGER.debug("delete document");
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
