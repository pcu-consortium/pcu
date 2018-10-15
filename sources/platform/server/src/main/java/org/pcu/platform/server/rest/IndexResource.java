package org.pcu.platform.server.rest;

import org.pcu.connectors.index.PcuIndexException;
import org.pcu.platform.server.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/indexes")
public class IndexResource {

	@Autowired
	private IndexService indexService;

	@RequestMapping(path = "/{indexId}", method = RequestMethod.POST)
	public ResponseEntity<Void> createIndex(@PathVariable String indexId) {
		try {
			indexService.createIndex(indexId);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (PcuIndexException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/{indexId}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteIndex(@PathVariable String indexId) {
		try {
			indexService.deleteIndex(indexId);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (PcuIndexException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}