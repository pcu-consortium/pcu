package org.pcu.providers.search.elasticsearch.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pcu.features.search.client.PcuSearchApiClientConfiguration;
import org.pcu.providers.search.api.PcuDocument;
import org.pcu.providers.search.api.PcuIndexResult;
import org.pcu.providers.search.api.PcuSearchApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={ESSearchProviderConfiguration.class, PcuSearchApiClientConfiguration.class },
   initializers = ConfigFileApplicationContextInitializer.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.DEFINED_PORT, properties="server.port=45665")
//rather than @SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT, properties="server_port=45665")
//which would require listening to an ApplicationEvent and therefore using a Provider pattern
//see https://docs.spring.io/spring-boot/docs/current/reference/html/howto-embedded-servlet-containers.html https://stackoverflow.com/questions/30312058/spring-boot-how-to-get-the-running-port
//or with autoconf redefine cxf.jaxrs.client.address
@ActiveProfiles("test")
public class ESSearchProviderApiImplTest  {

   //@Configuration
   //@Import(PcuElasticSearchClientConfiguration.class)
   /*@ComponentScan(basePackages={"org.pcu"}) // or PcuElasticSearchClientConfiguration AND ESSearchProviderApiImplConfiguration WITH @ComponentScan or Application
   public static class Conf {
      
   }*/

   @Autowired @Qualifier("pcuSearchApiRestClient") //@Qualifier("pcuSearchApiImpl") //pcuSearchApiRestClient
   private PcuSearchApi searchApi;

   @Test
   public void testIndex() {
      String index = "files";
      PcuDocument doc = new PcuDocument();
      doc.setType("file");
      doc.setId("myid"); // TODO gen
      
      // no version (in pipeline ?)
      PcuIndexResult res = searchApi.index(index, doc);
      assertNotNull(res);
      assertTrue(res.getCreated() || res.getVersion() > 0);

      // index version KO :
      /*
      try {
         esSearchProviderApi.index(index, doc);
         fail("optimistic locking should fail");
      } catch (RuntimeException rex) {
         assertTrue("versions don't match", true);
      }
      */

      // index version OK :
      PcuDocument foundDoc = searchApi.get(index, doc.getId());
      assertNotNull(foundDoc);
      doc.setVersion(foundDoc.getVersion());
      res = searchApi.index(index, doc);
      assertNotNull(res);
      assertFalse(res.getCreated());
      assertEquals(foundDoc.getVersion() + 1, (long) res.getVersion());

      // get version OK/KO :
      foundDoc = searchApi.get(index, doc.getId()/*,doc.getVersion()*/);
   }
   
}
