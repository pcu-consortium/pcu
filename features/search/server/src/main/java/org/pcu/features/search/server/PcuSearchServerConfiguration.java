package org.pcu.features.search.server;

import org.pcu.features.search.client.PcuSearchApiClientConfiguration;
import org.pcu.platform.rest.server.PcuPlatformRestServerConfiguration;
import org.pcu.providers.file.local.spi.LocalFileProviderConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * In package ABOVE others else (ex. in .web) doesn't scan them.
 * 
 * Jackson XML provider is configured and set up by client module's configuration.
 * 
 * @author mdutoo
 */
@Configuration
@ComponentScan(basePackageClasses={PcuSearchServerConfiguration.class,
      PcuSearchApiClientConfiguration.class, LocalFileProviderConfiguration.class,
      /*PcuSearchPipelineConfiguration.class, PcuSearchEngineConfiguration.class,*/ // PcuSearchSimpleConfiguration.class, 
      PcuPlatformRestServerConfiguration.class})
public class PcuSearchServerConfiguration {
   
}