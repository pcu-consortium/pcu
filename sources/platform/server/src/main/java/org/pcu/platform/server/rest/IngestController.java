package org.pcu.platform.server.rest;

import org.pcu.connectors.indexer.PcuIndexerException;
import org.pcu.platform.server.model.IngestRequest;
import org.pcu.platform.server.service.IngestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IngestController {

	@Autowired
	private IngestService ingestService;

	@RequestMapping(path = "/document", method = RequestMethod.POST)
	public ResponseEntity<Void> ingest(@RequestBody IngestRequest ingestRequest) {
		try {
			ingestService.createDocument(ingestRequest);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (PcuIndexerException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}