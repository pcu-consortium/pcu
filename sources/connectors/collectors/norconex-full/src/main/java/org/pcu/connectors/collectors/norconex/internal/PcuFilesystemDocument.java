package org.pcu.connectors.collectors.norconex.internal;

public class PcuFilesystemDocument {

	private String id;
	private String index;
	private String type;
	private byte[] metadata;

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

	public byte[] getMetadata() {
		return metadata;
	}

	public void setMetadata(byte[] metadata) {
		this.metadata = metadata;
	}

}
