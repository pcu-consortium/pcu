package org.pcu.platform.server.service;

import org.pcu.connectors.indexer.PcuIndexer;
import org.pcu.connectors.indexer.PcuIndexerException;
import org.pcu.platform.server.model.CreateDocumentRequest;
import org.pcu.platform.server.model.DeleteDocumentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IngestService {

	@Autowired
	private PcuIndexer pcuIndexer;

	public void createDocument(CreateDocumentRequest ingestRequest) throws PcuIndexerException {
		boolean created = pcuIndexer.createDocument(ingestRequest.getDocument(), ingestRequest.getIndex(),
				ingestRequest.getType(), ingestRequest.getId());
		if (!created) {
			throw new PcuIndexerException("could not create document");
		}
	}

	public void deleteDocument(DeleteDocumentRequest deleteRequest) throws PcuIndexerException {
		boolean deleted = pcuIndexer.deleteDocument(deleteRequest.getIndex(), deleteRequest.getType(),
				deleteRequest.getId());
		if (!deleted) {
			throw new PcuIndexerException("could not delete document");
		}
	}

}
