package org.pcu.connectors.indexer.elasticsearch;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;

import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.pcu.connectors.indexer.PcuIndexer;

public class PcuESIndexer implements PcuIndexer, BundleActivator {


	private ServiceReference<PcuIndexer> reference;
	private ServiceRegistration<PcuIndexer> registration;

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
	public void start(BundleContext context) throws Exception {
		System.out.println("Registering service.");
		registration = context.registerService(PcuIndexer.class, new PcuESIndexer(),
				new Hashtable<String, String>());
		reference = registration.getReference();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		System.out.println("Unregistering service.");
		registration.unregister();
	}

	@Override
	public void close() throws Exception {
		client.close();
	}

}
