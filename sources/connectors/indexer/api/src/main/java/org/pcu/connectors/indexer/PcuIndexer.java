package org.pcu.connectors.indexer;

/**
 * PCU indexer : connector containing all services to manage indexes.
 * 
 * @author gafou
 *
 */
public interface PcuIndexer extends AutoCloseable {

	boolean createIndex(String index) throws PcuIndexerException;

	boolean createDocument(byte[] document, String index, String type, String id) throws PcuIndexerException;

	boolean deleteDocument(String index, String type, String id) throws PcuIndexerException;

}
