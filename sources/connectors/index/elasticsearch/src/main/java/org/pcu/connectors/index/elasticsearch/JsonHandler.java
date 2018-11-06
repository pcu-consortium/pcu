package org.pcu.connectors.index.elasticsearch;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class JsonHandler {
	private static final JsonParser jsonParser;
	private static final ObjectMapper objectMapper;

	static {
		jsonParser = new JsonParser();
		objectMapper = new ObjectMapper();
	}

	public JsonHandler() {
		// empty constructor
	}

	public static final JsonObject tranform(JsonNode jsonNode) {
		return jsonParser.parse(jsonNode.toString()).getAsJsonObject();
	}

	public static final JsonNode tranform(JsonObject jsonObject) throws IOException {
		return objectMapper.readTree(jsonObject.toString());
	}

}
