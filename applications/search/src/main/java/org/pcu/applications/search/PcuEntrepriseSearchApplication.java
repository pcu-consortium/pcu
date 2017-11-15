package org.pcu.applications.search;

import org.springframework.boot.SpringApplication;

public class PcuEntrepriseSearchApplication extends SpringApplication {

	public PcuEntrepriseSearchApplication(final Object... sources) {
		super(sources);
	}

   //@Value("${pcu.app.search.spi:org.pcu.providers.search.elasticsearch.spi}")
   private static final String searchSpiPackage = "org.pcu.providers.search.elasticsearch.spi";
   
   public static void main(final String... args) {
      new PcuEntrepriseSearchApplication(PcuSearchApplicationConfiguration.class, // PcuSearchClientPackage.class
            searchSpiPackage).run(args);
   }

}
