package org.pcu.platform.server;

import java.io.IOException;
import java.io.InputStream;

import org.pcu.connectors.index.PcuIndex;
import org.pcu.connectors.index.PcuIndexConfiguration;
import org.pcu.connectors.index.PcuIndexFactory;
import org.pcu.connectors.storage.PcuStorage;
import org.pcu.connectors.storage.PcuStorageConfiguration;
import org.pcu.connectors.storage.PcuStorageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableSwagger2
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

	@Bean
	public PcuStorage pcuStorage() {
		try {
			String pcuStorageType = env.getProperty("pcu.storage.type");
			String pcuStorageFile = env.getProperty("pcu.storage.file");

			if (pcuStorageType == null) {
				throw new IllegalArgumentException("pcu.storage.type property is mandatory");
			}
			if (pcuStorageFile == null) {
				throw new IllegalArgumentException("pcu.storage.file property is mandatory");
			}

			ObjectMapper mapper = new ObjectMapper();
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(pcuStorageFile);
			if (is == null) {
				throw new IllegalArgumentException("Could not find file " + pcuStorageFile);
			}
			JsonNode configuration = mapper.readTree(is);
			PcuStorageConfiguration pcuStorageConfiguration = new PcuStorageConfiguration(pcuStorageType,
					configuration);
			return PcuStorageFactory.createStorage(pcuStorageConfiguration);
		} catch (IOException e) {
			throw new IllegalArgumentException("Could not load configuration");
		}
	}

	@Bean
	public Docket productApi() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("org.pcu.platform.server.rest")).build();
	}

}
