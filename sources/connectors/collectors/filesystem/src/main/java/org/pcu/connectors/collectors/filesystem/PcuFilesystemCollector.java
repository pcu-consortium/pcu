package org.pcu.connectors.collectors.filesystem;

/*-
 * #%L
 * PCU Collector Filesystem
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





import org.pcu.connectors.collectors.api.PcuCollector;
import org.pcu.connectors.collectors.api.PcuCollectorConfig;
import org.pcu.connectors.collectors.api.PcuCollectorException;
import org.pcu.connectors.collectors.filesystem.internal.PcuFilesystemNorconexCollector;
import org.pcu.platform.client.PcuPlatformClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PcuFilesystemCollector implements PcuCollector {

	private static final Logger LOGGER = LoggerFactory.getLogger(PcuFilesystemCollector.class);

	public PcuFilesystemCollector() {
		super();
	}

	@Override
	public void execute(PcuPlatformClient pcuPlatformClient, PcuCollectorConfig config) throws PcuCollectorException {
		LOGGER.debug("Execution start");

		if (pcuPlatformClient == null) {
			throw new PcuCollectorException("pcuPlatformClient is mandatory");
		}
		if (config == null) {
			throw new PcuCollectorException("config is mandatory");
		}
		PcuFilesystemNorconexCollector pcuFilesystemNorconexCollector = new PcuFilesystemNorconexCollector();
		pcuFilesystemNorconexCollector.execute(pcuPlatformClient, config);

		LOGGER.debug("Execution end");

	}

	@Override
	public String getId() {
		return PcuFilesystemCollector.class.getName();
	}

}
