package org.pcu.connectors.indexer.elasticsearch;

import org.pcu.connectors.indexer.PcuIndexerConfiguration;

public class PcuESIndexerConfiguration implements PcuIndexerConfiguration {

	private String uri;

	public PcuESIndexerConfiguration(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

}
