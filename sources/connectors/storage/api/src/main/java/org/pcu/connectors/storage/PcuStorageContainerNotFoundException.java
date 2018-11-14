package org.pcu.connectors.storage;

public class PcuStorageContainerNotFoundException extends PcuStorageException {

	public PcuStorageContainerNotFoundException(Throwable cause) {
		super(cause);
	}

	public PcuStorageContainerNotFoundException(String message) {
		super(message);
	}

	public PcuStorageContainerNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
