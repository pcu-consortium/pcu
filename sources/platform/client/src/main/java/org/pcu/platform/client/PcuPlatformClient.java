package org.pcu.platform.client;

import feign.Feign;
import feign.Logger;
import feign.Param;
import feign.RequestLine;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

public interface PcuPlatformClient {

	@RequestLine("POST document")
	boolean createDocument(@Param("document") byte[] document, @Param("index") String index, @Param("type") String type,
			@Param("id") String id);

	@RequestLine("DELETE document")
	boolean deleteDocument(@Param("index") String index, @Param("type") String type, @Param("id") String id);

	static PcuPlatformClient connect(String url) {
		Encoder encoder = new JacksonEncoder();
		Decoder decoder = new JacksonDecoder();
		return Feign.builder().decoder(decoder).encoder(encoder).errorDecoder(new PcuPlatformErrorDecoder(decoder))
				.logger(new Logger.ErrorLogger()).logLevel(Logger.Level.BASIC).target(PcuPlatformClient.class, url);
	}
}