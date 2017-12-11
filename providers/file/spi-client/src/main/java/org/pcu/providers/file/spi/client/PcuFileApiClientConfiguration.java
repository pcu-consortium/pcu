package org.pcu.providers.file.spi.client;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxrs.client.Client;
import org.pcu.features.search.client.PcuPlatformRestClientConfiguration;
import org.pcu.providers.file.api.PcuFileApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

/**
 * In package BELOW others else (ex. in .web) doesn't scan them
 * @author mdutoo
 */
@Configuration
@ComponentScan(basePackageClasses={PcuFileApiClientConfiguration.class, // i.e. ("org.pcu.providers.file.spi-client") ; not org.pcu else scans ex. ESSearchProviderApiImpl
      PcuPlatformRestClientConfiguration.class})
// which can't find client, and in another project does not work (order ?)
public class PcuFileApiClientConfiguration {

   @Autowired
   private PcuPlatformRestClientConfiguration clientConf;
   
   @Bean
   public Client pcuFileApiRestClient(SpringBus bus,
         JacksonJsonProvider pcuApiJsonProvider/*,
         ESApiExceptionMapper exceptionMapper,
         ESApiResponseExceptionMapper responseExceptionMapper*/) {
      return clientConf.pcuApiRestClientForClass(PcuFileApi.class, bus, pcuApiJsonProvider);
   }
    
}