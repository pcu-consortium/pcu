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





import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.pcu.connectors.collectors.api.PcuCollectorConfig;
import org.pcu.connectors.collectors.api.PcuCollectorException;
import org.pcu.platform.client.PcuPlatformClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.norconex.collector.core.CollectorConfigLoader;
import com.norconex.collector.core.crawler.ICrawlerConfig;
import com.norconex.collector.fs.FilesystemCollector;
import com.norconex.collector.fs.FilesystemCollectorConfig;
import com.norconex.collector.fs.crawler.FilesystemCrawlerConfig;
import com.norconex.collector.fs.doc.IFileDocumentProcessor;

public class PcuFilesystemNorconexCollector {

	private static final Logger LOGGER = LoggerFactory.getLogger(PcuFilesystemNorconexCollector.class);

	public static final String EXTERNAL_CONFIG_XML_KEY = "norconexfilesystem.config.xml";
	public static final String EXTERNAL_CONFIG_VARIABLES_KEY = "norconexfilesystem.config.variables";

	private static final String INTERNAL_CONFIG_XML_PATH = "config/internal-norconex-filesystem-config.xml";
	private static final String INTERNAL_CONFIG_VARIABLES_PATH = "config/internal-norconex-filesystem-config.variables";

	public void execute(PcuPlatformClient pcuPlatformclient, PcuCollectorConfig config) throws PcuCollectorException {
		LOGGER.debug("Execution start");
		try {
			File norconexFilesystemConfigXmlFile = loadConfigXml(config);
			File norconexFilesystemConfigVariablesFile = loadConfigVariables(config);

			FilesystemCollectorConfig collectorConfig = (FilesystemCollectorConfig) new CollectorConfigLoader(
					FilesystemCollectorConfig.class).loadCollectorConfig(norconexFilesystemConfigXmlFile,
							norconexFilesystemConfigVariablesFile);
			if (collectorConfig == null) {
				throw new PcuCollectorException("Collector configuration could not be instanciated");
			}

			for (ICrawlerConfig crawlerConfig : collectorConfig.getCrawlerConfigs()) {
				// configure commiter
				if (crawlerConfig.getCommitter() instanceof PcuFilesystemCommitter) {
					((PcuFilesystemCommitter) crawlerConfig.getCommitter()).setPcuPlatformClient(pcuPlatformclient);
					((PcuFilesystemCommitter) crawlerConfig.getCommitter()).setPcuCollectorConfig(config);
				}
				// configure postprocessor
				if (crawlerConfig instanceof FilesystemCrawlerConfig) {
					IFileDocumentProcessor[] postProcessors = ((FilesystemCrawlerConfig) crawlerConfig)
							.getPostImportProcessors();
					if (postProcessors != null) {
						for (IFileDocumentProcessor postProcessor : postProcessors) {
							if (postProcessor instanceof PcuFilesystemSendFilePostProcessor) {
								((PcuFilesystemSendFilePostProcessor) postProcessor).setPcuPlatformClient(pcuPlatformclient);
								((PcuFilesystemSendFilePostProcessor) postProcessor).setPcuCollectorConfig(config);
							}
						}
					}

				}
			}
			FilesystemCollector collector = new FilesystemCollector(collectorConfig);
			collector.start(true);
		} catch (IOException e) {
			throw new PcuCollectorException("Error while starting FileCrawler", e);
		}
	}

	private File loadConfigXml(PcuCollectorConfig config) throws PcuCollectorException {
		LOGGER.debug("Loading xml configuration");
		if (config.any().get(EXTERNAL_CONFIG_XML_KEY) != null) {
			LOGGER.debug("Loading xml configuration : using external configuration");
			return new File(config.any().get(EXTERNAL_CONFIG_XML_KEY));
		} else {
			LOGGER.debug("Loading xml configuration : using internal configuration");
			return generateTmpFileFromBundleFile(INTERNAL_CONFIG_XML_PATH, "norconex-filesystem-config", ".xml");
		}
	}

	private File loadConfigVariables(PcuCollectorConfig config) throws PcuCollectorException {
		LOGGER.debug("Loading variables configuration");
		if (config.any().get(EXTERNAL_CONFIG_VARIABLES_KEY) != null) {
			LOGGER.debug("Loading variables configuration : using external configuration");
			System.out.println("Loading variables configuration : using external configuration");
			return new File(config.any().get(EXTERNAL_CONFIG_VARIABLES_KEY));
		} else {
			LOGGER.debug("Loading variables configuration : using internal configuration");
			System.out.println("Loading variables configuration : using internal configuration");
			return generateTmpFileFromBundleFile(INTERNAL_CONFIG_VARIABLES_PATH, "norconex-filesystem-config",
					".variables");
		}
	}

	private File generateTmpFileFromBundleFile(String sourceFile, String targetFilename, String targetFileExt)
			throws PcuCollectorException {
		try {
			File tmpFile = File.createTempFile(targetFilename, targetFileExt);
			InputStream is = PcuFilesystemNorconexCollector.class.getClassLoader().getResourceAsStream(sourceFile);
			FileUtils.copyInputStreamToFile(is, tmpFile);
			return tmpFile;
		} catch (IOException e) {
			throw new PcuCollectorException("Could not copy bundle file to temp file", e);
		}
	}

}
