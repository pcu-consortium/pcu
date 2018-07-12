package org.pcu.connectors.collectors.filesystem;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.pcu.connectors.collectors.api.PcuCollector;
import org.pcu.platform.client.PcuPlatformClient;

public class PcuFilesystemCollectorOsgi implements BundleActivator {

	private BundleContext ctx;

	private ServiceReference<PcuCollector> reference;
	private ServiceRegistration<PcuCollector> registration;
	
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		System.out.println("Registering service.");
		this.ctx = bundleContext;
		PcuFilesystemCollector pcuFilesystemCollector = new PcuFilesystemCollector(this.ctx, new PcuPlatformClient());
		registration = ctx.registerService(PcuCollector.class, pcuFilesystemCollector, new Hashtable<String, String>());
		reference = registration.getReference();
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		System.out.println("Unregistering service.");
		registration.unregister();
	}
}
