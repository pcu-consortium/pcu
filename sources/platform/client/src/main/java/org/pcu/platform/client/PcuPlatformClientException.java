package org.pcu.platform.client;

public class PcuPlatformClientException extends RuntimeException {
	private String message; // parsed from json

	@Override
	public String getMessage() {
		return message;
	}
}