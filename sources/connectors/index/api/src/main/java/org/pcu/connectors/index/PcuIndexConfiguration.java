package org.pcu.connectors.index;

import com.fasterxml.jackson.databind.JsonNode;

public class PcuIndexConfiguration {

	private String type;
	private JsonNode configuration;

	public PcuIndexConfiguration(String type, JsonNode configuration) {
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
