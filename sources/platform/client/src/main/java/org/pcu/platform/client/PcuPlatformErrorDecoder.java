package org.pcu.platform.client;

import java.io.IOException;

import feign.Response;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;

public class PcuPlatformErrorDecoder implements ErrorDecoder {

	final Decoder decoder;
	final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();

	PcuPlatformErrorDecoder(Decoder decoder) {
		this.decoder = decoder;
	}

	@Override
	public Exception decode(String methodKey, Response response) {
		try {
			return (Exception) decoder.decode(response, PcuPlatformClientException.class);
		} catch (IOException fallbackToDefault) {
			return defaultDecoder.decode(methodKey, response);
		}
	}
}