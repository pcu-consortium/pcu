package org.pcu.connectors.collectors;

import java.io.IOException;

public class PcuCollectorException extends Exception {

	public PcuCollectorException(String message, IOException cause) {
		super(message, cause);
	}

}
