package org.pcu.platform.server.ingest.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IngestController {

	@RequestMapping(path = "/document", method = RequestMethod.POST)
	public ResponseEntity<Void> ingest(@RequestBody IngestRequest ingestRequest) {
		// TODO working code
		return new ResponseEntity<>(HttpStatus.OK);
	}
}