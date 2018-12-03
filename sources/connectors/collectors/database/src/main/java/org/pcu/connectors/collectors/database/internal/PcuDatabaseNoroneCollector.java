package org.pcu.connectors.collectors.database.internal;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.codec.digest.DigestUtils;

import org.json.JSONObject;
import org.pcu.connectors.collectors.api.PcuCollectorConfig;
import org.pcu.connectors.collectors.api.PcuCollectorException;
import org.pcu.platform.Document;
import org.pcu.platform.client.PcuPlatformClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class PcuDatabaseNoroneCollector {
	private static final Logger LOGGER = LoggerFactory.getLogger(PcuDatabaseNoroneCollector.class);


	public void execute(PcuPlatformClient pcuPlatformclient, PcuCollectorConfig config) throws PcuCollectorException {
		LOGGER.debug("Execution start");

		try {
			
			Map<String, String> other = new HashMap<String, String>();
			Document document = new Document();
			other = config.any();

			// config.set("query", "insert into tutorial (id,name) values (4,'mysql') ");

			String url = other.get("url");
			String password = other.get("password");
			String driver = other.get("driver");
			String username = other.get("username");
			String query = other.get("query");
			int reference = 0;
           	if (url == null || password == null || driver == null || username == null) {
				throw new PcuCollectorException("Collector configuration could not be instanciated");
			} else {
				PcuDatasourceConfiguration pcudatasourceconfiguration = new PcuDatasourceConfiguration();
				DataSource datasource = pcudatasourceconfiguration.dataSource(url, password, username, driver);
				Statement statement = datasource.getConnection().createStatement();

				ResultSet rs = statement.executeQuery(query);
				ResultSetMetaData resultMeta = rs.getMetaData();
				while (rs.next()) {
					JSONObject json = new JSONObject();
					for (int i = 1; i <= resultMeta.getColumnCount(); i++) {
						Object obj = rs.getObject(i);
						if (obj == null) {
							json.put(resultMeta.getColumnName(i), "null");
						} else {
							json.put(resultMeta.getColumnName(i), obj.toString());
						}
					}
					String documentId = DigestUtils.md5Hex(config.getDatasourceId() + reference++);
					document.setType("document");
					document.setId(documentId);
					document.setIndex("document");
					ObjectMapper objectMapper = new ObjectMapper();
					JsonNode jsonNode = objectMapper.readTree(json.toString());
					document.setDocument(jsonNode);
					pcuPlatformclient.ingest(document);
					LOGGER.debug("End Execution");
				}

			}

		} catch (Exception e) {
			throw new PcuCollectorException("Error while starting FileCrawler", e);
		}
	}
}