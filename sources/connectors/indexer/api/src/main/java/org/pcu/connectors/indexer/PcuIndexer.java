package org.pcu.connectors.indexer;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * PCU indexer : connector containing all services to manage indexes.
 * 
 * @author gafou
 *
 */
public interface PcuIndexer extends AutoCloseable {

	boolean createIndex(String index) throws PcuIndexerException;
	
	boolean deleteIndex(String index) throws PcuIndexerException;

	boolean createDocument(JsonNode document, String index, String type, String id) throws PcuIndexerException;

	boolean deleteDocument(String index, String type, String id) throws PcuIndexerException;

}
