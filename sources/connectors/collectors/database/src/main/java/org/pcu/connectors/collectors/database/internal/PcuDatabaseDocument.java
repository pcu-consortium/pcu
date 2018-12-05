package org.pcu.connectors.collectors.database.internal;

import com.fasterxml.jackson.databind.JsonNode;

public class PcuDatabaseDocument {
	private String reference;
	private String id;
	private String index;
	private String type;
	private JsonNode metadata;
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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
	public JsonNode getMetadata() {
		return metadata;
	}
	public void setMetadata(JsonNode metadata) {
		this.metadata = metadata;
	}
	

}
