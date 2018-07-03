package org.pcu.connectors.collectors.filesystem;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.norconex.collector.core.CollectorConfigLoader;
import com.norconex.collector.fs.FilesystemCollector;
import com.norconex.collector.fs.FilesystemCollectorConfig;

@Component
public class PcuFilesystemCollector {

	private static final Logger LOGGER = LoggerFactory.getLogger(PcuFilesystemCollector.class);

	/**
	 * Add possibility to override default values
	 */
	private Resource norconexFilesystemConfigXml = new ClassPathResource("norconex-filesystem-config.xml");
	/**
	 * Add possibility to override default values
	 */
	private Resource norconexFilesystemConfigVariables = new ClassPathResource("norconex-filesystem-config.variables");

	public void execute() {

		try {
			FilesystemCollectorConfig collectorConfig = (FilesystemCollectorConfig) new CollectorConfigLoader(
					FilesystemCollectorConfig.class).loadCollectorConfig(norconexFilesystemConfigXml.getFile(),
							norconexFilesystemConfigVariables.getFile());
			FilesystemCollector collector = new FilesystemCollector(collectorConfig);
			collector.start(true);
		} catch (IOException e) {
			LOGGER.error("Error while starting FileCrawler", e);
			throw new RuntimeException(e);
		}

	}

}
