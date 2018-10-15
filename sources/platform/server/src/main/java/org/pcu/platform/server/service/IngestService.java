package org.pcu.platform.server.service;

import org.pcu.connectors.index.PcuIndex;
import org.pcu.connectors.index.PcuIndexException;
import org.pcu.platform.server.model.CreateDocumentRequest;
import org.pcu.platform.server.model.DocumentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IngestService {

	@Autowired
	private PcuIndex pcuIndex;

	public void createDocument(CreateDocumentRequest ingestRequest) throws PcuIndexException {
		boolean created = pcuIndex.createDocument(ingestRequest.getDocument(), ingestRequest.getIndex(),
				ingestRequest.getType(), ingestRequest.getId());
		if (!created) {
			throw new PcuIndexException("could not create document");
		}
	}

	public void deleteDocument(DocumentRequest deleteRequest) throws PcuIndexException {
		boolean deleted = pcuIndex.deleteDocument(deleteRequest.getIndex(), deleteRequest.getType(),
				deleteRequest.getId());
		if (!deleted) {
			throw new PcuIndexException("could not delete document");
		}
	}

}
