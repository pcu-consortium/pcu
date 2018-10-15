package org.pcu.connectors.index;

import org.pcu.connectors.index.elasticsearch.PcuESIndex;

public class PcuIndexFactory {

	public static PcuIndex createIndex(PcuIndexConfiguration configuration) {
		switch (configuration.getType()) {
		case "ES5":
		case "ES6":
			return new PcuESIndex.Builder(configuration.getConfigutation().get("uri").asText()).build();
		default:
			throw new IllegalArgumentException("index type invalid");
		}

	}
}
