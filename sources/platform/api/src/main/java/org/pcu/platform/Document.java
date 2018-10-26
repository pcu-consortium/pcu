package org.pcu.platform;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class Document {

	@JsonProperty("index")
	private String index;
	@JsonProperty("type")
	private String type;
	@JsonProperty("id")
	private String id;

	@JsonProperty("document")
	private JsonNode document;

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public JsonNode getDocument() {
		return document;
	}

	public void setDocument(JsonNode document) {
		this.document = document;
	}

}
