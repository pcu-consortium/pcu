package org.pcu.features.search.pipeline;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pcu.features.connector.FileCrawler;
import org.pcu.features.connector.PcuConnectorConfiguration;
import org.pcu.providers.file.api.PcuFileApi;
import org.pcu.providers.metadata.api.PcuMetadataApi;
import org.pcu.providers.search.api.PcuDocument;
import org.pcu.providers.search.api.PcuIndexResult;
import org.pcu.providers.search.api.PcuSearchEsClientApi;
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
 * WARNING requires ElasticSearch 5.5 AND Kafka 0.11.0.1 to have been started independently.
 * 
 * TODO do this test with AND without the proxy client 
 * @author mardut
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={PcuSearchPipelineConfiguration.class,
      PcuConnectorConfiguration.class, ESSearchProviderConfiguration.class},
   initializers = ConfigFileApplicationContextInitializer.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.DEFINED_PORT, properties="server.port=45665")
//rather than @SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT, properties="server_port=45665")
//which would require listening to an ApplicationEvent and therefore using a Provider pattern
//see https://docs.spring.io/spring-boot/docs/current/reference/html/howto-embedded-servlet-containers.html https://stackoverflow.com/questions/30312058/spring-boot-how-to-get-the-running-port
//or with autoconf redefine cxf.jaxrs.client.address
@ActiveProfiles("test")
public class PcuSearchApiPipelineImplTest {
   @LocalServerPort
   protected int serverPort;
   
   @Autowired @Qualifier("pcuSearchIndexPipelineApiRestClient") //@Qualifier("pcuSearchApiRestClient") //@Qualifier("pcuSearchApiPipelineImpl")
   private PcuSearchIndexPipelineApi searchApi;

   @Autowired @Qualifier("pcuSearchEsApiRestClient") //@Qualifier("pcuSearchEsApiImpl") //pcuSearchEsApiRestClient
   private PcuSearchEsClientApi searchEsApi;
   @Autowired @Qualifier("pcuFileApiRestClient") //defaultFileProviderApi LocalFileProviderApiImpl pcuFileApiRestClient
   private PcuFileApi fileApi;
   @Autowired @Qualifier("defaultMetadataExtractorApi")
   private PcuMetadataApi metadataExtractorApi;
   
   /**
    * doesn't work as is, because doc is validated against file avro schema
    */
   //@Test
   public void test() {
      String index = "files";
      PcuDocument pcuDoc = new PcuDocument();
      pcuDoc.setType("file");
      pcuDoc.setId("myid"); // TODO gen
      // props following file.avsc :
      pcuDoc.setProperties(new LinkedHashMap<>());
      LinkedHashMap<String, Object> file = new LinkedHashMap<>();
      pcuDoc.getProperties().put("file", file);
      file.put("name", "a.doc");
      PcuIndexResult res = searchApi.index(index, pcuDoc);
      assertNotNull(res);
      // TODO check res
      
      // TODO more, from ES client API test
   }
   
   @Test
   ///@Ignore // uncomment if no kafka
   public void testSimulateCrawl() throws IOException {
      // prepare file to crawl :
      String testContent = "Spark ETL Kafka pipeline";
      File testFile = this.createTestFile(testContent);
      
      // init crawler :
      FileCrawler fileCrawler = new FileCrawler();
      fileCrawler.setIndex("files"); // TODO test
      fileCrawler.setType("file");
      fileCrawler.setSearchEsApi(searchEsApi);
      fileCrawler.setSearchApi(searchApi);
      fileCrawler.setFileApi(fileApi);
      fileCrawler.setMetadataExtractorApi(metadataExtractorApi);
      fileCrawler.init();
      
      // TODO more, from ES client API test

      // (upload content)
      
      // index crawled metadata :
      PcuDocument pcuDoc = fileCrawler.buildPcuDocument(testFile, testContent, "NO_CONTENT_UPLOADED");
      PcuIndexResult indexRes = searchApi.index(fileCrawler.getIndex(), pcuDoc);
      assertNotNull(indexRes);
   }
   
   /**
    * TODO reuse
    * @param testContent
    * @return
    * @throws IOException
    */
   public File createTestFile(String testContent) throws IOException {
      File testFile = File.createTempFile("pcu_test_", ".doc");
      testFile.deleteOnExit();
      try (FileOutputStream testFileOut = new FileOutputStream(testFile)) {
         IOUtils.write(testContent, testFileOut, (Charset) null);
      }
      return testFile;
   }
   
}
