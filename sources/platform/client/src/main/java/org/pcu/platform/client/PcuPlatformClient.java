package org.pcu.platform.client;

import org.pcu.platform.IngestDocumentRequest;

import com.fasterxml.jackson.databind.JsonNode;

import feign.Feign;
import feign.Headers;
import feign.Logger;
import feign.Param;
import feign.RequestLine;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

public interface PcuPlatformClient {

	@RequestLine("GET status")
	void status();

	@RequestLine("POST indexes/{indexId}")
	void createIndex(@Param("indexId") String indexId);

	@RequestLine("DELETE indexes/{indexId}")
	void deleteIndex(@Param("indexId") String indexId);

	@RequestLine("POST ingest")
	@Headers("Content-Type: application/json")
	void ingest(IngestDocumentRequest ingestDocumentRequest);

	@RequestLine("GET indexes/{indexId}/types/{type}/documents/{documentId}")
	JsonNode getDocument(@Param("indexId") String indexId, @Param("type") String type,
			@Param("documentId") String documentId);
	
	@RequestLine("DELETE indexes/{indexId}/types/{type}/documents/{documentId}")
	void deleteDocument(@Param("indexId") String indexId, @Param("type") String type,
			@Param("documentId") String documentId);

	static PcuPlatformClient connect(String url) {
		Encoder encoder = new JacksonEncoder();
		Decoder decoder = new JacksonDecoder();
		return Feign.builder().decoder(decoder).encoder(encoder).errorDecoder(new PcuPlatformErrorDecoder(decoder))
				.logger(new Logger.ErrorLogger()).logLevel(Logger.Level.BASIC).target(PcuPlatformClient.class, url);
	}


}