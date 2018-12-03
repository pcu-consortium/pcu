package org.pcu.connectors.collectors.database.internal;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@ComponentScan("org.pcu.connectors.collectors.database.internal")
public class PcuDatasourceConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(PcuDatasourceConfiguration.class);

	@Bean
	DataSource dataSource(String url, String password, String username, String driver) {
		System.out.print("Start database configuration");
		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
		driverManagerDataSource.setUrl(url);
		driverManagerDataSource.setUsername(username);
		driverManagerDataSource.setPassword(password);
		driverManagerDataSource.setDriverClassName(driver);
		LOGGER.debug("end database configuration");
		return driverManagerDataSource;
	}

}
