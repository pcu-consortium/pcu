package org.pcu.platform.server.service;

import org.pcu.connectors.indexer.PcuIndexer;
import org.pcu.connectors.indexer.PcuIndexerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IndexService {

	@Autowired
	private PcuIndexer pcuIndexer;

	public void createIndex(String indexId) throws PcuIndexerException {
		if (!pcuIndexer.createIndex(indexId)) {
			throw new PcuIndexerException("could not create index");
		}
	}

}
