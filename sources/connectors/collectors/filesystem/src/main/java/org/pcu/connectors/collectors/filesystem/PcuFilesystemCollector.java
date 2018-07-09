package org.pcu.connectors.collectors.filesystem;

import java.io.IOException;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.pcu.connectors.collectors.api.PcuCollector;
import org.pcu.connectors.collectors.api.PcuCollectorException;
import org.pcu.connectors.indexer.PcuIndexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.norconex.collector.core.CollectorConfigLoader;
import com.norconex.collector.core.crawler.ICrawlerConfig;
import com.norconex.collector.fs.FilesystemCollector;
import com.norconex.collector.fs.FilesystemCollectorConfig;

public class PcuFilesystemCollector implements PcuCollector, BundleActivator, ServiceListener {

	private BundleContext ctx;
	private ServiceReference serviceReference;

	private ServiceReference<PcuCollector> reference;
	private ServiceRegistration<PcuCollector> registration;

	private static final Logger LOGGER = LoggerFactory.getLogger(PcuFilesystemCollector.class);

	private PcuIndexer pcuIndexer;

	private String norconexFilesystemConfigXml = "norconex-filesystem-config.xml";

	private String norconexFilesystemConfigVariables = "norconex-filesystem-config.variables";

	@Override
	public void execute() throws PcuCollectorException {
		LOGGER.debug("Execution start");
		try {
			Resource norconexFilesystemConfigXmlResource = new ClassPathResource(norconexFilesystemConfigXml);
			Resource norconexFilesystemConfigVariablesResource = new ClassPathResource(
					norconexFilesystemConfigVariables);

			FilesystemCollectorConfig collectorConfig = (FilesystemCollectorConfig) new CollectorConfigLoader(
					FilesystemCollectorConfig.class).loadCollectorConfig(norconexFilesystemConfigXmlResource.getFile(),
							norconexFilesystemConfigVariablesResource.getFile());
			for (ICrawlerConfig crawlerConfig : collectorConfig.getCrawlerConfigs()) {
				if (crawlerConfig.getCommitter() instanceof PcuFilesystemCommitter) {
					((PcuFilesystemCommitter) crawlerConfig.getCommitter()).setPcuIndexer(pcuIndexer);
				}
			}
			FilesystemCollector collector = new FilesystemCollector(collectorConfig);
			collector.start(true);
		} catch (IOException e) {
			throw new PcuCollectorException("Error while starting FileCrawler", e);
		}
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
			pcuIndexer = (PcuIndexer) (ctx.getService(serviceReference));
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
