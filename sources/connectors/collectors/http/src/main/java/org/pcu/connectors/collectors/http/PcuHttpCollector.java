package org.pcu.connectors.collectors.http;

import org.pcu.connectors.collectors.api.PcuCollector;
import org.pcu.connectors.collectors.api.PcuCollectorConfig;
import org.pcu.connectors.collectors.api.PcuCollectorException;
import org.pcu.connectors.collectors.http.internal.PcuHttpNorconexCollector;
import org.pcu.platform.client.PcuPlatformClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PcuHttpCollector implements PcuCollector {

	private static final Logger LOGGER = LoggerFactory.getLogger(PcuHttpCollector.class);

	public PcuHttpCollector() {
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
		PcuHttpNorconexCollector pcuFilesystemNorconexCollector = new PcuHttpNorconexCollector();
		pcuFilesystemNorconexCollector.execute(pcuPlatformClient, config);

		LOGGER.debug("Execution end");

	}

	@Override
	public String getId() {
		return PcuHttpCollector.class.getName();
	}

}
