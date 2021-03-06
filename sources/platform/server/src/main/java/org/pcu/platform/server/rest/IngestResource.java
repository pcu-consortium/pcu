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





import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.pcu.connectors.storage.PcuStorageException;
import org.pcu.platform.Document;
import org.pcu.platform.PcuPlatformException;
import org.pcu.platform.server.service.IngestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IngestResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(IngestResource.class);

	@Autowired
	private IngestService ingestService;

	@RequestMapping(path = "/ingest", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Void> ingestDocument(@RequestBody Document document) {
		LOGGER.debug("ingest metadata document");
		try {
			ingestService.ingestDocument(document);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (PcuPlatformException | PcuStorageException e) {
			LOGGER.error("ingest metadata document error : {}", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/ingest/{documentId}", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_OCTET_STREAM_VALUE })
	public ResponseEntity<Void> ingestDocument(@PathVariable String documentId, final HttpServletRequest request) {
		LOGGER.debug("ingest binary document");

		try (final InputStream is = getInputStream(request)) {
			if (is != null) {
				ingestService.ingestDocument(is, documentId);
			}
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (PcuPlatformException | PcuStorageException | IOException | FileUploadException e) {
			LOGGER.error("ingest binary document error : {}", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private InputStream getInputStream(final HttpServletRequest request) throws IOException, FileUploadException {
		return request.getInputStream();
	}
}
