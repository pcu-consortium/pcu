package org.pcu.platform.server.rest;

import org.pcu.connectors.indexer.PcuIndexerException;
import org.pcu.platform.server.model.CreateDocumentRequest;
import org.pcu.platform.server.model.DeleteDocumentRequest;
import org.pcu.platform.server.service.IngestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IngestController {

	@Autowired
	private IngestService ingestService;

	@RequestMapping(path = "/document", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Void> ingestDocument(@RequestBody CreateDocumentRequest ingestRequest) {
		try {
			ingestService.createDocument(ingestRequest);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (PcuIndexerException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/document", method = RequestMethod.DELETE, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Void> deleteDocument(@RequestBody DeleteDocumentRequest deleteRequest) {
		try {
			ingestService.deleteDocument(deleteRequest);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (PcuIndexerException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}