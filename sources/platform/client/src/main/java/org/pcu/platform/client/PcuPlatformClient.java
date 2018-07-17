package org.pcu.platform.client;

import java.io.IOException;

import feign.Feign;
import feign.Logger;
import feign.RequestLine;
import feign.Response;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import feign.gson.GsonDecoder;

public class PcuPlatformClient {

	interface PCUPlateform {

		@RequestLine("POST document")
		boolean createDocument(byte[] document, String index, String type, String id);

		@RequestLine("DELETE document")
		boolean deleteDocument(String index, String type, String id);

		static PCUPlateform connect(String url) {
			Decoder decoder = new GsonDecoder();
			return Feign.builder().decoder(decoder).errorDecoder(new PCUPlateformErrorDecoder(decoder))
					.logger(new Logger.ErrorLogger()).logLevel(Logger.Level.BASIC).target(PCUPlateform.class, url);
		}
	}

	static class PCUPlateformErrorDecoder implements ErrorDecoder {

		final Decoder decoder;
		final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();

		PCUPlateformErrorDecoder(Decoder decoder) {
			this.decoder = decoder;
		}

		@Override
		public Exception decode(String methodKey, Response response) {
			try {
				return (Exception) decoder.decode(response, PCUPlateformClientError.class);
			} catch (IOException fallbackToDefault) {
				return defaultDecoder.decode(methodKey, response);
			}
		}
	}

	static class PCUPlateformClientError extends RuntimeException {
		private String message; // parsed from json

		@Override
		public String getMessage() {
			return message;
		}
	}
}
