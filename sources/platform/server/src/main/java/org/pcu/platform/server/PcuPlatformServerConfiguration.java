package org.pcu.platform.server;

import java.io.File;
import java.io.IOException;

import org.pcu.connectors.indexer.PcuIndexer;
import org.pcu.connectors.indexer.PcuIndexerConfiguration;
import org.pcu.connectors.indexer.PcuIndexerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableAutoConfiguration
public class PcuPlatformServerConfiguration {

	/** for prop check purpose */
	@Autowired
	private Environment env;

	@Bean
	public PcuIndexer pcuIndexer() {
		try {
			String pcuIndexerType = env.getProperty("pcu.indexer.type");
			String pcuIndexerFile = env.getProperty("pcu.indexer.file");

			ObjectMapper mapper = new ObjectMapper();
			File from = new File(pcuIndexerFile);
			JsonNode configuration = mapper.readTree(from);
			PcuIndexerConfiguration pcuIndexerConfiguration = new PcuIndexerConfiguration(pcuIndexerType,
					configuration);
			return PcuIndexerFactory.createIndexer(pcuIndexerConfiguration);
		} catch (IOException e) {
			throw new IllegalArgumentException("Could not load configuration");
		}
	}
}
