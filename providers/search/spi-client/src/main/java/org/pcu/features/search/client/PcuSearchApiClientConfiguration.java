package org.pcu.features.search.client;

import java.util.List;

import javax.ws.rs.ext.ExceptionMapper;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxrs.client.Client;
import org.apache.cxf.jaxrs.client.ResponseExceptionMapper;
import org.pcu.providers.search.api.PcuSearchApi;
import org.pcu.providers.search.api.PcuSearchEsClientApi;
import org.pcu.search.elasticsearch.client.PcuElasticSearchClientConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

/**
 * In package BELOW others else (ex. in .web) doesn't scan them
 * @author mdutoo
 */
@Configuration
@ComponentScan(basePackageClasses={PcuSearchApiClientConfiguration.class, // i.e. ("org.pcu.features.search.client") ; not org.pcu else scans ex. ESSearchProviderApiImpl
      PcuPlatformRestClientConfiguration.class, PcuElasticSearchClientConfiguration.class})
// which can't find client, and in another project does not work (order ?)
public class PcuSearchApiClientConfiguration {

   @Autowired
   private PcuPlatformRestClientConfiguration clientConf;
   
   @Bean
   public Client pcuSearchEsApiRestClient(SpringBus bus,
         @Qualifier("pcuApiJsonProvider") JacksonJsonProvider pcuApiJsonProvider,
         List<ExceptionMapper<?>> pcuExceptionMappers,
         List<ResponseExceptionMapper<?>> pcuResponseExceptionMappers) {
      return clientConf.pcuApiRestClientForClass(PcuSearchEsClientApi.class, bus,
            pcuApiJsonProvider, pcuExceptionMappers, pcuResponseExceptionMappers);
   }
   @Bean
   public Client pcuSearchApiRestClient(SpringBus bus,
         @Qualifier("pcuApiJsonProvider") JacksonJsonProvider pcuApiJsonProvider,
         List<ExceptionMapper<?>> pcuExceptionMappers,
         List<ResponseExceptionMapper<?>> pcuResponseExceptionMappers) {
      return clientConf.pcuApiRestClientForClass(PcuSearchApi.class, bus,
            pcuApiJsonProvider, pcuExceptionMappers, pcuResponseExceptionMappers);
   }
    
}