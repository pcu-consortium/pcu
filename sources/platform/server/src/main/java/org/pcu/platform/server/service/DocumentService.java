package org.pcu.platform.server.service;

import org.pcu.connectors.index.PcuIndexException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class DocumentService {

	public void search(JsonNode searchQuery) throws PcuIndexException {
		// TODO gafou

	}

	public JsonNode get(String indexId, String documentId) throws PcuIndexException {
		// TODO gafou
		return null;
	}

}
