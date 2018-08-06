package org.pcu.platform.server;

import org.pcu.connectors.indexer.PcuIndexer;
import org.pcu.connectors.indexer.PcuIndexerFactory;
import org.pcu.connectors.indexer.PcuIndexerType;
import org.pcu.connectors.indexer.elasticsearch.PcuESIndexerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@EnableAutoConfiguration
public class PcuPlatformServerConfiguration {

    /** for prop check purpose */
    @Autowired
    private Environment env;
    
	@Bean
    public PcuIndexer pcuIndexer() {
		System.out.println("creating indexer");
		String pcuIndexerType = env.getProperty("pcu.indexer.type");
		String pcuIndexerConfiguration = env.getProperty("pcu.indexer.configuration");
		return PcuIndexerFactory.createIndexer(PcuIndexerType.ES5, new PcuESIndexerConfiguration("http://localhost:9200"));
    }
}
