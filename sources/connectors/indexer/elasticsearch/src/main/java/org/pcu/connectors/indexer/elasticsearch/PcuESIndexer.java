package org.pcu.connectors.indexer.elasticsearch;

import java.io.IOException;

import org.pcu.connectors.indexer.PcuIndexer;
import org.pcu.connectors.indexer.PcuIndexerException;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import io.searchbox.indices.CreateIndex;

public class PcuESIndexer implements PcuIndexer {

	private JestClient client;

	public PcuESIndexer() {
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(new HttpClientConfig.Builder("http://localhost:9200").multiThreaded(true).build());
		client = factory.getObject();
	}

	@Override
	public boolean createDocument(byte[] document, String index, String type, String id) throws PcuIndexerException {
		try {
			Index query = new Index.Builder(document).index(index).type(type).build();
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
	public void close() throws Exception {
		client.close();
	}

	@Override
	public String getIndexerId() {
		return "ES5";
	}

}
