package org.pcu.connectors.collectors.filesystem.internal;

import java.io.IOException;

import org.pcu.connectors.collectors.api.PcuCollectorException;
import org.pcu.platform.client.PcuPlatformClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.norconex.collector.core.CollectorConfigLoader;
import com.norconex.collector.core.crawler.ICrawlerConfig;
import com.norconex.collector.fs.FilesystemCollector;
import com.norconex.collector.fs.FilesystemCollectorConfig;

public class PcuFilesystemNorconexCollector {

	private static final Logger LOGGER = LoggerFactory.getLogger(PcuFilesystemNorconexCollector.class);

	private String norconexFilesystemConfigXml = "norconex-filesystem-config.xml";

	private String norconexFilesystemConfigVariables = "norconex-filesystem-config.variables";
	
	public void execute(PcuPlatformClient pcuIndexer) throws PcuCollectorException {
		LOGGER.debug("Execution start");
		try {
			Resource norconexFilesystemConfigXmlResource = new ClassPathResource(norconexFilesystemConfigXml);
			Resource norconexFilesystemConfigVariablesResource = new ClassPathResource(
					norconexFilesystemConfigVariables);

			FilesystemCollectorConfig collectorConfig = (FilesystemCollectorConfig) new CollectorConfigLoader(
					FilesystemCollectorConfig.class).loadCollectorConfig(norconexFilesystemConfigXmlResource.getFile(),
							norconexFilesystemConfigVariablesResource.getFile());
			for (ICrawlerConfig crawlerConfig : collectorConfig.getCrawlerConfigs()) {
				if (crawlerConfig.getCommitter() instanceof PcuFilesystemCommitter) {
					((PcuFilesystemCommitter) crawlerConfig.getCommitter()).setPcuIndexer(pcuIndexer);
				}
			}
			FilesystemCollector collector = new FilesystemCollector(collectorConfig);
			collector.start(true);
		} catch (IOException e) {
			throw new PcuCollectorException("Error while starting FileCrawler", e);
		}
	}

}
