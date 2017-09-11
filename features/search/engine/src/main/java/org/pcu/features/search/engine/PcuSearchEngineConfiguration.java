package org.pcu.features.search.engine;

import org.pcu.features.search.client.PcuSearchApiClientConfiguration;
import org.pcu.platform.rest.server.PcuPlatformRestServerConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @author mdutoo
 */
@Configuration
@ComponentScan(basePackageClasses={PcuSearchEngineConfiguration.class, PcuSearchApiClientConfiguration.class,
      PcuPlatformRestServerConfiguration.class})
public class PcuSearchEngineConfiguration {
    
}