package org.pcu.providers.file.spi.client;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pcu.providers.file.api.PcuFileApi;
import org.pcu.providers.file.api.PcuFileResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * 
 * @author mdutoo
 *
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes=PcuFileApiClientConfiguration.class,
   initializers = ConfigFileApplicationContextInitializer.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.DEFINED_PORT, properties="server.port=45665")
//rather than @SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT, properties="server_port=45665")
//which would require listening to an ApplicationEvent and therefore using a Provider pattern
//see https://docs.spring.io/spring-boot/docs/current/reference/html/howto-embedded-servlet-containers.html https://stackoverflow.com/questions/30312058/spring-boot-how-to-get-the-running-port
//or with autoconf redefine cxf.jaxrs.client.address
@ActiveProfiles("test")
public abstract class PcuFileApiClientTest {

   protected static final Logger LOGGER = LoggerFactory.getLogger(PcuFileApiClientTest.class);
   
   private boolean debug = true;

   // should be magical proxy (no impl in -client)
   @Autowired @Qualifier("pcuFileApiRestClient") //defaultFileProviderApi LocalFileProviderApiImpl pcuFileApiRestClient
   private PcuFileApi fileApi;

   protected boolean isMock() {
      return false;
   }
   

   /** TODO without @Pre 
    * @return */
   @Test
   public void testApi() throws IOException {
      String testStore = "testStore";
      String testContent = "test";
      // 1. upload crawled content :
      PcuFileResult fileRes = fileApi.storeContent(testStore, new ByteArrayInputStream(testContent.getBytes()));
      if (isMock())
      assertEquals(fileRes.getPath(), PcuFileApiMockImpl.TEST_PATH);
      // and check :
      InputStream testFileInRes = fileApi.getContent(testStore, fileRes.getPath());
      assertEquals(testContent, IOUtils.toString(testFileInRes, (Charset) null));
   }

   @Test
   public void testFeatures() {
      
   }
   
}
