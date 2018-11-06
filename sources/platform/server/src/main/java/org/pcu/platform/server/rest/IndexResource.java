package org.pcu.platform.server.rest;

import org.pcu.connectors.index.PcuIndexException;
import org.pcu.platform.server.service.IndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(IndexResource.class);

	@Autowired
	private IndexService indexService;

	@RequestMapping(path = "/{indexId}", method = RequestMethod.POST)
	public ResponseEntity<Void> createIndex(@PathVariable String indexId) {
		LOGGER.debug("create index");
		try {
			indexService.createIndex(indexId);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (PcuIndexException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/{indexId}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteIndex(@PathVariable String indexId) {
		LOGGER.debug("delete index");
		try {
			indexService.deleteIndex(indexId);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (PcuIndexException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}