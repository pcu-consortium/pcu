package org.pcu.platform.server.service;

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





import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.pcu.connectors.storage.PcuStorage;
import org.pcu.connectors.storage.PcuStorageContainerNotFoundException;
import org.pcu.connectors.storage.PcuStorageException;
import org.pcu.platform.Document;
import org.pcu.platform.PcuPlatformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class IngestService {

	private static final Logger LOGGER = LoggerFactory.getLogger(IngestService.class);

	@Autowired
	private KafkaTemplate<String, Document> kafkaTemplateMetadata;

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplateFile;

	@Value("${ingest.topic.metadata}")
	private String ingestTopicMetadata;

	@Value("${ingest.topic.file}")
	private String ingestTopicFile;

	@Value("${ingest.container.name.metadata}")
	private String ingestContainerNameMetadata;

	@Value("${ingest.container.name.file}")
	private String ingestContainerNameFile;

	@Autowired
	private PcuStorage pcuStorage;

	public void ingestDocument(Document document) throws PcuPlatformException, PcuStorageException {
		LOGGER.debug("Ingest document metadata");
		try {
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(document);
			JsonNode jsonNode = mapper.readTree(json);
			byte[] bytes = mapper.writeValueAsBytes(jsonNode);
			ByteArrayInputStream content = new ByteArrayInputStream(bytes);
			LOGGER.debug("Ingest document metadata : store");
			boolean stored = storeDocument(content, ingestContainerNameMetadata, document.getId());
			if (stored) {
				LOGGER.debug("Ingest document metadata : add to pipeline");
				kafkaTemplateMetadata.send(ingestTopicMetadata, document);
			} else {
				throw new PcuPlatformException("Could not ingest document : did not store metadata");
			}
		} catch (IOException e) {
			throw new PcuPlatformException("Could not ingest document : parsing error", e);
		}
	}

	public void ingestDocument(InputStream document, String id) throws PcuPlatformException, PcuStorageException {
		LOGGER.debug("Ingest document file");
		LOGGER.debug("Ingest document file : store");
		boolean stored = storeDocument(document, ingestContainerNameFile, id);
		if (stored) {
			LOGGER.debug("Ingest document file : add to pipeline");
			kafkaTemplateFile.send(ingestTopicFile, id);
		} else {
			throw new PcuPlatformException("Could not ingest document : did not store file");
		}
	}

	private boolean storeDocument(InputStream document, String containerName, String id) throws PcuStorageException {
		try {
			// TODO what to do when not created ?
			return pcuStorage.upload(document, containerName, id);
		} catch (PcuStorageContainerNotFoundException e) {
			pcuStorage.createContainer(containerName);
			return pcuStorage.upload(document, containerName, id);
		}

	}
}
