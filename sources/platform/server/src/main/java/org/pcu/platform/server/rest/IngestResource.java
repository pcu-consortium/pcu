package org.pcu.platform.server.rest;

import org.pcu.connectors.index.PcuIndexException;
import org.pcu.platform.Document;
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
public class IngestResource {

	@Autowired
	private IngestService ingestService;

	@RequestMapping(path = "/ingest", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Void> ingestDocument(@RequestBody Document document) {
		try {
			ingestService.ingestDocument(document);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (PcuIndexException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}