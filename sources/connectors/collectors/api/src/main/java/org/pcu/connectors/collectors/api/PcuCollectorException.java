package org.pcu.connectors.collectors.api;

import java.io.IOException;

public class PcuCollectorException extends Exception {

	public PcuCollectorException(String message, IOException cause) {
		super(message, cause);
	}

}
