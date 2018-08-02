package org.pcu.connectors.indexer.elasticsearch;

import org.springframework.beans.factory.annotation.Value;

public class PcuESIndexerConfig {

	@Value("${pcu.indexer.url}")
	private String url;

	public String getUrl() {
		return url;
	}

}
