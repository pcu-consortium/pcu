package org.pcu.platform.server.service;

import org.pcu.connectors.index.PcuIndexException;
import org.pcu.platform.IngestDocumentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IngestService {

	@Autowired
	private DocumentService documentService;

	// TODO replace with real ingests
	public void ingestDocument(IngestDocumentRequest ingestRequest) throws PcuIndexException {
		documentService.createDocument(ingestRequest.getDocument(), ingestRequest.getIndex(), ingestRequest.getType(),
				ingestRequest.getId());

	}

}
