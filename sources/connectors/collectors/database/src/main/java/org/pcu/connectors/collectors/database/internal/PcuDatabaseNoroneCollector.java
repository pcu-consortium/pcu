package org.pcu.connectors.collectors.database.internal;

import java.io.File;
import java.io.IOException;

import org.pcu.connectors.collectors.api.PcuCollectorConfig;
import org.pcu.connectors.collectors.api.PcuCollectorException;
import org.pcu.platform.client.PcuPlatformClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.norconex.collector.core.CollectorConfigLoader;
import com.norconex.collector.core.crawler.ICrawlerConfig;



public class PcuDatabaseNoroneCollector {
	private static final Logger LOGGER = LoggerFactory.getLogger(PcuDatabaseNoroneCollector.class);


	public void execute(PcuPlatformClient pcuPlatformclient, PcuCollectorConfig config) throws PcuCollectorException {
		LOGGER.debug("Execution start");
	
	
}
}