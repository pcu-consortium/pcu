package org.pcu.platform.model;

import org.pcu.platform.rest.server.PcuPlatformRestServerConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * In package ABOVE others else (ex. in .web) doesn't scan them.
 * 
 * @author mdutoo
 */
@Configuration
@ComponentScan(basePackageClasses={PcuModelConfiguration.class,
      //PcuSearchApiConfiguration.class, //PcuSearchApiClientConfiguration.class, LocalFileProviderConfiguration.class,
      /*PcuSearchPipelineConfiguration.class, PcuSearchEngineConfiguration.class,*/ // PcuSearchSimpleConfiguration.class, 
      //ESSearchProviderConfiguration.class, // TODO requires ES
      PcuPlatformRestServerConfiguration.class})
public class PcuModelConfiguration {

   @Bean
   public ObjectMapper pcuApiAvroMapper(@Qualifier("pcuApiMapper") ObjectMapper pcuApiMapper) {
      // custom JSON conversion because of dates :
      ObjectMapper pcuApiAvroMapper = pcuApiMapper.copy();
      pcuApiAvroMapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS); // else double ex. 1512423589.000000000 https://stackoverflow.com/questions/27951124/jackson-java-8-datetime-serialisation
      pcuApiAvroMapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      // and timestamp-millis avro annotation (optional) https://avro.apache.org/docs/1.8.0/spec.html#Timestamp+%28millisecond+precision%29
      // else AvroTypeException: Expected long. Got VALUE_STRING
      return pcuApiAvroMapper;
   }
   
}