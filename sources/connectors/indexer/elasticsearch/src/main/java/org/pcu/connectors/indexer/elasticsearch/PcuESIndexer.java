package org.pcu.connectors.indexer.elasticsearch;

import java.io.IOException;

import org.pcu.connectors.indexer.PcuIndexer;
import org.pcu.connectors.indexer.PcuIndexerException;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;

public class PcuESIndexer implements PcuIndexer {

	private JestClient client;
	private JsonParser jsonParser;

	private PcuESIndexer(Builder builder) {
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(new HttpClientConfig.Builder(builder.getUri()).multiThreaded(true).build());
		client = factory.getObject();
		jsonParser = new JsonParser();
	}

	@Override
	public boolean createDocument(JsonNode document, String index, String type, String id) throws PcuIndexerException {
		try {
			JsonObject objectFromString = jsonParser.parse(document.toString()).getAsJsonObject();
			Index query = new Index.Builder(objectFromString).index(index).type(type).build();
			JestResult result = client.execute(query);
			return result.isSucceeded();
		} catch (IOException e) {
			throw new PcuIndexerException(e);
		}
	}

	@Override
	public boolean deleteDocument(String index, String type, String id) throws PcuIndexerException {
		try {
			Delete query = new Delete.Builder(id).index(index).type(type).build();
			JestResult result = client.execute(query);
			return result.isSucceeded();
		} catch (IOException e) {
			throw new PcuIndexerException(e);
		}
	}

	@Override
	public boolean createIndex(String index) throws PcuIndexerException {
		try {
			CreateIndex query = new CreateIndex.Builder(index).build();
			JestResult result = client.execute(query);
			return result.isSucceeded();
		} catch (IOException e) {
			throw new PcuIndexerException(e);
		}
	}

	@Override
	public boolean deleteIndex(String index) throws PcuIndexerException {
		try {
			DeleteIndex query = new DeleteIndex.Builder(index).build();
			JestResult result = client.execute(query);
			return result.isSucceeded();
		} catch (IOException e) {
			throw new PcuIndexerException(e);
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

		public PcuIndexer build() {
			PcuIndexer result = new PcuESIndexer(this);
			return result;
		}
	}

}
