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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.pcu.connectors.collectors.api.PcuCollectorConfig;
import org.pcu.platform.client.PcuPlatformClient;

import com.norconex.collector.core.CollectorException;
import com.norconex.collector.fs.doc.FileDocument;
import com.norconex.collector.fs.doc.IFileDocumentProcessor;

public class PcuFilesystemSendFilePostProcessor implements IFileDocumentProcessor {

	private static final Logger LOGGER = LogManager.getLogger(PcuFilesystemSendFilePostProcessor.class);

	private PcuPlatformClient pcuPlatformclient;
	private PcuCollectorConfig pcuCollectorConfig;

	@Override
	public void processDocument(FileSystemManager fileManager, FileDocument doc) {
		LOGGER.debug("Post process document with reference " + doc.getReference());
		try {
			FileObject fileObject = fileManager.resolveFile(doc.getReference());
			FileContent file = fileObject.getContent();
			String documentId = DigestUtils.md5Hex(pcuCollectorConfig.getDatasourceId() + doc.getReference());
			try (InputStream is = file.getInputStream()) {
				pcuPlatformclient.ingest(documentId, is);
			} catch (IOException e) {
				throw new CollectorException("Cannot read content file for reference: " + doc.getReference(), e);
			}
		} catch (FileSystemException e) {
			throw new CollectorException("Cannot retrieve document for reference: " + doc.getReference(), e);
		}
	}

	public void setPcuPlatformClient(PcuPlatformClient pcuPlatformclient) {
		this.pcuPlatformclient = pcuPlatformclient;
	}

	public void setPcuCollectorConfig(PcuCollectorConfig pcuCollectorConfig) {
		this.pcuCollectorConfig = pcuCollectorConfig;
	}
}
