package org.pcu.connectors.collectors.filesystem;

import org.osgi.framework.BundleContext;
import org.pcu.connectors.collectors.api.PcuCollector;
import org.pcu.connectors.collectors.api.PcuCollectorException;
import org.pcu.connectors.collectors.filesystem.internal.PcuFilesystemNorconexCollector;
import org.pcu.platform.client.PcuPlatformClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PcuFilesystemCollector implements PcuCollector {

	private static final Logger LOGGER = LoggerFactory.getLogger(PcuFilesystemCollector.class);

	private BundleContext ctx;
	private PcuPlatformClient pcuIndexer;

	public PcuFilesystemCollector(BundleContext ctx, PcuPlatformClient pcuIndexer) {
		super();
		this.ctx = ctx;
		this.pcuIndexer = pcuIndexer;
	}

	@Override
	public void execute() throws PcuCollectorException {
		LOGGER.debug("Execution start");
		System.out.println("Execution start");
		PcuFilesystemNorconexCollector pcuFilesystemNorconexCollector = new PcuFilesystemNorconexCollector();
		System.out.println("is context there ?");
		pcuFilesystemNorconexCollector.execute(pcuIndexer, ctx);
	}

}
