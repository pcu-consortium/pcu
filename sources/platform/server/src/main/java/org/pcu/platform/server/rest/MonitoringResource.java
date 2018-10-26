package org.pcu.platform.server.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MonitoringResource {

	@Autowired
	private KafkaAdmin admin;

	@RequestMapping(path = "/status", method = RequestMethod.GET)
	public ResponseEntity<Void> status() {
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(path = "/configuration", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> configuration() {
		return new ResponseEntity<Map<String, Object>>(admin.getConfig(), HttpStatus.OK);
	}

}
