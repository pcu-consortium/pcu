package org.pcu.connectors.indexer;

import org.pcu.connectors.indexer.elasticsearch.PcuESIndexer;
import org.pcu.connectors.indexer.elasticsearch.PcuESIndexerConfiguration;

public class PcuIndexerFactory {

	public static PcuIndexer createIndexer(PcuIndexerType type, PcuIndexerConfiguration configuration) {
		switch (type) {
		case ES5:
		case ES6:
			PcuESIndexerConfiguration pcuESIndexerConfiguration = (PcuESIndexerConfiguration) configuration;
			return new PcuESIndexer.Builder(pcuESIndexerConfiguration.getUri()).build();
		default:
			throw new IllegalArgumentException("indexer type invalid");
		}

	}
}
