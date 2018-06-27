package org.pcu.applications.search;

import java.util.ArrayList;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class PcuEntrepriseSearchApplication extends SpringApplication {

   private static final Log log = LogFactory.getLog(PcuEntrepriseSearchApplication.class);
   
   public PcuEntrepriseSearchApplication() {
	   // mandatory for spring
   }
   
   public PcuEntrepriseSearchApplication(final Class<?>... sources) {
		super(sources);
	}

	/** YAML property that specifies the packages of chosen provider implementations */
   public static final String PROVIDERS_PROP = "pcu.providers";
   /** default search provider impl package */
   public static final String searchSpiPackage = "org.pcu.providers.search.elasticsearch.spi";
   /** default file provider impl package */
   public static final String fileSpiPackage = "org.pcu.providers.file.local.spi";
   
   public static void main(final String... args) {
      new PcuEntrepriseSearchApplication(PcuEntrepriseSearchConfiguration.class).run(args);
   }
   
   @Override
   protected void postProcessApplicationContext(ConfigurableApplicationContext context) {
      chooseProviderImplementations(context);
      
      super.postProcessApplicationContext(context);
   }

   /**
    * Enriches Spring Boot Application sources by (provider implementation) packages that have been configured in YAML.
    * Must be called after YAML properties have been read (in run() > prepareEnvironment() > listeners.environmentPrepared(environment))
    * and before sources are read (in run() > prepareContext() > getSources())
    * @param context
    */
   private void chooseProviderImplementations(ConfigurableApplicationContext context) {
      ArrayList<String> providerSpiPackages = new ArrayList<String>();
      String providerSpiPackage;
      int i = 0;
      while ((providerSpiPackage = context.getEnvironment().getProperty(PROVIDERS_PROP + "[" + i++ + "]")) != null) {
         providerSpiPackages.add(providerSpiPackage);
      }
      if (providerSpiPackages.isEmpty()) {
         // default :
         providerSpiPackages.add(searchSpiPackage);
         providerSpiPackages.add(fileSpiPackage);
      }
      log.info("Choosing the following configured providers : " + providerSpiPackages);
      //Object found = context.getEnvironment().getProperty("pcu.providers[1]");
      //context.getEnvironment().getPropertySources().get("applicationConfigurationProperties")
      Set<String> sources = this.getSources(); // TODO Q copy ?
      sources.addAll(providerSpiPackages);
      this.setSources(sources);
   }

}
