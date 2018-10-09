package org.pcu.connectors.indexer;

import com.fasterxml.jackson.databind.JsonNode;

public class PcuIndexerConfiguration {

	private String type;
	private JsonNode configuration;

	public PcuIndexerConfiguration(String type, JsonNode configuration) {
		super();
		this.configuration = configuration;
		this.type = type;
	}

	public JsonNode getConfigutation() {
		return configuration;
	}

	public String getType() {
		return type;
	}

}
