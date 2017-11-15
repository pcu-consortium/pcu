package org.pcu.providers.search.elasticsearch.spi;

import org.pcu.providers.search.api.PcuSearchApi;
import org.pcu.providers.search.api.PcuSearchEsApi;
import org.pcu.search.elasticsearch.client.PcuElasticSearchClientConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses={ESSearchProviderConfiguration.class,
      PcuElasticSearchClientConfiguration.class}) // loads ES client dep
public class ESSearchProviderConfiguration {

   /**
    * PCU ES-like search API impl'd over ES
    * alias https://stackoverflow.com/questions/41025627/how-can-i-name-a-service-with-multiple-names-in-spring
    * @param eSSearchProviderEsApiServerImpl
    * @return
    */
   @Bean
   PcuSearchEsApi defaultSearchProviderEsApiImpl(@Qualifier("ESSearchProviderEsApiServerImpl") PcuSearchEsApi eSSearchProviderEsApiServerImpl) {
      return eSSearchProviderEsApiServerImpl;
   }
   /**
    * PCU specific search API impl'd over ES
    * alias https://stackoverflow.com/questions/41025627/how-can-i-name-a-service-with-multiple-names-in-spring
    * @param eSSearchProviderApiImpl
    * @return
    */
   @Bean
   PcuSearchApi defaultSearchProviderApiImpl(@Qualifier("ESSearchProviderApiImpl") PcuSearchApi eSSearchProviderApiImpl) {
      return eSSearchProviderApiImpl;
   }
   
}
