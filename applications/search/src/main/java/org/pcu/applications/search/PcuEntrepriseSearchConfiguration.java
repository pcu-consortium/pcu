package org.pcu.applications.search;

import org.pcu.features.connector.PcuConnectorConfiguration;
import org.pcu.features.search.server.PcuSearchServerConfiguration;
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
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
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class })
@ComponentScan(basePackageClasses={PcuEntrepriseSearchConfiguration.class,
      // NB. integrated big data components are brought (selected to be scanned) by conf in PcuEntrepriseSearchApplication
      // generic components are brought by features
      //PcuConnectorConfiguration.class, // embedded connector, crawls at startup by default but commented for now
      PcuSearchServerConfiguration.class}) // brings dummy server, pipeline, TODO engine
public class PcuEntrepriseSearchConfiguration {
    
}