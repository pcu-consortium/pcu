package org.pcu.connectors.collectors.database.internal;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.pcu.connectors.collectors.api.PcuCollectorConfig;
import org.pcu.connectors.collectors.api.PcuCollectorException;
import org.pcu.platform.client.PcuPlatformClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
;

public class PcuDatabaseNoroneCollector {
	private static final Logger LOGGER = LoggerFactory.getLogger(PcuDatabaseNoroneCollector.class);

	public void execute(PcuPlatformClient pcuPlatformclient, PcuCollectorConfig config) throws PcuCollectorException {
		LOGGER.debug("Execution start");
		try {
			Map<String, String> other = new HashMap<String, String>();
			other = config.any();
			String url = other.get("url");
			String password = other.get("password");
			String driver = other.get("driver");
			String username = other.get("username");
			if (url != null && password != null && driver != null && username != null) {
				PcuDatasourceConfiguration pcudatasourceconfiguration = new PcuDatasourceConfiguration();
				DataSource datasource=	pcudatasourceconfiguration.dataSource(url, password, username, driver);

				
				//pcuPlatformclient.ingest(document);
			} else {

				throw new PcuCollectorException("Collector configuration could not be instanciated");

			}

		} catch (Exception e) {
			throw new PcuCollectorException("Error while starting FileCrawler", e);
		}

	}

	

}