package org.pcu.connectors.collectors.database.internal;

import org.pcu.connectors.collectors.api.PcuCollectorConfig;
import org.pcu.connectors.collectors.api.PcuCollectorException;
import org.pcu.connectors.collectors.database.PcuDatabaseCollector;
import org.pcu.platform.client.PcuPlatformClient;

public class testapplicationdatabase {

	public static void main(String[] args) throws PcuCollectorException {
		// TODO Auto-generated method stub
		PcuCollectorConfig config = new PcuCollectorConfig();
		config.set("username", "jague");
		config.set("driver", "com.mysql.jdbc.Driver");
		config.set("password", "123");
		config.set("typequery", "select");
		//config.set("query", "insert into  tutorial (id,name) values (4,'mysql') ");
		config.set("query", "select * from tutorial ");
		config.set("url", "jdbc:mysql://localhost/testdatabase1");
		PcuDatabaseCollector pcudatabasecollector = new PcuDatabaseCollector();
		pcudatabasecollector.execute(null, config);

	}

}
