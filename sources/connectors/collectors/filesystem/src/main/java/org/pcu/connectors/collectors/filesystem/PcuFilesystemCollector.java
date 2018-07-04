package org.pcu.connectors.collectors.filesystem;

import java.io.IOException;

import org.pcu.connectors.collectors.PcuCollector;
import org.pcu.connectors.indexer.PcuIndexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	/**
	 * Add possibility to override default values
	 */
	private Resource norconexFilesystemConfigXml = new ClassPathResource("norconex-filesystem-config.xml");
	/**
	 * Add possibility to override default values
	 */
	private Resource norconexFilesystemConfigVariables = new ClassPathResource("norconex-filesystem-config.variables");

	@Override
	public void execute() {

		try {
			FilesystemCollectorConfig collectorConfig = (FilesystemCollectorConfig) new CollectorConfigLoader(
					FilesystemCollectorConfig.class).loadCollectorConfig(norconexFilesystemConfigXml.getFile(),
							norconexFilesystemConfigVariables.getFile());
			for(ICrawlerConfig crawlerConfig :collectorConfig.getCrawlerConfigs()){
				if(crawlerConfig.getCommitter() instanceof PcuFilesystemCommitter) {
					((PcuFilesystemCommitter)crawlerConfig.getCommitter()).setPcuIndexer(pcuIndexer);
				}
			}
			FilesystemCollector collector = new FilesystemCollector(collectorConfig);
			collector.start(true);
		} catch (IOException e) {
			LOGGER.error("Error while starting FileCrawler", e);
			throw new RuntimeException(e);
		}

	}

}
