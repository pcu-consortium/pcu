package org.pcu.features.connector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * In package BELOW others else (ex. in .web) doesn't scan them
 * 
 * @author mdutoo
 */
@ComponentScan({ "org.pcu.providers.search.api", "org.pcu.platform.model", "org.pcu.features.connector",
		"org.pcu.providers.search.elasticsearch.spi" })
@SpringBootApplication
public class PcuConnectorApplication {

	// @Value("${pcu.app.search.spi:org.pcu.providers.search.elasticsearch.spi}")
	private static final String searchSpiPackage = "org.pcu.providers.search.elasticsearch.spi";

	public static void main(String[] args) {
		// NOT also searchSpiPackage else still web server !
		try {
			SpringApplication app = new SpringApplication(PcuConnectorApplication.class);
			app.setWebApplicationType(WebApplicationType.NONE); // no web server
			ConfigurableApplicationContext context = app.run(args);
			PcuConnector bean = context.getBean(PcuConnector.class);
			bean.defaultCrawl();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

}
