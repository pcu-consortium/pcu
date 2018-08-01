package org.pcu.connectors.indexer;

import java.util.Iterator;
import java.util.ServiceLoader;

public class PcuIndexerFactory {

	private ServiceLoader<PcuIndexer> loader;

	public PcuIndexer getPcuIndexer(String indexerId) {
		Iterator<PcuIndexer> indexers = loader.iterator();
		while (indexers.hasNext()) {
			PcuIndexer indexer = indexers.next();
			if (indexerId.equals(indexer.getIndexerId())) {
				return indexer;
			}
		}
		// exception ?
		return null;
	}
}
