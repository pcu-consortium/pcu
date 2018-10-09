package org.pcu.connectors.indexer;

import org.pcu.connectors.indexer.elasticsearch.PcuESIndexer;

public class PcuIndexerFactory {

	public static PcuIndexer createIndexer(PcuIndexerConfiguration configuration) {
		switch (configuration.getType()) {
		case "ES5":
		case "ES6":
			return new PcuESIndexer.Builder(configuration.getConfigutation().get("uri").asText()).build();
		default:
			throw new IllegalArgumentException("indexer type invalid");
		}

	}
}
