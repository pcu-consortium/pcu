package org.pcu.connectors.collectors.norconex;

import java.util.Dictionary;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class PcuFilesystemCollectorOsgi implements BundleActivator {

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		System.out.println("Start.");
		//Dictionary<String, String> dict = bundleContext.getBundle().getHeaders();
//		dict.put("javax.xml.validation.SchemaFactory:http://www.w3.org/XML/XMLSchema/v1.0",
//				"org.apache.xerces.jaxp.validation.XMLSchemaFactory");
//		dict.put("javax.xml.validation.SchemaFactory:http://www.w3.org/XML/XMLSchema/v1.1",
//				"org.apache.xerces.jaxp.validation.XMLSchema11Factory");
//		System.setProperty("jaxp.debug", "true");
		//SchemaFactory.newInstance("http://www.w3.org/XML/XMLSchema/v1.1");
		//SchemaFactory.newInstance("http://www.w3.org/XML/XMLSchema/v1.1");
				
		
		Thread thread = Thread.currentThread();
		ClassLoader loader = thread.getContextClassLoader();
		thread.setContextClassLoader(this.getClass().getClassLoader());
		try {
			PcuFilesystemCollector pcuFilesystemCollector = new PcuFilesystemCollector(bundleContext);
			pcuFilesystemCollector.execute();
		} finally {
		  thread.setContextClassLoader(loader);
		}
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		System.out.println("Stop.");
	}
}
