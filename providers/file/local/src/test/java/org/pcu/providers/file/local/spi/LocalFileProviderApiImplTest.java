package org.pcu.providers.file.local.spi;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pcu.providers.file.api.PcuFileApi;
import org.pcu.providers.file.api.PcuFileResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * TODO extend it to inject REST client proxy and test also REST API
 * @author mardut
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={LocalFileProviderConfiguration.class},
   initializers = ConfigFileApplicationContextInitializer.class)
@ActiveProfiles("test")
public class LocalFileProviderApiImplTest  {

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
   
   @Test
   public void testHashAsId() throws Exception {
      PcuFileResult res = localFileProviderApi.storeContent(store, new ByteArrayInputStream(testContent.getBytes()));
      String path = res.getPath();
      InputStream testInRes = localFileProviderApi.getContent(store, path);
      assertEquals(testContent, IOUtils.toString(testInRes, (Charset) null));
      
      // TODO check that can't be appended

      // check that not written again in same store :
      long firstWriteTime = localFileProviderApiImpl.getContentFile(store, path).lastModified();
      Thread.sleep(1);
      localFileProviderApi.storeContent(store, new ByteArrayInputStream(testContent.getBytes()));
      long secondWriteTime = localFileProviderApiImpl.getContentFile(store, path).lastModified();
      assertEquals(firstWriteTime, secondWriteTime);
      
      // check that can still be written in another store :
      String anotherStore = "anotherStore";
      Thread.sleep(1);
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

      localFileProviderApi.deleteContent(store, path);
      
      // check none yet :
      try {
         localFileProviderApi.getContent(store, path);
         fail("content should not exist yet");
      } catch (RuntimeException e) {
         assertTrue(e.getMessage().contains("Can't find content"));
      }
      
      // create using append :
      PcuFileResult res = localFileProviderApi.appendContent(store, path, null, new ByteArrayInputStream(testContent.getBytes()));
      InputStream testFileInRes = localFileProviderApi.getContent(store, path);
      assertEquals(testContent, IOUtils.toString(testFileInRes, (Charset) null));
      
      // append :
      res = localFileProviderApi.appendContent(store, path, null, new ByteArrayInputStream(testAppendContent.getBytes()));
      testFileInRes = localFileProviderApi.getContent(store, path);
      assertEquals(testFullContent, IOUtils.toString(testFileInRes, (Charset) null));
      
      // replace :
      res = localFileProviderApi.putContent(store, path, new ByteArrayInputStream(testContent.getBytes()));
      testFileInRes = localFileProviderApi.getContent(store, path);
      assertEquals(testContent, IOUtils.toString(testFileInRes, (Charset) null));
      
      // append using random access :
      long previousLength = testContent.getBytes().length;
      res = localFileProviderApi.appendContent(store, path, previousLength, new ByteArrayInputStream(testAppendContent.getBytes()));
      testFileInRes = localFileProviderApi.getContent(store, path);
      assertEquals(testFullContent, IOUtils.toString(testFileInRes, (Charset) null));
      
      // TODO list, delete ?!

      localFileProviderApi.deleteContent(store, path);
      
      // check none anymore :
      try {
         localFileProviderApi.getContent(store, path);
         fail("content should not exist anymore");
      } catch (RuntimeException e) {
         assertTrue(e.getMessage().contains("Can't find content"));
      }
      
   }
   
}
