package org.pcu.providers.search.elasticsearch.spi;

import org.pcu.platform.rest.server.PcuPlatformRestServerConfiguration;
import org.pcu.search.elasticsearch.client.PcuElasticSearchClientConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses={ESSearchProviderConfiguration.class,
      PcuPlatformRestServerConfiguration.class, // server
      PcuElasticSearchClientConfiguration.class}) // loads ES client dep
public class ESSearchProviderConfiguration {

   /**
    * NOO simpler : impl is declared in Spring directly with this name
    * (it is also deployed in JAXRS and JAXRS can't be aliased, so should be proxied).
    * PCU ES-like search API impl'd over ES
    * alias https://stackoverflow.com/questions/41025627/how-can-i-name-a-service-with-multiple-names-in-spring
    * @param eSSearchProviderEsApiServerImpl
    * @return
    */
   /*@Bean
   PcuSearchEsApi defaultSearchProviderEsApi(@Qualifier("ESSearchProviderEsApiServerImpl") PcuSearchEsApi eSSearchProviderEsApiServerImpl) {
      return eSSearchProviderEsApiServerImpl;
   }*/
   /**
    * NOO simpler : impl is declared in Spring directly with this name
    * (it is also deployed in JAXRS and JAXRS can't be aliased, so should be proxied).
    * PCU specific search API impl'd over ES
    * alias https://stackoverflow.com/questions/41025627/how-can-i-name-a-service-with-multiple-names-in-spring
    * @param eSSearchProviderApiImpl
    * @return
    */
   /*@Bean
   PcuSearchApi defaultSearchProviderApi(@Qualifier("ESSearchProviderApiImpl") PcuSearchApi eSSearchProviderApiImpl) {
      return eSSearchProviderApiImpl;
   }*/
   
}
