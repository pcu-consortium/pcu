package org.pcu.connectors.indexer;

import com.fasterxml.jackson.databind.JsonNode;

public interface PcuIndexer extends AutoCloseable {

	public boolean index(JsonNode document, String index, String type, String id);

}
