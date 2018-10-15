package org.pcu.platform;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class IngestDocumentRequest extends DocumentRequest {
	@JsonProperty("document")
	private JsonNode document;

	public JsonNode getDocument() {
		return document;
	}

	public void setDocument(JsonNode document) {
		this.document = document;
	}

}
