package org.pcu.platform.client;

public class PcuPlatformClientException extends RuntimeException {

	public PcuPlatformClientException(String message) {
		super(message);
	}

	public PcuPlatformClientException(String message, Throwable cause) {
		super(message, cause);

	}

    public PcuPlatformClientException(Throwable cause) {
        super(cause);
    }

}