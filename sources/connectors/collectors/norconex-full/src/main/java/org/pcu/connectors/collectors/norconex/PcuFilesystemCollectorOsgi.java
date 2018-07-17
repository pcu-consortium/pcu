package org.pcu.connectors.collectors.norconex;

import javax.xml.validation.SchemaFactory;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.norconex.commons.lang.config.XMLConfigurationUtil;

public class PcuFilesystemCollectorOsgi implements BundleActivator {

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		System.out.println("Start.");
		
		System.setProperty("jaxp.debug", "true");
		SchemaFactory.newInstance("http://www.w3.org/XML/XMLSchema/v1.0");
		PcuFilesystemCollector pcuFilesystemCollector = new PcuFilesystemCollector(bundleContext);
		pcuFilesystemCollector.execute();
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		System.out.println("Stop.");
	}
}
