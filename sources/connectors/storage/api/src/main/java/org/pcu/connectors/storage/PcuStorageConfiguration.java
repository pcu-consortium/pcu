package org.pcu.connectors.storage;

import com.fasterxml.jackson.databind.JsonNode;

public class PcuStorageConfiguration {

	private String type;
	private JsonNode configuration;

	public PcuStorageConfiguration(String type, JsonNode configuration) {
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
