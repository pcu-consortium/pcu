package org.pcu.features.search.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pcu.features.search.api.PcuDocument;
import org.pcu.features.search.api.PcuSearchApi;
import org.pcu.features.search.api.PcuIndexResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * TODO rm / replace by PcuSearchApiImpl on server side ?!
 * 
 * @author mdutoo
 *
 */
/*@RunWith(SpringRunner.class)
@ContextConfiguration(classes=PcuSearchApiClientConfiguration.class, // PcuSearchApiClientApplication
   initializers = ConfigFileApplicationContextInitializer.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.DEFINED_PORT, properties="server.port=45665")
//rather than @SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT, properties="server_port=45665")
//which would require listening to an ApplicationEvent and therefore using a Provider pattern
//see https://docs.spring.io/spring-boot/docs/current/reference/html/howto-embedded-servlet-containers.html https://stackoverflow.com/questions/30312058/spring-boot-how-to-get-the-running-port
//or with autoconf redefine cxf.jaxrs.client.address
@ActiveProfiles("test")*/
public abstract class PcuSearchApiClientTest {

   protected static final Logger LOGGER = LoggerFactory.getLogger(PcuSearchApiClientTest.class);
   
   private boolean debug = true;

   // should be magical proxy (no impl in -client)
   @Autowired
   protected PcuSearchApi searchApi;

   protected String index = "files";
   protected String docId;
   

   /** TODO without @Pre 
    * @return */
   @Test
   public void testApi() {
      String index = "files";
      PcuDocument doc = new PcuDocument();
      PcuIndexResult res = searchApi.index(index, doc);
      // TODO check res
   }

   @Test
   public void testFeatures() {
      
   }
   
}
