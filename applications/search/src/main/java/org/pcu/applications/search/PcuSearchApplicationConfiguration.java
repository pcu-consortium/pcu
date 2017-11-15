package org.pcu.applications.search;

import org.pcu.features.search.server.PcuSearchServerConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * In package ABOVE others else (ex. in .web) doesn't scan them.
 * 
 * Jackson XML provider is configured and set up by client module's configuration.
 * 
 * @author mdutoo
 */
@Configuration
@ComponentScan(basePackageClasses={PcuSearchApplicationConfiguration.class, PcuSearchServerConfiguration.class})
public class PcuSearchApplicationConfiguration {
    
}