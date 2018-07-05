package org.pcu.connectors.collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = { "org.pcu.connectors.collectors","org.pcu.connectors.indexer" })
@SpringBootApplication
public class PcuCollectorApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(PcuCollectorApplication.class);

	public static void main(String[] args) throws PcuCollectorException {
		LOGGER.debug("Start Application PcuCollectorApplication");
		SpringApplication app = new SpringApplication(PcuCollectorApplication.class);
		app.setWebApplicationType(WebApplicationType.NONE); // no web server
		ConfigurableApplicationContext context = app.run(args);
		PcuAgent agent = context.getBean(PcuAgent.class);
		agent.exectue();
		LOGGER.debug("Stop Application PcuCollectorApplication");
	}

}
