package org.pcu.connectors.collectors.filesystem;

import org.pcu.connectors.collectors.api.PcuCollector;
import org.pcu.connectors.collectors.api.PcuCollectorConfig;
import org.pcu.connectors.collectors.api.PcuCollectorException;
import org.pcu.connectors.collectors.filesystem.internal.PcuFilesystemNorconexCollector;
import org.pcu.platform.client.PcuPlatformClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PcuFilesystemCollector implements PcuCollector {

	private static final Logger LOGGER = LoggerFactory.getLogger(PcuFilesystemCollector.class);

	public PcuFilesystemCollector() {
		super();
	}

	@Override
	public void execute(PcuPlatformClient pcuPlatformClient, PcuCollectorConfig config) throws PcuCollectorException {
		LOGGER.debug("Execution start");

		if (pcuPlatformClient == null) {
			throw new PcuCollectorException("pcuPlatformClient is mandatory");
		}
		if (config == null) {
			throw new PcuCollectorException("config is mandatory");
		}
		PcuFilesystemNorconexCollector pcuFilesystemNorconexCollector = new PcuFilesystemNorconexCollector();
		pcuFilesystemNorconexCollector.execute(pcuPlatformClient, config);

		LOGGER.debug("Execution end");

	}

	@Override
	public String getId() {
		return PcuFilesystemCollector.class.getName();
	}

}
