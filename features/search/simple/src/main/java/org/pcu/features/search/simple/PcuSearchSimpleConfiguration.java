package org.pcu.features.search.simple;

import org.pcu.features.search.client.PcuSearchApiClientConfiguration;
import org.pcu.platform.rest.server.PcuPlatformRestServerConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * In package BELOW others else (ex. in .web) doesn't scan them.
 * 
 * @author mdutoo
 */
@Configuration
@ComponentScan(basePackageClasses={PcuSearchSimpleConfiguration.class, PcuSearchApiClientConfiguration.class,
      PcuPlatformRestServerConfiguration.class})
public class PcuSearchSimpleConfiguration {
    
}