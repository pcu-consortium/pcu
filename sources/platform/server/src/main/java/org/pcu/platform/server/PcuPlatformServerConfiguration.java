package org.pcu.platform.server;

import java.io.IOException;
import java.io.InputStream;

import org.pcu.connectors.index.PcuIndex;
import org.pcu.connectors.index.PcuIndexConfiguration;
import org.pcu.connectors.index.PcuIndexFactory;
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
	public PcuIndex pcuIndex() {
		try {
			String pcuIndexType = env.getProperty("pcu.index.type");
			String pcuIndexFile = env.getProperty("pcu.index.file");

			if (pcuIndexType == null) {
				throw new IllegalArgumentException("pcu.index.type property is mandatory");
			}
			if (pcuIndexFile == null) {
				throw new IllegalArgumentException("pcu.index.file property is mandatory");
			}

			ObjectMapper mapper = new ObjectMapper();
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(pcuIndexFile);
			if (is == null) {
				throw new IllegalArgumentException("Could not find file " + pcuIndexFile);
			}
			JsonNode configuration = mapper.readTree(is);
			PcuIndexConfiguration pcuIndexConfiguration = new PcuIndexConfiguration(pcuIndexType, configuration);
			return PcuIndexFactory.createIndex(pcuIndexConfiguration);
		} catch (IOException e) {
			throw new IllegalArgumentException("Could not load configuration");
		}
	}
}
