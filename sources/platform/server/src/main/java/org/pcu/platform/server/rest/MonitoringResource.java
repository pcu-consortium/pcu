package org.pcu.platform.server.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MonitoringResource {

	@RequestMapping(path = "/status", method = RequestMethod.GET)
	public ResponseEntity<Void> status() {
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
