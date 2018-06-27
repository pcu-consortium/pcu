package org.pcu.features.search.server;

import java.util.HashSet;
import java.util.Set;

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
	   SpringApplication app = new SpringApplication(PcuSearchServerApplication.class);
	   Set<String> sources = new HashSet<>();
	   sources.add(searchSpiPackage);
	   app.setSources(sources);
	   app.run(args);
   }

}
