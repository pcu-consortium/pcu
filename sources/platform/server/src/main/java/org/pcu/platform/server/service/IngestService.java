package org.pcu.platform.server.service;

import org.pcu.connectors.indexer.PcuIndexer;
import org.pcu.connectors.indexer.PcuIndexerException;
import org.pcu.platform.server.model.IngestRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IngestService {

	@Autowired
	private PcuIndexer pcuIndexer;

	public void createDocument(IngestRequest ingestRequest) throws PcuIndexerException {
		pcuIndexer.createDocument(ingestRequest.getDocument(), ingestRequest.getIndex(), ingestRequest.getType(),
				ingestRequest.getId());

	}

}
