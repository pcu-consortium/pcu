package org.pcu.platform.server.rest;

import org.pcu.connectors.index.PcuIndexException;
import org.pcu.platform.server.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping(path = "/documents")
public class DocumentController {

	@Autowired
	private DocumentService documentService;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<JsonNode> createIndex(@RequestBody JsonNode searchQuery) {
		try {
			documentService.search(searchQuery);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (PcuIndexException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/{indexId}/{documentId}", method = RequestMethod.GET)
	public ResponseEntity<JsonNode> deleteIndex(@PathVariable String indexId, @PathVariable String documentId) {
		try {
			JsonNode document = documentService.get(indexId, documentId);
			return new ResponseEntity<JsonNode>(document, HttpStatus.OK);
		} catch (PcuIndexException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}