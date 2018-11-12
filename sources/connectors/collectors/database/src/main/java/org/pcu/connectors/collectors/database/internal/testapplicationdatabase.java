package org.pcu.connectors.collectors.database.internal;

import org.pcu.connectors.collectors.api.PcuCollectorConfig;
import org.pcu.connectors.collectors.api.PcuCollectorException;
import org.pcu.connectors.collectors.database.PcuDatabaseCollector;

public class testapplicationdatabase {

	public static void main(String[] args) throws PcuCollectorException {
		// TODO Auto-generated method stub
		PcuCollectorConfig config = new PcuCollectorConfig();
		config.set("username", "username");
		config.set("driver", "com.mysql.jdbc.Driver");
		config.set("password", "paswword");
		config.set("url", "url");
		PcuDatabaseCollector pcudatabasecollector = new PcuDatabaseCollector();
		pcudatabasecollector.execute(null, config);

	}

}
