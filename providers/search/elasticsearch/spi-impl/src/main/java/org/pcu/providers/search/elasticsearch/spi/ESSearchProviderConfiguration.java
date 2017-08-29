package org.pcu.providers.search.elasticsearch.spi;

import org.pcu.search.elasticsearch.client.PcuElasticSearchClientConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses={ESSearchProviderConfiguration.class,
      PcuElasticSearchClientConfiguration.class}) // loads ES client dep
public class ESSearchProviderConfiguration {

}
