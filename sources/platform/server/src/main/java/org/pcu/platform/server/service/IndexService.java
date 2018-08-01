package org.pcu.platform.server.service;

import org.pcu.connectors.indexer.PcuIndexer;
import org.pcu.connectors.indexer.PcuIndexerException;
import org.pcu.connectors.indexer.PcuIndexerFactory;
import org.springframework.stereotype.Component;

@Component
public class IndexService {

	private PcuIndexer pcuIndexer;

	public IndexService() {
		PcuIndexerFactory factory = new PcuIndexerFactory();
		pcuIndexer = factory.getPcuIndexer("ES5");
	}

	public void createIndex(String indexId) throws PcuIndexerException {
		if (!pcuIndexer.createIndex(indexId)) {
			throw new PcuIndexerException("could not create index");
		}
	}

}
