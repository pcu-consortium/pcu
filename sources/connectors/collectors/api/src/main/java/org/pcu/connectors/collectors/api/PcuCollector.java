package org.pcu.connectors.collectors.api;

import org.pcu.platform.client.PcuPlatformClient;

public interface PcuCollector {

	public void execute(PcuPlatformClient pcuPlatformClient, PcuCollectorConfig config) throws PcuCollectorException;

	public String getId();
}
