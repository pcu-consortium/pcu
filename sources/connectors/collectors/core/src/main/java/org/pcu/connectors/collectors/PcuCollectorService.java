package org.pcu.connectors.collectors;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.pcu.connectors.collectors.api.PcuCollector;
import org.pcu.connectors.collectors.api.PcuCollectorConfig;
import org.pcu.connectors.collectors.api.PcuCollectorException;
import org.pcu.platform.client.PcuPlatformClient;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PcuCollectorService {

	private ServiceLoader<PcuCollector> loader;
	private static PcuCollectorService service;
	private PcuCollectorConfig config;

	private PcuCollectorService() {
		loader = ServiceLoader.load(PcuCollector.class);
	}

	public static synchronized PcuCollectorService getInstance() {
		if (service == null) {
			service = new PcuCollectorService();
		}
		return service;
	}

	public void execute() {
		if (config == null) {
			throw new IllegalStateException("Missing collector configuration");
		}

		try {
			PcuPlatformClient pcuPlatformClient = PcuPlatformClient.connect(config.getPcuPlatformUrl());
			Iterator<PcuCollector> collectors = loader.iterator();
			while (collectors.hasNext()) {
				PcuCollector collector = collectors.next();
				collector.execute(pcuPlatformClient, config);
			}
		} catch (ServiceConfigurationError serviceError) {
			serviceError.printStackTrace();
		} catch (PcuCollectorException e) {
			e.printStackTrace();
		}
	}

	public void init(String collectorConfig) {
		ObjectMapper objectMapper = new ObjectMapper(new JsonFactory());
		try {
			config = objectMapper.readValue(new File(collectorConfig), PcuCollectorConfig.class);
		} catch (IOException e) {
			throw new IllegalArgumentException("Could not load configuration file", e);
		}
	}
}