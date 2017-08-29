package org.pcu.providers.search.elasticsearch.spi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pcu.providers.search.spi.PcuSearchProviderApi;
import org.pcu.providers.search.spi.SpiDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={ESSearchProviderConfiguration.class},
   initializers = ConfigFileApplicationContextInitializer.class)
@ActiveProfiles("test")
public class ESSearchProviderApiImplTest  {

   //@Configuration
   //@Import(PcuElasticSearchClientConfiguration.class)
   /*@ComponentScan(basePackages={"org.pcu"}) // or PcuElasticSearchClientConfiguration AND ESSearchProviderApiImplConfiguration WITH @ComponentScan or Application
   public static class Conf {
      
   }*/

   @Autowired
   private PcuSearchProviderApi esSearchProviderApi;

   @Test
   public void testIndex() {
      String index = "files";
      SpiDocument spiDoc = new SpiDocument();
      spiDoc.setType("file");
      spiDoc.setId("myid"); // TODO gen
      // TODO version
      /*Object spiRes = */esSearchProviderApi.index(index, spiDoc);
      // TODO check
   }
   
}
