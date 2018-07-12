package org.pcu.connectors.collectors.core;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.pcu.connectors.collectors.api.PcuCollector;
import org.pcu.connectors.collectors.api.PcuCollectorException;

public class PcuCollectorOsgi implements BundleActivator, ServiceListener {

	private BundleContext ctx;
	private ServiceReference<PcuCollector> serviceReference;

	public void start(BundleContext bundleContext) {
		System.out.println("Start of activator.");
		this.ctx = bundleContext;
		try {
			ctx.addServiceListener(this, "(objectclass=" + PcuCollector.class.getName() + ")");
			if (ctx.getServiceReference(PcuCollector.class) != null) {
				serviceReference = ctx.getServiceReference(PcuCollector.class);
				if (serviceReference != null) {
					System.out.println(serviceReference.getClass().getName());
				} else {
					System.out.println(" no serviceReference");
					return;
				}
				PcuCollector pcuCollector = ctx.getService(serviceReference);
				System.out.println("has pcu collector ?");
				if (pcuCollector != null) {
					System.out.println("yes it does");
					try {
						pcuCollector.execute();
						System.out.println("did the thing");
					} catch (PcuCollectorException e) {
						System.out.println("something bad happened");
						e.printStackTrace();
					}
				}
			} else {
				System.out.println("nope");
			}
		} catch (InvalidSyntaxException ise) {
			System.out.println("InvalidSyntaxException.");
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
		System.out.println("serviceChanged.");
		int type = serviceEvent.getType();
		switch (type) {
		case (ServiceEvent.REGISTERED):
			System.out.println("Notification of service registered.");
			serviceReference = (ServiceReference<PcuCollector>) serviceEvent.getServiceReference();
			PcuCollector pcuCollector = (PcuCollector) (ctx.getService(serviceReference));
			System.out.println("service is there");
			try {
				pcuCollector.execute();
				System.out.println("did the thing");
			} catch (PcuCollectorException e) {
				System.out.println("something bad happened");
				e.printStackTrace();
			}
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
