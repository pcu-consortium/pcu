package org.pcu.platform.server.service;

import org.pcu.connectors.index.PcuIndex;
import org.pcu.connectors.index.PcuIndexException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class DocumentService {

	@Autowired
	private PcuIndex pcuIndex;

	public void search(JsonNode searchQuery) throws PcuIndexException {
		// TODO gafou

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
