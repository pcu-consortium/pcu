package org.pcu.platform.server.rest;

import javax.annotation.PostConstruct;

import org.pcu.connectors.indexer.PcuIndexerException;
import org.pcu.platform.server.service.IndexService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

	private IndexService indexService;
	
	@PostConstruct
	public void init() {
		
	}

	@RequestMapping(path = "/index/{indexId}", method = RequestMethod.POST)
	public ResponseEntity<Void> ingest(@PathVariable String indexId) {
		try {
			indexService.createIndex(indexId);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (PcuIndexerException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}