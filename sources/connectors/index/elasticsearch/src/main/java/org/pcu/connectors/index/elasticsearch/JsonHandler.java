package org.pcu.connectors.index.elasticsearch;

/*-
 * #%L
 * PCU Index Elasticsearch
 * %%
 * Copyright (C) 2017 - 2018 PCU Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */





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
