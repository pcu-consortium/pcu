package org.pcu.connectors.index.elasticsearch;

import java.io.IOException;

import org.pcu.connectors.index.PcuIndex;
import org.pcu.connectors.index.PcuIndexException;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
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

	private PcuESIndex(Builder builder) {
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(new HttpClientConfig.Builder(builder.getUri()).multiThreaded(true).build());
		client = factory.getObject();
		jsonParser = new JsonParser();
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
			return JsonHandler.tranform(result.getJsonObject());
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

	public static class Builder {

		private String uri;

		public Builder(String uri) {
			this.uri = uri;
		}

		public String getUri() {
			return uri;
		}

		public PcuIndex build() {
			PcuIndex result = new PcuESIndex(this);
			return result;
		}
	}

}
