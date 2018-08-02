package org.pcu.platform.server.service;

import java.io.IOException;

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
		try {
			pcuIndexer.createDocument(ingestRequest.getDocument().binaryValue(), ingestRequest.getIndex(),
					ingestRequest.getType(), ingestRequest.getId());
		} catch (IOException e) {
			throw new PcuIndexerException("could not index document");
		}
	}

}
