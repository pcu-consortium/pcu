package org.pcu.connectors.collectors.filesystem.internal;

/*-
 * #%L
 * PCU Collector Filesystem
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
import java.io.StringWriter;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.pcu.connectors.collectors.api.PcuCollectorConfig;
import org.pcu.platform.Document;
import org.pcu.platform.client.PcuPlatformClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.norconex.committer.core.CommitterException;
import com.norconex.committer.core.ICommitter;
import com.norconex.commons.lang.map.Properties;

public class PcuFilesystemCommitter implements ICommitter {

	private static final Logger LOGGER = LogManager.getLogger(PcuFilesystemCommitter.class);

	private PcuPlatformClient pcuPlatformclient;
	private PcuCollectorConfig pcuCollectorConfig;

	@Override
	public void add(String reference, InputStream content, Properties metadata) {
		addDocumentToPcu(reference, content, metadata);
	}

	@Override
	public void remove(String reference, Properties metadata) {
		removeDocumentToPcu(reference, metadata);
	}

	@Override
	public void commit() {
		// nothing to do, yolo
	}

	private void removeDocumentToPcu(String reference, Properties metadata) {
		LOGGER.info("Commit remove document with reference " + reference);

		String documentId = DigestUtils.md5Hex(pcuCollectorConfig.getDatasourceId() + reference);

		PcuFilesystemDocument doc = new PcuFilesystemDocument();
		doc.setId(documentId);
		doc.setReference(reference);
		doc.setIndex("documents");
		doc.setType("document");

		LOGGER.debug("Id: " + doc.getId());
		LOGGER.debug("Reference: " + doc.getReference());
		LOGGER.debug("Index: " + doc.getIndex());
		LOGGER.debug("Type: " + doc.getType());

		Document document = new Document();
		document.setId(doc.getId());
		document.setIndex(doc.getIndex());
		document.setType(doc.getType());
		pcuPlatformclient.deleteDocument(doc.getIndex(), doc.getType(), doc.getId());
	}

	private void addDocumentToPcu(String reference, InputStream content, Properties metadata) {
		LOGGER.info("Commit add document with reference " + reference);

		try {

			String documentId = DigestUtils.md5Hex(pcuCollectorConfig.getDatasourceId() + reference);

			PcuFilesystemDocument doc = new PcuFilesystemDocument();
			doc.setId(documentId);
			doc.setReference(reference);
			doc.setIndex("documents");
			doc.setType("document");

			StringWriter writer = new StringWriter();
			metadata.storeToJSON(writer);
			doc.setMetadata(new ObjectMapper().readTree(writer.toString()));

			LOGGER.debug("Id: " + doc.getId());
			LOGGER.debug("Reference: " + doc.getReference());
			LOGGER.debug("Index: " + doc.getIndex());
			LOGGER.debug("Type: " + doc.getType());
			if (doc != null && doc.getMetadata() != null) {
				LOGGER.debug("Metadatas: " + doc.getMetadata().toString());
			}

			Document document = new Document();
			document.setId(doc.getId());
			document.setIndex(doc.getIndex());
			document.setType(doc.getType());
			document.setDocument(doc.getMetadata());
			pcuPlatformclient.ingest(document);
		} catch (IOException e) {
			throw new CommitterException("Cannot create Json document to add for reference: " + reference, e);
		}
	}

	public void setPcuPlatformClient(PcuPlatformClient pcuPlatformclient) {
		this.pcuPlatformclient = pcuPlatformclient;
	}

	public void setPcuCollectorConfig(PcuCollectorConfig pcuCollectorConfig) {
		this.pcuCollectorConfig = pcuCollectorConfig;
	}

}
