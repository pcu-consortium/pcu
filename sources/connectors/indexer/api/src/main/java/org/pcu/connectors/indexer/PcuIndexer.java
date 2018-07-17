package org.pcu.connectors.indexer;

/**
 * PCU indexer : connector containing all services to manage indexes.
 * 
 * @author gafou
 *
 */
public interface PcuIndexer extends AutoCloseable {

	boolean createDocument(byte[] document, String index, String type, String id);

	boolean deleteDocument(String index, String type, String id);

}
