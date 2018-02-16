package org.pcu.providers.file.local.spi;

import org.pcu.platform.rest.server.PcuPlatformRestServerConfiguration;
import org.pcu.providers.file.api.PcuFileApi;
import org.pcu.search.elasticsearch.client.PcuElasticSearchClientConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses={LocalFileProviderConfiguration.class,
      PcuPlatformRestServerConfiguration.class,  // else can't find our PcuFileApi impl even if near this class !
      PcuElasticSearchClientConfiguration.class}) // else No qualifying bean of type 'java.util.List<javax.ws.rs.ext.ExceptionMapper<?>>' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {}
public class LocalFileProviderConfiguration {

   /**
    * PCU specific file API impl'd on its own
    * alias https://stackoverflow.com/questions/41025627/how-can-i-name-a-service-with-multiple-names-in-spring
    * else context.getBean() or  @Bean(name = { ...}) https://stackoverflow.com/questions/27107133/spring-bean-alias-in-javaconfig
    * @param localFileProviderApiImpl
    * @return
    */
   ///@Bean
   PcuFileApi defaultFileProviderApi(/*TODO @Qualifier("LocalFileProviderApiImpl")*/ PcuFileApi localFileProviderApiImpl) {
      return localFileProviderApiImpl;
   }
   
}
