package org.pcu.connectors.collectors.core;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.pcu.connectors.collectors.api.PcuCollector;

public class PcuCollectorOsgi implements BundleActivator, ServiceListener {

	private BundleContext ctx;
	private ServiceReference serviceReference;

	public void start(BundleContext ctx) {
		System.out.println("Start of activator.");
		this.ctx = ctx;
		try {
			ctx.addServiceListener(this, "(objectclass=" + PcuCollector.class.getName() + ")");
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
	}

	public void stop(BundleContext bundleContext) {
		System.out.println("Stop of activator.");
		if (serviceReference != null) {
			ctx.ungetService(serviceReference);
		}
		this.ctx = null;
	}

	@Override
	public void serviceChanged(ServiceEvent serviceEvent) {
		int type = serviceEvent.getType();
		switch (type) {
		case (ServiceEvent.REGISTERED):
			System.out.println("Notification of service registered.");
			serviceReference = serviceEvent.getServiceReference();
			PcuCollector pcuCollector = (PcuCollector) (ctx.getService(serviceReference));
			System.out.println("service is there");
			// pcuCollector.execute();
			break;
		case (ServiceEvent.UNREGISTERING):
			System.out.println("Notification of service unregistered.");
			ctx.ungetService(serviceEvent.getServiceReference());
			break;
		default:
			break;
		}

	}

}
