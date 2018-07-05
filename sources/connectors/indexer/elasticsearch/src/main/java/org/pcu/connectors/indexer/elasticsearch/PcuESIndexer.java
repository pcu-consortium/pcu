package org.pcu.connectors.indexer.elasticsearch;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.pcu.connectors.indexer.PcuIndexer;
import org.springframework.stereotype.Component;

@Component
public class PcuESIndexer implements PcuIndexer {

	private TransportClient client;

	public PcuESIndexer() throws UnknownHostException {
		client = new PreBuiltTransportClient(Settings.EMPTY);
		// FIXME configurable host and port
		client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
	}

	@Override
	public boolean createDocument(byte[] document, String index, String type, String id) {
		IndexResponse response = client.prepareIndex(index, type, id).setSource(document, XContentType.JSON).get();
		return Result.CREATED.equals(response.getResult());
	}

	@Override
	public boolean deleteDocument(String index, String type, String id) {
		DeleteResponse response = client.prepareDelete(index, type, id).get();
		return Result.DELETED.equals(response.getResult());
	}

	@Override
	public void close() throws Exception {
		client.close();
	}

}
