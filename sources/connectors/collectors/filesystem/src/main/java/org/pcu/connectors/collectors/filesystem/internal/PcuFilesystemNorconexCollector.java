package org.pcu.connectors.collectors.filesystem.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.pcu.connectors.collectors.api.PcuCollectorException;
import org.pcu.platform.client.PcuPlatformClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.norconex.collector.core.CollectorConfigLoader;
import com.norconex.collector.core.crawler.ICrawlerConfig;
import com.norconex.collector.fs.FilesystemCollector;
import com.norconex.collector.fs.FilesystemCollectorConfig;

public class PcuFilesystemNorconexCollector {

	private static final Logger LOGGER = LoggerFactory.getLogger(PcuFilesystemNorconexCollector.class);

	private String norconexFilesystemConfigXml = "/config/norconex-filesystem-config.xml";
	private String norconexFilesystemConfigVariables = "/config/norconex-filesystem-config.variables";

	public void execute(PcuPlatformClient pcuIndexer, BundleContext bundleContext) throws PcuCollectorException {
		LOGGER.debug("Execution start");
		try {
			Bundle bundle = bundleContext.getBundle();
			System.out.println(bundle.getBundleId());
			File norconexFilesystemConfigXmlFile = generateTmpFileFromBundleFile(bundleContext,
					norconexFilesystemConfigXml, "norconex-filesystem-config", "xml");
			File norconexFilesystemConfigVariablesFile = generateTmpFileFromBundleFile(bundleContext,
					norconexFilesystemConfigVariables, "norconex-filesystem-config", "variables");

			FilesystemCollectorConfig collectorConfig = (FilesystemCollectorConfig) new CollectorConfigLoader(
					FilesystemCollectorConfig.class).loadCollectorConfig(norconexFilesystemConfigXmlFile,
							norconexFilesystemConfigVariablesFile);
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

	private File generateTmpFileFromBundleFile(BundleContext bundleContext, String sourceFile, String targetFilename,
			String targetFileext) throws PcuCollectorException {
		try {
			File tmpFile = File.createTempFile(targetFilename, targetFileext);
			InputStream is = bundleContext.getBundle().getEntry(sourceFile).openStream();
			FileUtils.copyInputStreamToFile(is, tmpFile);
			return tmpFile;
		} catch (IOException e) {
			throw new PcuCollectorException("Could not copye bundle file to temp file", e);
		}

	}

}
