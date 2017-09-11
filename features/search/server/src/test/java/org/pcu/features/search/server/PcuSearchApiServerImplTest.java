package org.pcu.features.search.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pcu.providers.file.api.PcuFileApi;
import org.pcu.providers.file.api.PcuFileResult;
import org.pcu.providers.search.api.PcuDocument;
import org.pcu.providers.search.api.PcuIndexResult;
import org.pcu.providers.search.api.PcuSearchApi;
import org.pcu.providers.search.elasticsearch.spi.ESSearchProviderConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Test of PCU search API and features
 * WARNING requires ElasticSearch 5.5 to have been started independently.
 * 
 * TODO do this test with AND without the proxy client 
 * @author mardut
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={PcuSearchServerConfiguration.class,ESSearchProviderConfiguration.class},
   initializers = ConfigFileApplicationContextInitializer.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.DEFINED_PORT, properties="server.port=45665")
//rather than @SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT, properties="server_port=45665")
//which would require listening to an ApplicationEvent and therefore using a Provider pattern
//see https://docs.spring.io/spring-boot/docs/current/reference/html/howto-embedded-servlet-containers.html https://stackoverflow.com/questions/30312058/spring-boot-how-to-get-the-running-port
//or with autoconf redefine cxf.jaxrs.client.address
@ActiveProfiles("test")
public class PcuSearchApiServerImplTest /*extends PcuSearchApiClientTest */{
   @LocalServerPort
   protected int serverPort;
   
   @Autowired @Qualifier("pcuSearchApiRestClient") //@Qualifier("pcuSearchApiImpl")
   private PcuSearchApi searchApi;

   @Autowired @Qualifier("pcuFileApiRestClient")
   private PcuFileApi fileApi;
   
   @Test
   public void testIndex() {
      String index = "files";
      PcuDocument pcuDoc = new PcuDocument();
      pcuDoc.setType("file");
      pcuDoc.setId("myid"); // TODO gen
      pcuDoc.setProperties(new LinkedHashMap<>());
      pcuDoc.getProperties().put("name", "a.doc");
      PcuIndexResult res = searchApi.index(index, pcuDoc);
      // TODO check res
      
      // TODO more, from ES client API test
   }

   
   @Test
   public void testSimulateCrawl() throws Exception {
      // prepare file to crawl :
      String store = "mystore"; // TODO
      //String path = "myfile.doc"; // TODO
      String testContent = "My test content";
      File testFile = File.createTempFile("pcu_test_", ".doc");
      testFile.deleteOnExit();
      try (FileOutputStream testFileOut = new FileOutputStream(testFile)) {
         IOUtils.write(testContent, testFileOut, (Charset) null);
      }
      
      // 1. upload crawled content :
      PcuFileResult fileRes;
      try (FileInputStream testFileIn = new FileInputStream(testFile)) {
         fileRes = fileApi.storeContent(store,testFileIn);
      }
      // and check :
      InputStream testFileInRes = fileApi.getContent(store, fileRes.getPath());
      assertEquals(testContent, IOUtils.toString(testFileInRes, (Charset) null));
      
      // 2. index crawled metadata :
      String index = "files";
      PcuDocument pcuDoc = new PcuDocument();
      pcuDoc.setType("file");
      pcuDoc.setId("myid"); // TODO gen
      LinkedHashMap<String, Object> content = new LinkedHashMap<>();
      pcuDoc.setProperties(new LinkedHashMap<>());
      pcuDoc.getProperties().put("name", "a.doc");
      pcuDoc.getProperties().put("content", content); // TODO of type "file"
      content.put("path", fileRes.getPath());
      content.put("hash", fileRes.getPath()); // why not
      content.put("fulltext", testContent); // parsed clist-side by tika in connector crawler
      PcuIndexResult res = searchApi.index(index, pcuDoc);
   }
   
}
