package org.pcu.features.search.server;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TODO move to pcu-app-search with dep to ES SPI and running (by default) its conf
 * In package BELOW others else (ex. in .web) doesn't scan them
 * @author mdutoo
 */
@SpringBootApplication
public class PcuSearchServerApplication {
   
   //@Value("${pcu.app.search.spi:org.pcu.providers.search.elasticsearch.spi}")
   private static final String searchSpiPackage = "org.pcu.providers.search.elasticsearch.spi";

   public static void main(String[] args) {
      SpringApplication.run(new Object[] { PcuSearchServerApplication.class, // PcuSearchClientPackage.class
            searchSpiPackage}, args); // PcuSearchServerConfiguration.class
   }

}
