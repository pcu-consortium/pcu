package org.pcu.applications.search;

import org.pcu.features.search.server.PcuSearchServerApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.ConfigurableEnvironment;

//@SpringBootApplication // NO else NoSuchBeanDefinitionException: No qualifying bean of type 'java.lang.Class<?>' available
public class PcuEntrepriseSearchApplication extends SpringApplication {

	public PcuEntrepriseSearchApplication(final Object... sources) {
		super(sources);
	}

   //@Value("${pcu.app.search.spi:org.pcu.providers.search.elasticsearch.spi}")
   private static final String searchSpiPackage = "org.pcu.providers.search.elasticsearch.spi";
   private static final String fileSpiPackage = "org.pcu.providers.file.local.spi";
   
   public static void main(final String... args) {
      new PcuEntrepriseSearchApplication(PcuEntrepriseSearchConfiguration.class, // PcuSearchClientPackage.class
            searchSpiPackage, fileSpiPackage).run(args);
      //SpringApplication.run(new Object[] { PcuSearchServerApplication.class, // PcuSearchClientPackage.class
      //      searchSpiPackage}, args); // PcuSearchServerConfiguration.class
   }

}
