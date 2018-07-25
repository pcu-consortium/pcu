package org.pcu.connectors.collectors.filesystem;

import javax.xml.validation.SchemaFactory;

import org.pcu.connectors.collectors.api.PcuCollector;
import org.pcu.connectors.collectors.api.PcuCollectorException;
import org.pcu.connectors.collectors.filesystem.internal.PcuFilesystemNorconexCollector;
import org.pcu.platform.client.PcuPlatformClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PcuFilesystemCollector implements PcuCollector {

	private static final Logger LOGGER = LoggerFactory.getLogger(PcuFilesystemCollector.class);

	private PcuPlatformClient pcuIndexer;

	public PcuFilesystemCollector() {
		super();
	}
	
	public PcuFilesystemCollector(PcuPlatformClient pcuIndexer) {
		super();
		this.pcuIndexer = pcuIndexer;
	}

	@Override
	public void execute() throws PcuCollectorException {
		LOGGER.debug("Execution start");
		System.out.println("Execution start");
		PcuFilesystemNorconexCollector pcuFilesystemNorconexCollector = new PcuFilesystemNorconexCollector();
		System.out.println(SchemaFactory.class.isLocalClass());
		System.out.println(SchemaFactory.class.toString());
		System.out.println("is context there ?");
		pcuFilesystemNorconexCollector.execute(pcuIndexer);
	}

	@Override
	public String getId() {
		return PcuFilesystemCollector.class.getName();
	}

}
