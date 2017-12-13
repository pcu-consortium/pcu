package org.pcu.features.search.server;

import org.pcu.features.search.client.PcuSearchApiClientConfiguration;
import org.pcu.features.search.pipeline.PcuSearchPipelineConfiguration;
import org.pcu.platform.rest.server.PcuPlatformRestServerConfiguration;
import org.pcu.providers.file.spi.client.PcuFileApiClientConfiguration;
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
      PcuSearchApiClientConfiguration.class, PcuFileApiClientConfiguration.class, // REST clients are not really useful, API (no Spring conf) would be enough // LocalFileProviderConfiguration.class, 
      // NB. integrated big data components are brought (selected to be scanned) by conf in PcuEntrepriseSearchApplication
      PcuSearchPipelineConfiguration.class, /*PcuSearchEngineConfiguration.class,*/
      //PcuModelConfiguration.class, // brought by PcuSearchPipelineConfiguration
      PcuPlatformRestServerConfiguration.class})
public class PcuSearchServerConfiguration {
   
}