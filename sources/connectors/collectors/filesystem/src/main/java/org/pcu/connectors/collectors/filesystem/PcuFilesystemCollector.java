package org.pcu.connectors.collectors.filesystem;

import java.io.IOException;

import org.pcu.connectors.collectors.PcuCollector;
import org.pcu.connectors.collectors.PcuCollectorException;
import org.pcu.connectors.indexer.PcuIndexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.norconex.collector.core.CollectorConfigLoader;
import com.norconex.collector.core.crawler.ICrawlerConfig;
import com.norconex.collector.fs.FilesystemCollector;
import com.norconex.collector.fs.FilesystemCollectorConfig;

@Component
public class PcuFilesystemCollector implements PcuCollector {

	private static final Logger LOGGER = LoggerFactory.getLogger(PcuFilesystemCollector.class);

	@Autowired
	private PcuIndexer pcuIndexer;

	@Value("${norconex.filesystem.config.xml.path:norconex-filesystem-config.xml}")
	private String norconexFilesystemConfigXml;

	@Value("${norconex.filesystem.config.variables.path:norconex-filesystem-config.variables}")
	private String norconexFilesystemConfigVariables;

	@Override
	public void execute() throws PcuCollectorException {
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
