package org.pcu.providers.file.local.spi;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pcu.providers.file.api.PcuFileApi;
import org.pcu.providers.file.api.PcuFileResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO extend it to inject REST client proxy and test also REST API
 * @author mardut
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={LocalFileProviderConfiguration.class},
   initializers = ConfigFileApplicationContextInitializer.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.DEFINED_PORT, properties="server.port=45665")
//rather than @SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT, properties="server_port=45665")
//which would require listening to an ApplicationEvent and therefore using a Provider pattern
//see https://docs.spring.io/spring-boot/docs/current/reference/html/howto-embedded-servlet-containers.html https://stackoverflow.com/questions/30312058/spring-boot-how-to-get-the-running-port
//or with autoconf redefine cxf.jaxrs.client.address
@ActiveProfiles("test")
public class LocalFileProviderApiImplTest  {
   
   @LocalServerPort
   protected int serverPort;

   //@Configuration
   //@Import(PcuElasticSearchClientConfiguration.class)
   /*@ComponentScan(basePackages={"org.pcu"}) // or PcuElasticSearchClientConfiguration AND ESSearchProviderApiImplConfiguration WITH @ComponentScan or Application
   public static class Conf {
      
   }*/

   @Autowired
   private PcuFileApi localFileProviderApi;
   /** for tests */
   @Autowired
   private LocalFileProviderApiImpl localFileProviderApiImpl;

   String store = "mystore"; // TODO
   String path = "mydir/myfile.doc"; // TODO
   String testContent = "My test content";
   
   @Before
   public void cleanup() throws Exception {
      // cleanup :
      localFileProviderApiImpl.deleteStore(store);
   }
   
   @Test
   public void testHashAsId() throws Exception {
      String anotherStore = "anotherStore";
      
      // cleanup :
      localFileProviderApiImpl.deleteStore(anotherStore);
      
      PcuFileResult res = localFileProviderApi.storeContent(store, new ByteArrayInputStream(testContent.getBytes()));
      String path = res.getPath();
      InputStream testInRes = localFileProviderApi.getContent(store, path);
      assertEquals(testContent, IOUtils.toString(testInRes, (Charset) null));
      
      // TODO check that can't be appended

      // check that not written again in same store :
      long firstWriteTime = localFileProviderApiImpl.getContentFile(store, path).lastModified();
      Thread.sleep(1000);
      localFileProviderApi.storeContent(store, new ByteArrayInputStream(testContent.getBytes()));
      long secondWriteTime = localFileProviderApiImpl.getContentFile(store, path).lastModified();
      assertEquals(firstWriteTime, secondWriteTime);
      
      // check that can still be written in another store :
      Thread.sleep(1000);
      res = localFileProviderApi.storeContent(anotherStore, new ByteArrayInputStream(testContent.getBytes()));
      try {
         localFileProviderApi.getContent(anotherStore, path);
         assertTrue(true);
      } catch (RuntimeException e) {
         fail("content should exist");
      }
      File anotherStoredFile = localFileProviderApiImpl.getContentFile(anotherStore, path);
      secondWriteTime = anotherStoredFile.lastModified();
      assertNotEquals(firstWriteTime, secondWriteTime);

      // test delete :
      localFileProviderApi.deleteContent(store, path);
      try {
         localFileProviderApi.getContent(store, path);
         fail("content should not exist anymore");
      } catch (RuntimeException e) {
         assertTrue(true);
      }
      
      // check still there in another store :
      try {
         localFileProviderApi.getContent(anotherStore, path);
         assertTrue(true);
      } catch (RuntimeException e) {
         fail("content should still exist");
      }

      localFileProviderApi.deleteContent(anotherStore, path);
      try {
         localFileProviderApi.getContent(anotherStore, path);
         fail("content should not exist anymore");
      } catch (RuntimeException e) {
         assertTrue(true);
      }
   }

   @Test
   public void testNameAsId() throws Exception {
      String testAppendContent = "\nand another test content";
      String testFullContent = testContent + testAppendContent;
      
      // check none yet :
      try {
         localFileProviderApi.getContent(store, path);
         fail("content should not exist yet");
      } catch (RuntimeException e) {
         assertTrue(e.getMessage().contains("Can't find"));
      }
      
      // create using append :
      PcuFileResult res = localFileProviderApi.appendContent(store, path, null, new ByteArrayInputStream(testContent.getBytes()));
      assertEquals(path, res.getPath());
      InputStream testFileInRes = localFileProviderApi.getContent(store, path);
      assertEquals(testContent, IOUtils.toString(testFileInRes, (Charset) null));
      
      // append :
      res = localFileProviderApi.appendContent(store, path, null, new ByteArrayInputStream(testAppendContent.getBytes()));
      assertEquals(path, res.getPath());
      testFileInRes = localFileProviderApi.getContent(store, path);
      assertEquals(testFullContent, IOUtils.toString(testFileInRes, (Charset) null));
      
      // replace :
      res = localFileProviderApi.putContent(store, path, new ByteArrayInputStream(testContent.getBytes()));
      assertEquals(path, res.getPath());
      testFileInRes = localFileProviderApi.getContent(store, path);
      assertEquals(testContent, IOUtils.toString(testFileInRes, (Charset) null));
      
      // append using random access :
      long previousLength = testContent.getBytes().length;
      res = localFileProviderApi.appendContent(store, path, previousLength, new ByteArrayInputStream(testAppendContent.getBytes()));
      assertEquals(path, res.getPath());
      testFileInRes = localFileProviderApi.getContent(store, path);
      assertEquals(testFullContent, IOUtils.toString(testFileInRes, (Charset) null));
      
      // TODO list, delete ?!
      
      // test delete :
      localFileProviderApi.deleteContent(store, path);
      try {
         localFileProviderApi.getContent(store, path);
         fail("content should not exist anymore");
      } catch (RuntimeException e) {
         assertTrue(e.getMessage().contains("Can't find"));
      }
      
   }
   
}
