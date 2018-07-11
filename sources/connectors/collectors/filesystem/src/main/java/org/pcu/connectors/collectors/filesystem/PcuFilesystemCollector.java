package org.pcu.connectors.collectors.filesystem;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.pcu.connectors.collectors.api.PcuCollector;
import org.pcu.connectors.collectors.api.PcuCollectorException;
import org.pcu.connectors.collectors.filesystem.internal.PcuFilesystemNorconexCollector;
import org.pcu.platform.client.PcuPlatformClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PcuFilesystemCollector implements PcuCollector, BundleActivator, ServiceListener {

	private BundleContext ctx;
	private ServiceReference serviceReference; 

	private ServiceReference<PcuCollector> reference;
	private ServiceRegistration<PcuCollector> registration;

	private static final Logger LOGGER = LoggerFactory.getLogger(PcuFilesystemCollector.class);

	private PcuPlatformClient pcuIndexer;

	public PcuFilesystemCollector() {
		LOGGER.debug("init bean");
	}

	@Override
	public void execute() throws PcuCollectorException {
		LOGGER.debug("Execution start");
		PcuFilesystemNorconexCollector pcuFilesystemNorconexCollector = new PcuFilesystemNorconexCollector();
		pcuFilesystemNorconexCollector.execute(pcuIndexer);
	}

	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println("Registering service.");
		registration = context.registerService(PcuCollector.class, new PcuFilesystemCollector(),
				new Hashtable<String, String>());
		reference = registration.getReference();

	}

	@Override
	public void stop(BundleContext context) throws Exception {
		System.out.println("Unregistering service.");
		registration.unregister();
	}

	@Override
	public void serviceChanged(ServiceEvent serviceEvent) {
		System.out.println("serviceChanged.");
		int type = serviceEvent.getType();
		switch (type) {
		case (ServiceEvent.REGISTERED):
			System.out.println("Notification of service registered.");
			serviceReference = serviceEvent.getServiceReference();
			pcuIndexer = (PcuPlatformClient) (ctx.getService(serviceReference));
			System.out.println("service is there");
			// pcuCollector.execute();
			break;
		case (ServiceEvent.UNREGISTERING):
			System.out.println("Notification of service unregistered.");
			ctx.ungetService(serviceEvent.getServiceReference());
			pcuIndexer = null;
			break;
		default:
			break;
		}

	}

}
