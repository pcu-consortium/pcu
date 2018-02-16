package org.pcu.features.connector;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.ext.ExceptionMapper;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxrs.client.Client;
import org.apache.cxf.jaxrs.client.ResponseExceptionMapper;
import org.pcu.features.configuration.api.PcuConfigurationApi;
import org.pcu.features.search.client.PcuPlatformRestClientConfiguration;
import org.pcu.features.search.client.PcuSearchApiClientConfiguration;
import org.pcu.providers.file.spi.client.PcuFileApiClientConfiguration;
import org.pcu.providers.metadata.api.PcuMetadataApi;
import org.pcu.providers.metadata.spi.ExtractorProviderApiImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

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
   
   @Autowired
   private PcuPlatformRestClientConfiguration clientConf;

   /*
   @Bean
   public Client pcuConnectorApiRestClient(SpringBus bus,
         JacksonJsonProvider pcuApiJsonProvider/,
         ESApiExceptionMapper exceptionMapper,
         ESApiResponseExceptionMapper responseExceptionMapper/) {
      return clientConf.pcuApiRestClientForClass(PcuConnectorApi.class, bus, pcuApiJsonProvider);
   }
   */
   @Bean
   public Client pcuConfigurationApiRestClient(SpringBus bus,
         @Qualifier("pcuApiTypedJsonProvider") JacksonJsonProvider pcuApiTypedJsonProvider,
         List<ExceptionMapper<?>> pcuExceptionMappers,
         List<ResponseExceptionMapper<?>> pcuResponseExceptionMappers) {
      return clientConf.pcuApiRestClientForClass(PcuConfigurationApi.class, bus,
            pcuApiTypedJsonProvider, pcuExceptionMappers, pcuResponseExceptionMappers);
   }
   
   @Bean
   public PcuMetadataApi defaultMetadataExtractorApi() {
      return new ExtractorProviderApiImpl(); // TODO rather in metatada SPI ?!
   }

   @Bean
   public PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver() {
      return new PathMatchingResourcePatternResolver();
   }

   /** JDBC sample init
    * rather than @Sql({ "classpath:/sql/data.sql" }) which only works in tests // NOT "classpath:/sql/cleanup.sql" since not created by hibernate previously */
   @Bean
   public DataSourceInitializer defaultPersonDataSourceInitializer(PathMatchingResourcePatternResolver resourceLoader) throws IOException {
      // setup samples :
      DataSourceInitializer dsi = new DataSourceInitializer();
      dsi.setDataSource(PcuConnector.buildDefaultPersonAvroDrivenJDBCCrawler(null).getJdbcTemplate().getDataSource());
      dsi.setDatabasePopulator(new ResourceDatabasePopulator(resourceLoader.getResources("classpath:/sql/data.sql")));
      dsi.setDatabaseCleaner(new ResourceDatabasePopulator(resourceLoader.getResources("classpath:/sql/cleanup.sql")));
      return dsi;
   }
   
}