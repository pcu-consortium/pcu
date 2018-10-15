package org.pcu.connectors.index;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * PCU index : connector containing all services to manage indexes.
 * 
 * @author gafou
 *
 */
public interface PcuIndex extends AutoCloseable {

	boolean createIndex(String index) throws PcuIndexException;
	
	boolean deleteIndex(String index) throws PcuIndexException;

	boolean createDocument(JsonNode document, String index, String type, String id) throws PcuIndexException;
	
	boolean deleteDocument(String index, String type, String id) throws PcuIndexException;
	
	JsonNode getDocument(String index, String type, String id) throws PcuIndexException;
	
	JsonNode getDocuments(JsonNode searchQuery) throws PcuIndexException;

}
