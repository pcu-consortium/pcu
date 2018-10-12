package org.pcu.platform.server;

import java.io.IOException;
import java.io.InputStream;

import org.pcu.connectors.indexer.PcuIndexer;
import org.pcu.connectors.indexer.PcuIndexerConfiguration;
import org.pcu.connectors.indexer.PcuIndexerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootConfiguration
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

			if (pcuIndexerType == null) {
				throw new IllegalArgumentException("pcu.indexer.type property is mandatory");
			}
			if (pcuIndexerFile == null) {
				throw new IllegalArgumentException("pcu.indexer.file property is mandatory");
			}

			ObjectMapper mapper = new ObjectMapper();
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(pcuIndexerFile);
			JsonNode configuration = mapper.readTree(is);
			PcuIndexerConfiguration pcuIndexerConfiguration = new PcuIndexerConfiguration(pcuIndexerType,
					configuration);
			return PcuIndexerFactory.createIndexer(pcuIndexerConfiguration);
		} catch (IOException e) {
			throw new IllegalArgumentException("Could not load configuration");
		}
	}
}
