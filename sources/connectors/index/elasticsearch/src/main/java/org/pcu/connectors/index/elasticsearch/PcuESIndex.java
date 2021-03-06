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

import org.pcu.connectors.index.PcuIndex;
import org.pcu.connectors.index.PcuIndexConfiguration;
import org.pcu.connectors.index.PcuIndexConfigurationException;
import org.pcu.connectors.index.PcuIndexException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.cluster.Health;
import io.searchbox.core.Delete;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.params.Parameters;

public class PcuESIndex implements PcuIndex {

	private JestClient client;
	private JsonParser jsonParser;

	public PcuESIndex(PcuIndexConfiguration configuration) {
		if (configuration.getConfigutation() == null || !configuration.getConfigutation().has("uri")) {
			throw new PcuIndexConfigurationException("configuration invalid : expected 'uri' parameter");
		}
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(new HttpClientConfig.Builder(configuration.getConfigutation().get("uri").asText())
				.multiThreaded(true).build());
		client = factory.getObject();
		jsonParser = new JsonParser();
	}

	@Override
	public JsonNode getStatus() {
		try {
			Health healthQuery = new Health.Builder().build();
			JestResult result = client.execute(healthQuery);
			return JsonHandler.tranform(result.getJsonObject());
		} catch (IOException e) {
			ObjectNode error = new ObjectMapper().createObjectNode();
			error.put("error", e.getMessage());
			return error;
		}
	}

	@Override
	public boolean createDocument(JsonNode document, String index, String type, String id) throws PcuIndexException {
		try {
			JsonObject objectFromString = jsonParser.parse(document.toString()).getAsJsonObject();
			Index query = new Index.Builder(objectFromString).setParameter(Parameters.OP_TYPE, "create").index(index)
					.type(type).id(id).build();
			JestResult result = client.execute(query);
			return result.isSucceeded();
		} catch (IOException e) {
			throw new PcuIndexException(e);
		}
	}

	@Override
	public boolean deleteDocument(String index, String type, String id) throws PcuIndexException {
		try {
			Delete query = new Delete.Builder(id).index(index).type(type).build();
			JestResult result = client.execute(query);
			return result.isSucceeded();
		} catch (IOException e) {
			throw new PcuIndexException(e);
		}
	}

	@Override
	public JsonNode getDocument(String index, String type, String id) throws PcuIndexException {
		try {
			Get get = new Get.Builder(index, id).type(type).build();
			JestResult result = client.execute(get);
			if (result.isSucceeded()) {
				return JsonHandler.tranform(result.getJsonObject());
			} else {
				throw new PcuIndexException(result.getErrorMessage());
			}
		} catch (IOException e) {
			throw new PcuIndexException(e);
		}
	}

	@Override
	public JsonNode getDocuments(JsonNode searchQuery) throws PcuIndexException {
		try {
			JsonNode query = searchQuery.get("query");
			String index = searchQuery.get("index").asText();
			String type = searchQuery.get("type").asText();
			Search search = new Search.Builder(query.toString()).addIndex(index).addType(type).build();
			SearchResult result = client.execute(search);
			return JsonHandler.tranform(result.getJsonObject());
		} catch (IOException e) {
			throw new PcuIndexException(e);
		}
	}

	@Override
	public boolean createIndex(String index) throws PcuIndexException {
		try {
			CreateIndex query = new CreateIndex.Builder(index).build();
			JestResult result = client.execute(query);
			return result.isSucceeded();
		} catch (IOException e) {
			throw new PcuIndexException(e);
		}
	}

	@Override
	public boolean deleteIndex(String index) throws PcuIndexException {
		try {
			DeleteIndex query = new DeleteIndex.Builder(index).build();
			JestResult result = client.execute(query);
			return result.isSucceeded();
		} catch (IOException e) {
			throw new PcuIndexException(e);
		}
	}

	@Override
	public void close() throws Exception {
		client.close();
	}

}
