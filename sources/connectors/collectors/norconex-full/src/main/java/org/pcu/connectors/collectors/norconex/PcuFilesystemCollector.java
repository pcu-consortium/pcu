package org.pcu.connectors.collectors.norconex;

import org.osgi.framework.BundleContext;
import org.pcu.connectors.collectors.norconex.internal.PcuFilesystemNorconexCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PcuFilesystemCollector {

	private static final Logger LOGGER = LoggerFactory.getLogger(PcuFilesystemCollector.class);

	private BundleContext ctx;

	public PcuFilesystemCollector(BundleContext ctx) {
		super();
		this.ctx = ctx;
	}

	public void execute() throws Exception {
		LOGGER.debug("Execution start");
		System.out.println("Execution start");
		PcuFilesystemNorconexCollector pcuFilesystemNorconexCollector = new PcuFilesystemNorconexCollector();
		System.out.println("is context there ?");
		pcuFilesystemNorconexCollector.execute(ctx);
	}

}
