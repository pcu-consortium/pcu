package org.pcu.connectors.collectors.database;

/*-
 * #%L
 * pcu-collectors-database
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





import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.dbcp2.BasicDataSource;
import org.pcu.connectors.collectors.api.PcuCollectorConfig;
import org.pcu.connectors.collectors.api.PcuCollectorException;
import org.pcu.platform.Document;
import org.pcu.platform.client.PcuPlatformClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PcuDatabaseJdbcCollector {
	private static final Logger LOGGER = LoggerFactory.getLogger(PcuDatabaseJdbcCollector.class);

	public void execute(PcuPlatformClient pcuPlatformclient, PcuCollectorConfig config) throws PcuCollectorException {
		LOGGER.debug("Execution start");

		try {
			Map<String, String> other = config.any();

			String index = other.get("pcuIndex");
			String type = other.get("pcuType");

			String url = other.get("url");
			String password = other.get("password");
			String driver = other.get("driver");
			String username = other.get("username");
			String query = other.get("query");

			if (index == null || type == null) {
				throw new PcuCollectorException(
						"Collector configuration could not be instanciated : missing pcuIndex or pcuType");
			}
			if (url == null || driver == null) {
				throw new PcuCollectorException(
						"Collector configuration could not be instanciated : missing driver configuration");
			}
			DataSource datasource = getDataSource(url, password, username, driver);

			try (Connection connection = datasource.getConnection();
					Statement statement = connection.createStatement()) {

				ObjectMapper objectMapper = new ObjectMapper();
				ResultSet rs = statement.executeQuery(query);
				ResultSetMetaData resultMeta = rs.getMetaData();
				int rowNum = 0;

				while (rs.next()) {
					ObjectNode json = objectMapper.createObjectNode();
					for (int i = 1; i <= resultMeta.getColumnCount(); i++)					{
						Object obj = rs.getObject(i);
						if (obj == null) {
							json.set(resultMeta.getColumnLabel(i), null);
						} else {
							json.put(resultMeta.getColumnLabel(i), obj.toString());
						}
					}
					String documentId = DigestUtils.md5Hex(config.getDatasourceId() + rowNum);
					Document document = new Document();
					document.setType(type);
					document.setId(documentId);
					document.setIndex(index);
					document.setDocument(json);
					document.setDocument(json);
					pcuPlatformclient.ingest(document);
					rowNum++;
				}
			}
		} catch (SQLException e) {
			throw new PcuCollectorException("Error while executing query", e);
		} catch (Exception e) {
			throw new PcuCollectorException("Error while executing DatabaseJdbcCollector", e);
		}
		LOGGER.debug("End Execution");
	}

	private DataSource getDataSource(String url, String password, String username, String driver) {
		LOGGER.debug("Start database configuration");
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(driver);
		ds.setUrl(url);
		ds.setUsername(username);
		ds.setPassword(password);
		LOGGER.debug("End database configuration");
		return ds;
	}

}
