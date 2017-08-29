package org.pcu.features.search.simple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Helper
 * In package BELOW others else (ex. in .web) doesn't scan them
 * @author mdutoo
 */
@SpringBootApplication
public class PcuSearchSimpleApplication {
   
   //@Value("${pcu.app.search.spi:org.pcu.providers.search.elasticsearch.spi}")
   private static final String searchSpiPackage = "org.pcu.providers.search.elasticsearch.spi";

   public static void main(String[] args) {
      SpringApplication.run(new Object[] { PcuSearchSimpleApplication.class, // PcuSearchClientPackage.class
            searchSpiPackage}, args); // PcuSearchServerConfiguration.class
   }

}
