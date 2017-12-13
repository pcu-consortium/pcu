package org.pcu.features.connector;

import org.pcu.features.search.client.PcuPlatformRestClientConfiguration;
import org.pcu.features.search.client.PcuSearchApiClientConfiguration;
import org.pcu.providers.file.spi.client.PcuFileApiClientConfiguration;
import org.pcu.providers.metadata.api.PcuMetadataApi;
import org.pcu.providers.metadata.spi.ExtractorProviderApiImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * In package ABOVE others else (ex. in .web) doesn't scan them.
 * 
 * @author mdutoo
 */
@EnableScheduling
@Configuration
@ComponentScan(basePackageClasses={PcuConnectorConfiguration.class,
      PcuSearchApiClientConfiguration.class, PcuFileApiClientConfiguration.class, // PCU API REST clients in required !
      PcuPlatformRestClientConfiguration.class})
public class PcuConnectorConfiguration {
   
   @Bean
   public PcuMetadataApi defaultMetadataExtractorApi() {
      return new ExtractorProviderApiImpl(); // TODO rather in metatada SPI ?!
   }
   
}