package org.pcu.platform.server.rest;

/*-
 * #%L
 * PCU Platform Server
 * %%
 * Copyright (C) 2017 - 2018 PCU Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */





import org.pcu.connectors.index.PcuIndexException;
import org.pcu.platform.server.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping(path = "/indexes/{indexId}/types/{type}/documents")
public class DocumentResource {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentResource.class);

	@Autowired
	private DocumentService documentService;

	@RequestMapping(path = "{documentId}", method = RequestMethod.GET)
	public ResponseEntity<JsonNode> getDocument(@PathVariable String indexId, @PathVariable String type,
			@PathVariable String documentId) {
		LOGGER.debug("get document");
		try {
			JsonNode document = documentService.get(indexId, type, documentId);
			return new ResponseEntity<JsonNode>(document, HttpStatus.OK);
		} catch (PcuIndexException e) {
			LOGGER.error("get document error : {}",e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "{documentId}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteDocument(@PathVariable String indexId, @PathVariable String type,
			@PathVariable String documentId) {
		LOGGER.debug("delete document");
		try {
			documentService.deleteDocument(indexId, type, documentId);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (PcuIndexException e) {
			LOGGER.error("delete document error : {}",e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
