package org.pcu.platform.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteDocumentRequest {
	
	@JsonProperty("index")
	private String index;
	@JsonProperty("type")
	private String type;
	@JsonProperty("id")
	private String id;
	
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
}
