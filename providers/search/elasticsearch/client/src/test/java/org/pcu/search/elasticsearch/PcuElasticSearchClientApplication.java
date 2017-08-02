package org.pcu.search.elasticsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * In package BELOW others else (ex. in .web) doesn't scan them
 * @author mdutoo
 */
@SpringBootApplication 
public class PcuElasticSearchClientApplication {
   
   public static void main(String[] args) {
       SpringApplication.run(PcuElasticSearchClientApplication.class, args); // TODO or PcuElasticSearchClientPackage.class (BELOW others)
   }
      
}