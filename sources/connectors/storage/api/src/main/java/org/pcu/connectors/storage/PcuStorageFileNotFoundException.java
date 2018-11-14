package org.pcu.connectors.storage;

public class PcuStorageFileNotFoundException extends PcuStorageException {

	public PcuStorageFileNotFoundException(Throwable cause) {
		super(cause);
	}

	public PcuStorageFileNotFoundException(String message) {
		super(message);
	}

	public PcuStorageFileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
