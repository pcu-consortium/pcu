package org.pcu.platform;

public class PcuPlatformException extends Exception {

	public PcuPlatformException(Throwable cause) {
		super(cause);
	}

	public PcuPlatformException(String message) {
		super(message);
	}

	public PcuPlatformException(String message, Throwable cause) {
		super(message, cause);
	}

}
