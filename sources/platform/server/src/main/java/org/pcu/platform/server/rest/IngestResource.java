package org.pcu.platform.server.rest;

import java.io.InputStream;

import org.pcu.connectors.storage.PcuStorageException;
import org.pcu.platform.Document;
import org.pcu.platform.PcuPlatformException;
import org.pcu.platform.server.service.IngestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IngestResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(IngestResource.class);

	@Autowired
	private IngestService ingestService;

	@RequestMapping(path = "/ingest", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Void> ingestDocument(@RequestBody Document document) {
		LOGGER.debug("ingest metadata document");
		try {
			ingestService.ingestDocument(document);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (PcuPlatformException | PcuStorageException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/ingest/{documentId}", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_OCTET_STREAM_VALUE })
	public ResponseEntity<Void> ingestDocument(@PathVariable String documentId, @RequestBody InputStream document) {
		LOGGER.debug("ingest binary document");
		try {
			ingestService.ingestDocument(document, documentId);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (PcuPlatformException | PcuStorageException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}