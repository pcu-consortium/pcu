package org.pcu.connectors.indexer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.fasterxml.jackson.databind.JsonNode;

public class PcuESIndexer implements PcuIndexer {

	private TransportClient client;

	public PcuESIndexer() throws UnknownHostException {
		client = new PreBuiltTransportClient(Settings.EMPTY);
		client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
	}

	@Override
	public boolean index(JsonNode document, String index, String type, String id) {
		try {
			IndexResponse response = client.prepareIndex(index, type, id)
					.setSource(document.binaryValue(), XContentType.JSON).get();
			return Result.CREATED.equals(response.getResult());
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public void close() throws Exception {
		client.close();

	}

}
