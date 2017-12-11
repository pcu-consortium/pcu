package org.pcu.features.connector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * In package BELOW others else (ex. in .web) doesn't scan them
 * @author mdutoo
 */
@SpringBootApplication
public class PcuConnectorApplication {
   
   //@Value("${pcu.app.search.spi:org.pcu.providers.search.elasticsearch.spi}")
   private static final String searchSpiPackage = "org.pcu.providers.search.elasticsearch.spi";

   public static void main(String[] args) {
      SpringApplication app = new SpringApplication(new Object[] { PcuConnectorApplication.class // PcuSearchConnectorPackage.class
            }); // NOT also searchSpiPackage else still web server !
      app.setWebEnvironment(false); // no web server
      app.run(args);
      
   }

}
