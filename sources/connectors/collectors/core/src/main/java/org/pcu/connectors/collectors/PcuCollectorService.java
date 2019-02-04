package org.pcu.connectors.collectors;

/*-
 * #%L
 * PCU Collector Core
 * %%
 * Copyright (C) 2017 - 2018 PCU Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */





import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.pcu.connectors.collectors.api.PcuCollector;
import org.pcu.connectors.collectors.api.PcuCollectorConfig;
import org.pcu.connectors.collectors.api.PcuCollectorException;
import org.pcu.platform.client.PcuPlatformClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PcuCollectorService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PcuCollectorService.class);

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
		LOGGER.debug("Init collector configuration");
		ObjectMapper objectMapper = new ObjectMapper(new JsonFactory());
		try {
			config = objectMapper.readValue(new File(collectorConfig), PcuCollectorConfig.class);
			LOGGER.debug("Configuration " + config.toString());
		} catch (IOException e) {
			throw new IllegalArgumentException("Could not load configuration file", e);
		}
	}
}
