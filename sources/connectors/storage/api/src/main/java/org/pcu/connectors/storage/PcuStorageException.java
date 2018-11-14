package org.pcu.connectors.storage;

public class PcuStorageException extends Exception {

	public PcuStorageException(Throwable cause) {
		super(cause);
	}

	public PcuStorageException(String message) {
		super(message);
	}

	public PcuStorageException(String message, Throwable cause) {
		super(message, cause);
	}
}
