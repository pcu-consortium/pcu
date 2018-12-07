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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping(path = "/search")
public class SearchController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

	@Autowired
	private DocumentService documentService;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<JsonNode> search(@RequestBody JsonNode searchQuery) {
		LOGGER.debug("search");
		try {
			JsonNode result = documentService.search(searchQuery);
			return new ResponseEntity<JsonNode>(result, HttpStatus.OK);
		} catch (PcuIndexException e) {
			LOGGER.error("search error : {}", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
