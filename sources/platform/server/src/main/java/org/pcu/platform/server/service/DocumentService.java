package org.pcu.platform.server.service;

/*-
 * #%L
 * PCU Platform Server
 * %%
 * Copyright (C) 2017 - 2018 PCU Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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

	@KafkaListener(topics = "${index.topic.metadata}")
	public void createDocumentFromKafka(ConsumerRecord<String, Document> cr) throws Exception {
		LOGGER.debug("add document in index from pipeline");
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
		LOGGER.debug("create document");
		boolean created = pcuIndex.createDocument(document, indexId, type, documentId);
		if (!created) {
			throw new PcuIndexException("could not create document");
		}
	}

}
