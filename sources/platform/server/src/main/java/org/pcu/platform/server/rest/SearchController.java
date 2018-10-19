package org.pcu.platform.server.rest;

import org.pcu.connectors.index.PcuIndexException;
import org.pcu.platform.server.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping(path = "/search")
public class SearchController {

	@Autowired
	private DocumentService documentService;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<JsonNode> search(@RequestBody JsonNode searchQuery) {
		try {
			JsonNode result = documentService.search(searchQuery);
			return new ResponseEntity<JsonNode>(result, HttpStatus.OK);
		} catch (PcuIndexException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}