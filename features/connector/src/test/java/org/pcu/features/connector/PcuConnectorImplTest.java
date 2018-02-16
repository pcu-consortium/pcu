package org.pcu.features.connector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.avro.AvroTypeException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pcu.features.configuration.api.GetConfigurationResult;
import org.pcu.features.configuration.api.PcuConfigurationApi;
import org.pcu.features.connector.crawler.CrawlUtils;
import org.pcu.features.connector.crawler.Crawler2;
import org.pcu.features.connector.crawler.FileCrawlBatch;
import org.pcu.features.connector.crawler.FileCrawler;
import org.pcu.features.connector.crawler.JDBCCrawlBatchBase;
import org.pcu.features.search.server.meta.PcuField;
import org.pcu.features.search.server.meta.PcuIndex;
import org.pcu.features.search.server.meta.PcuIndexField;
import org.pcu.features.search.server.meta.PcuType;
import org.pcu.platform.model.ModelServiceImpl;
import org.pcu.platform.model.PcuModelConfiguration;
import org.pcu.providers.file.api.PcuFileApi;
import org.pcu.providers.file.api.PcuFileResult;
import org.pcu.providers.file.local.spi.LocalFileProviderConfiguration;
import org.pcu.providers.metadata.api.PcuMetadataApi;
import org.pcu.providers.search.api.PcuDocument;
import org.pcu.providers.search.api.PcuIndexResult;
import org.pcu.providers.search.api.PcuSearchApi;
import org.pcu.providers.search.api.PcuSearchEsClientApi;
import org.pcu.providers.search.elasticsearch.spi.ESSearchProviderConfiguration;
import org.pcu.search.elasticsearch.api.ESApiException;
import org.pcu.search.elasticsearch.api.ElasticSearchClientApi;
import org.pcu.search.elasticsearch.api.query.ESQuery;
import org.pcu.search.elasticsearch.api.query.ESQueryMessage;
import org.pcu.search.elasticsearch.api.query.Hit;
import org.pcu.search.elasticsearch.api.query.clause.PrefixFieldParameters;
import org.pcu.search.elasticsearch.api.query.clause.RangeFieldParameters;
import org.pcu.search.elasticsearch.api.query.clause.bool;
import org.pcu.search.elasticsearch.api.query.clause.multi_match;
import org.pcu.search.elasticsearch.api.query.clause.prefix;
import org.pcu.search.elasticsearch.api.query.clause.range;
import org.pcu.search.elasticsearch.api.query.clause.terms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.zaxxer.hikari.HikariDataSource;


/**
 * Test of PCU search API and features
 * WARNING requires ElasticSearch 5.5 to have been started independently.
 * 
 * TODO do this test with AND without the proxy client 
 * @author mardut
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={PcuConnectorImplTest.JdbcConnectorTestConfiguration.class, PcuConnectorConfiguration.class, // client
      ESSearchProviderConfiguration.class, LocalFileProviderConfiguration.class, PcuModelConfiguration.class}, // server
      initializers = ConfigFileApplicationContextInitializer.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.DEFINED_PORT, properties="server.port=45665")
//rather than @SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT, properties="server_port=45665")
//which would require listening to an ApplicationEvent and therefore using a Provider pattern
//see https://docs.spring.io/spring-boot/docs/current/reference/html/howto-embedded-servlet-containers.html https://stackoverflow.com/questions/30312058/spring-boot-how-to-get-the-running-port
//or with autoconf redefine cxf.jaxrs.client.address
@ActiveProfiles("test") // TODO StarterProfiles.TEST ... in -common
//@Sql({ "classpath:/sql/data.sql" }) // TODO move ; NOT "classpath:/sql/cleanup.sql" since not created by hibernate previously BUT THEN FAILS WHEN SEVERAL TESTS
// NOO MOVED TO PcuConnectorConfiguration but using DataSourceInitializer since @Sql only works within tests
public class PcuConnectorImplTest /*extends PcuSearchApiClientTest */{
   @LocalServerPort
   protected int serverPort;

   @Autowired @Qualifier("pcuSearchEsApiRestClient") //@Qualifier("pcuSearchEsApiImpl") //pcuSearchEsApiRestClient //defaultSearchProviderEsApi
   private PcuSearchEsClientApi searchEsApi;
   @Autowired @Qualifier("pcuSearchApiRestClient") //@Qualifier("pcuSearchApiImpl") //pcuSearchApiRestClient
   private PcuSearchApi searchApi;
   @Autowired @Qualifier("defaultFileProviderApi") //defaultFileProviderApi LocalFileProviderApiImpl pcuFileApiRestClient
   private PcuFileApi fileApi;
   @Autowired @Qualifier("defaultMetadataExtractorApi") // possibly defaultMetadataExtractorRestClient ?
   private PcuMetadataApi metadataExtractorApi;

   @Autowired @Qualifier("pcuApiDateTimeFormatter")
   private DateTimeFormatter pcuApiDateTimeFormatter;
   @Autowired @Qualifier("pcuApiMapper")
   private ObjectMapper pcuApiMapper;
   @Autowired @Qualifier("pcuApiPrettyMapper")
   private ObjectMapper pcuApiPrettyMapper;

   ///@Resource(name="pcuConnectorApiRestClient") // NOT @Autowired else NoSuchBean because proxy has not the same class https://stackoverflow.com/questions/15614786/could-not-autowire-jaxrs-client
   ///private PcuConnectorApi pcuConnectorApi;
   @Resource(name="pcuConfigurationApiRestClient") // NOT @Autowired else NoSuchBean because proxy has not the same class https://stackoverflow.com/questions/15614786/could-not-autowire-jaxrs-client
   private PcuConfigurationApi pcuConfigurationApi;
   @Autowired
   private ModelServiceImpl modelService;
   @Autowired @Qualifier("pcuApiTypedMapper")
   private ObjectMapper pcuApiTypedMapper;

   /** for tests */
   @Autowired @Qualifier("elasticSearchRestClient")
   private ElasticSearchClientApi elasticSearchRestClient;
   @Autowired @Qualifier("elasticSearchMapper")
   private ObjectMapper elasticSearchMapper;
   
   @Autowired
   private JdbcTemplate jdbcConnectorJdbcTemplate;
   
   @Configuration
   public static class JdbcConnectorTestConfiguration {
      @Profile({ "standalone", "test" }) // TODO StarterProfiles.TEST ... in -common
      @PropertySource("classpath:application-defaults.properties") // Not loaded by naming convention
      @Configuration
      static class StandaloneDatabaseConfig {
         @Bean
         public DataSource jdbcConnectorDataSource(final Environment env) {
            final HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(env.getRequiredProperty("pcu.connector.jdbc.url"));
            ds.setUsername(env.getRequiredProperty("pcu.connector.jdbc.username"));
            return ds;
         }
         @Bean
         public JdbcTemplate jdbcConnectorJdbcTemplate(final Environment env, DataSource jdbcConnectorDataSource) {
            final JdbcTemplate jdbcTemplate = new JdbcTemplate();
            jdbcTemplate.setDataSource(jdbcConnectorDataSource);
            return jdbcTemplate;
         }
      }

      /*
      @Profile(StarterProfiles.STAGING)
      @Configuration
      static class StagingDatabaseConfig {
         @Bean
         public DataSource dataSource(final Environment env) {
            final HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(env.getRequiredProperty("psql.jdbcurl"));
            ds.setUsername(env.getRequiredProperty("psql.username"));
            return ds;
         }
      }

      @Profile(StarterProfiles.PRODUCTION)
      @Configuration
      static class ProuctionDatabaseConfig {
         @Bean
         public DataSource dataSource(final Environment env) {
            final HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(env.getRequiredProperty("psql.jdbcurl"));
            ds.setUsername(env.getRequiredProperty("psql.username"));
            return ds;
         }
      }
      */
      
   }
   
   @Test
   public void testIndex() throws Exception {
      String index = "files";
      PcuDocument pcuDoc = new PcuDocument();
      pcuDoc.setType("file");
      pcuDoc.setId("myid"); // TODO gen
      // testing very long id in HTTP URL :
      //pcuDoc.setId("mdutoo-laptop/home/mardut/dev/pcu/workspace/pcu/features/search/server/src/main/java/org/pcu/features/search/server/deeper/pcu/features/search/server/src/main/java/org/pcu/features/search/server/PcuSearchApiServerImpl.java");
      // NOO ES can't handle slash in HTTP url, must be encoded : No handler found for uri [/files/file/mydir/myfile.doc] and method [PUT] https://stackoverflow.com/questions/35025401/no-handler-found-for-uri-index-type-and-method-put
      pcuDoc.setId("mdutoo-laptop/home/mardut/dev/pcu/workspace/pcu/features/search/server/src/main/java/org/pcu/features/search/server/deeper/pcu/features/search/server/src/main/java/org/pcu/features/search/server/PcuSearchApiServerImpl.java");
      // TODO Q or only encode in ESSearchProviderImpl ??
      pcuDoc.setProperties(new LinkedHashMap<>());
      pcuDoc.getProperties().put("name", "a.doc");
      PcuIndexResult res = searchApi.index(index, pcuDoc);
      System.out.println("indexed\n" + pcuApiPrettyMapper.writeValueAsString(pcuDoc));
      System.out.println("found\n" + pcuApiPrettyMapper.writeValueAsString(searchApi.get(index, pcuDoc.getId())));
      // TODO check res
      
      // TODO more, from ES client API test
   }
   @Test
   public void reinit() throws Exception {
      // done in postConstruct
   }
   
   @Autowired
   private PcuConnector pcuConnector;
   @Test
   public void testCrawler2full() {
      pcuConnector.defaultCrawl();
   }

   @Test
   public void testCrawler2() throws Exception {
      // prepare file to crawl :
      String testContent = "My test content";
      ///File testFile = createTestFile(testContent);
      File testDir = new File("testDir");
      if (!testDir.exists()) testDir.mkdirs();
      File testFile = File.createTempFile("pcu_test_", ".txt", testDir);
      testFile.deleteOnExit();
      try (FileOutputStream testFileOut = new FileOutputStream(testFile)) {
         IOUtils.write(testContent, testFileOut, (Charset) null);
      }
      
      // init crawler :
      Crawler2 fileCrawler = PcuConnector.buildDefaultSampleFileCrawler(pcuConnector).getCrawler();
      fileCrawler.getToBeCrawledQeue().clear();
      fileCrawler.getToBeCrawledQeue().add(new FileCrawlBatch(testDir, fileCrawler));
      ///fileCrawler.getToBeCrawledQeue().add(new CrawlBatch(new File("/home/mardut/Documents"), fileCrawler));
      fileCrawler.init();
      
      fileCrawler.crawlIteration();
      fileCrawler.crawlIteration();
      fileCrawler.crawlIteration();
   }
   
   @Test
   public void testCrawler2Jdbc() throws Exception {
      // init crawler :
      JDBCCrawlBatchBase jdbcCrawlBatch = PcuConnector.buildDefaultPersonAvroDrivenJDBCCrawler(pcuConnector);
      Crawler2 crawler = jdbcCrawlBatch.getCrawler();
      jdbcCrawlBatch.setBatchSize(2); // to be able to test batching with 3 items

      crawler.init();
      
      crawler.crawlIteration();
      crawler.crawlIteration();
   }

   @Test
   public void testCrawler2Conf() throws Exception {
      // init crawler :
      Crawler2 crawler = PcuConnector.buildDefaultPersonAvroDrivenJDBCCrawler(pcuConnector).getCrawler();
      
      LinkedHashMap<String, Object> jdbcProperties = new LinkedHashMap<String,Object>();
      jdbcProperties.put("jdbcUrl", "jdbc:h2:mem:jdbcConnectorTest;DATABASE_TO_UPPER=false;MODE=PostgreSQL;DB_CLOSE_ON_EXIT=FALSE\";DB_CLOSE_DELAY=-1");
      jdbcProperties.put("username", "h2");
      jdbcProperties.put("userkokoname", "ko");
      ((JDBCCrawlBatchBase) crawler.getToBeCrawledQeue().peek()).setJdbcProperties(jdbcProperties); // to allow ser / deser of JdbcTemplate and its datasource
      
      // YAML :
      ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
      mapper.enableDefaultTyping(); 
      ///jdbcCrawlBatch.setJdbcTemplate(null); // TODO ignore & bloom else seri KO
      try {
         //String jdbcCrawlBatchYaml = mapper.writeValueAsString(jdbcCrawlBatch); // NO MUST BE WITHIN A LIST else class name is not serialized along http://www.baeldung.com/jackson-inheritance
         String crawlerYaml = mapper.writeValueAsString(crawler);
         System.out.println("testCrawler2Conf :\n" + crawlerYaml);
         Crawler2 parsedCrawler = mapper.readValue(crawlerYaml, Crawler2.class);
         assertTrue(parsedCrawler.getToBeCrawledQeue().peek() instanceof JDBCCrawlBatchBase);
         JDBCCrawlBatchBase jdbcCrawlBatch = (JDBCCrawlBatchBase) crawler.getToBeCrawledQeue().peek();
         assertTrue(jdbcCrawlBatch.getJdbcTemplate() != null);
      } catch (Exception e) {
         log.error("Error setting up crawler TODO", e);
      }
      
      // JSON that is ElasticSearch-compatible :
      // (and not
      ///pcuApiTypedMapper.setPropertyInclusion(JsonInclude., contentIncl));
      pcuApiTypedMapper.writeValueAsString(crawler);
   }
   @Test
   public void testCrawler2ConfDefault() throws ESApiException, IOException {
      // 1. hardcoded default conf :
      assertEquals(PcuConnector.hardcodedDefaultConfId, pcuConnector.getConfId());
      assertEquals(2, pcuConnector.getConf().getComponents().size());
      try {
         pcuConfigurationApi.get(PcuConnector.confType, pcuConnector.getConfId(), null);
         fail("conf should not exist in mgr");
      } catch (ESApiException esapiex) {
         assertEquals(404, esapiex.getStatus()); // hardcoded means not on server !
      }
      
      // 2. using existing template :
      pcuConnector.setTemplateId("connector-default-test-template");
      // checking that template has been bootstrapped :
      /*try { // uncomment if needed to get back to a correct state
         pcuConfigurationApi.delete(PcuConnector.confType, pcuConnector.getConnectorTemplateId(), null);
      } catch (ESApiException esapiex) {
         assertEquals(404, esapiex.getStatus()); // i.e. not yet uploaded, but other errors are not allowed here
      }*/
      GetConfigurationResult pcuConfTemplateRes;
      try {
         pcuConfTemplateRes = pcuConfigurationApi.get(PcuConnector.confType, pcuConnector.getTemplateId(), true);
         // testing PcuConfigurationApi btw :
         assertEquals(pcuConnector.getTemplateId(), pcuConfTemplateRes.get_id());
         assertEquals(pcuConnector.confType, pcuConfTemplateRes.get_type());
         assertEquals(pcuConnector.CONF_INDEX, pcuConfTemplateRes.get_index());
      } catch (ESApiException esapiex) {
         fail("template should exist in mgr " + esapiex.getMessage());
      }
      // delete conf if already uploaded (by previous test...) :
      try {
         pcuConfigurationApi.delete(PcuConnector.confType, pcuConnector.getConfId(), null);
      } catch (ESApiException esapiex) {
         assertEquals(404, esapiex.getStatus()); // i.e. not yet uploaded, but other errors are not allowed here
      }
      // re trigger setup :
      //pcuConnector.setRunAtStartup(false);
      //pcuConnector.onApplicationEvent(null);
      pcuConnector.setConfId(null); // rather than hardcoded conf id
      pcuConnector.setup();
      // check that conf has been created from template and uploaded :
      try {
         GetConfigurationResult pcuConfRes = pcuConfigurationApi.get(PcuConnector.confType, pcuConnector.getConfId(), true);
         // testing PcuConfigurationApi btw :
         assertEquals(pcuConnector.getConfId(), pcuConfRes.get_id());
         assertEquals(CrawlUtils.macAddress(), pcuConfRes.get_id()); // check machine-specific default id
         assertEquals(pcuConnector.confType, pcuConfRes.get_type());
         assertEquals(pcuConnector.CONF_INDEX, pcuConfRes.get_index());
         // TODO write value
      } catch (ESApiException esapiex) {
         fail("conf should exist in mgr");
      }

      // 3. using existing conf :
      // re trigger setup :
      //pcuConnector.setRunAtStartup(false);
      //pcuConnector.onApplicationEvent(null);
      pcuConnector.setup();
      // check that conf has been created from template and uploaded :
      try {
         GetConfigurationResult pcuConfRes = pcuConfigurationApi.get(PcuConnector.confType, pcuConnector.getConfId(), true);
         // testing PcuConfigurationApi btw :
         assertEquals(pcuConnector.getConfId(), pcuConfRes.get_id());
         assertEquals(pcuConnector.confType, pcuConfRes.get_type());
         assertEquals(pcuConnector.CONF_INDEX, pcuConfRes.get_index());
         // TODO write value
      } catch (ESApiException esapiex) {
         fail("conf should exist in mgr");
      }
   }

   private PathMatchingResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver(); // TODO @Bean
   @Test
   public void testCrawler2AvroJdbc() throws Exception {
      // init crawler :
      JDBCCrawlBatchBase jdbcCrawlBatch = PcuConnector.buildDefaultPersonAvroDrivenJDBCCrawler(pcuConnector);
      Crawler2 crawler = jdbcCrawlBatch.getCrawler();
      jdbcCrawlBatch.setBatchSize(2); // to be able to test batching with 3 items

      crawler.init();
      
      crawler.crawlIteration();
      crawler.crawlIteration();
   }

   @Test
   @Ignore // remove this to use it
   public void testSimulateCrawlHome() throws IOException {
      // init crawler :
      FileCrawlBatch fileCrawlBatch = PcuConnector.buildDefaultSampleFileCrawler(pcuConnector);
      ///fileCrawler.seFilter();
      FileFilter txtFileFilter = new SuffixFileFilter(".txt"); // ONLY for .txt else too slow & big for demo
      
      // dataset :
      recursiveCrawlFolder(fileCrawlBatch, new File("/home/mardut/Documents/projets/pcu/WP7 search/dataset"), txtFileFilter);
      // pcu
      //simulateCrawlFolder(fileCrawler, new File("/home/mardut/Documents/projets/pcu"));
      //simulateCrawlFolder(fileCrawler, new File("/home/mardut/dev/pcu"));
      // home :
      recursiveCrawlFolder(fileCrawlBatch, new File(System.getProperty("user.home")), txtFileFilter); // + File.separator + "Documents" // "/home/mardut/dev/pcu/workspace"
   }
   public void simulateCrawlFolder(FileCrawlBatch fileCrawlBatch, File folder) {
      this.recursiveCrawlFolder(fileCrawlBatch, folder, null); // , PcuConnectorImplTest::simulateCrawlFile
   }
   protected static final Logger log = LoggerFactory.getLogger(PcuConnectorImplTest.class);
   public void recursiveCrawlFolder(FileCrawlBatch fileCrawlBatch, File folder, FileFilter fileFilter) {
      for (File file : folder.listFiles()) {
         if (file.isFile()) {
            if (file.canRead()) {
               if (fileFilter == null || fileFilter.accept(file)) {
                  try {
                     log.debug("crawling " + file.getAbsolutePath());
                     simulateCrawlFile(fileCrawlBatch, file);
                  } catch (Exception e) {
                     // test fails but it still works
                  }
               }
            }
         } else if (file.isDirectory()) {
            if (file.canRead()) {
               recursiveCrawlFolder(fileCrawlBatch, file, fileFilter);
            }
         } // else non-regular files : symlinks (TODO resolve), devices, pipes, sockets https://stackoverflow.com/a/21224032
      }
   }
   public void simulateCrawlFile(FileCrawlBatch fileCrawlBatch, File file) throws Exception {
      try {
         System.err.println("crawling " + file.getAbsolutePath());
         ///String content = null; // disables fulltext & checks
         String content = IOUtils.toString(new FileInputStream(file), (Charset) null);
         simulateCrawl(fileCrawlBatch, file, content); // null
      } catch (AssertionError e) {
         // test fails but it still works
      }
   }
   
   /**
    * The client connector's crawler should work this way :
    * @throws Exception
    */
   @Test
   public void testSimulateCrawl() throws Exception {
      // prepare file to crawl :
      String testContent = "My test content";
      File testFile = createTestFile(testContent);
      
      // init crawler :
      FileCrawlBatch fileCrawlBatch = PcuConnector.buildDefaultSampleFileCrawler(pcuConnector);
      
      simulateCrawl(fileCrawlBatch, testFile, testContent);
   }
   public File createTestFile(String testContent) throws IOException {
      File testFile = File.createTempFile("pcu_test_", ".doc");
      testFile.deleteOnExit();
      try (FileOutputStream testFileOut = new FileOutputStream(testFile)) {
         IOUtils.write(testContent, testFileOut, (Charset) null);
      }
      return testFile;
   }

   /**
    * 
    * @param fileCrawler
    * @param testFile
    * @param testContent null disables fulltext & checks
    * @throws Exception
    */
   public void simulateCrawl(FileCrawlBatch fileCrawlBatch, File testFile, String testContent) throws Exception {
      Crawler2 fileCrawler = fileCrawlBatch.getCrawler();
      
      // 1. upload crawled content :
      PcuFileResult fileRes;
      try (FileInputStream testFileIn = new FileInputStream(testFile)) {
         fileRes = fileApi.storeContent(fileCrawler.getContentStore(),testFileIn);
      }
      // and check :
      InputStream testFileInRes = fileApi.getContent(fileCrawler.getContentStore(), fileRes.getPath());
      if (testContent != null)
      assertEquals(testContent, IOUtils.toString(testFileInRes, (Charset) null));
      
      // 2. index crawled metadata :

      PcuDocument pcuDoc = fileCrawlBatch.buildPcuDocument(testFile, null, testContent, fileRes.getPath(), null);
      
      PcuIndexResult indexRes = searchApi.index(fileCrawler.getIndex(), pcuDoc);
      
      // find : (TODO by path...)
      PcuDocument res = searchApi.get(fileCrawler.getIndex(), pcuDoc.getId());
      assertEquals(pcuDoc.getId(), res.getId());
      //assertEquals(pcuDoc.getVersion(), res.getVersion());
      // get content :
      String[] storePath = ((String) pcuDoc.getByPath("content.store_path")).split("/", 2);
      InputStream foundContent = fileApi.getContent(storePath[0], storePath[1]);
      if (testContent != null)
      assertEquals(testContent, IOUtils.toString(foundContent, (Charset) null));
      
      // TODO test versionS & optimistic locking
      
      // test query :
      // NB. searchInType() and not search() else also in indexes outside files which have not been reinited by the test
      String index = "files";
      String type = "file";
      
      // query - on date range :
      ESQueryMessage dateRangeMsg = new ESQueryMessage();
      range dateRange = new range();
      dateRangeMsg.setQuery(dateRange);
      RangeFieldParameters rangeParams = new RangeFieldParameters();
      dateRange.setRangeParameters("file.last_modified", rangeParams);
      rangeParams.setGt(ZonedDateTime.parse("2017-01-01T00:00:01.000+0000", pcuApiDateTimeFormatter));
      rangeParams.setLte(ZonedDateTime.ofInstant(Instant.ofEpochMilli(testFile.lastModified()), ZoneId.systemDefault()));
      List<Hit> hits = searchEsApi.searchInType(index, type, dateRangeMsg, null, null, null).getHits().getHits();
      assertTrue(!hits.isEmpty());
      assertTrue(pcuDoc.getId().equals(hits.get(0).get_id()));
      rangeParams.setLt(ZonedDateTime.parse("2016-01-01T00:00:01.000+0000", pcuApiDateTimeFormatter));
      rangeParams.setGt(ZonedDateTime.parse("2015-01-01T00:00:01.000+0000", pcuApiDateTimeFormatter)); // not null because crawling samples write 0 date i.e. 1970
      hits = searchEsApi.searchInType(index, type, dateRangeMsg, null, null, null).getHits().getHits();
      assertTrue(hits.isEmpty());

      // query - on mimetype :
      ESQueryMessage mimetypeMsg = new ESQueryMessage();
      multi_match mimetypeMultiMatch = new multi_match();
      mimetypeMultiMatch.setFields(Arrays.asList(new String[] {"http.mimetype"}));
      mimetypeMultiMatch.setQuery((String) pcuDoc.getByPath("http.mimetype"));
      mimetypeMsg.setQuery(mimetypeMultiMatch);
      hits = searchEsApi.searchInType(index, type, mimetypeMsg , null, null, null).getHits().getHits();
      assertTrue(!hits.isEmpty());
      assertTrue(pcuDoc.getId().equals(hits.get(0).get_id()));

      // query - on path (prefix) :
      ESQueryMessage pathMsg = new ESQueryMessage();
      prefix pathPrefix = new prefix();
      PrefixFieldParameters prefixParameters = new PrefixFieldParameters();
      String testFilePath = (String) pcuDoc.getByPath("file.path");
      String testFileParentPath = testFilePath.substring(0, testFilePath.lastIndexOf('/')); 
      prefixParameters.setValue(testFileParentPath);
      pathPrefix.setPrefixParameters("file.path", prefixParameters );
      pathMsg.setQuery(pathPrefix);
      hits = searchEsApi.searchInType(index, type, pathMsg , null, null, null).getHits().getHits();
      assertTrue(!hits.isEmpty());
      assertTrue(pcuDoc.getId().equals(hits.get(0).get_id()));
      
      // query - on path (keyword) :
      terms pathTerms = new terms();
      //pathTerms.setTermListOrLookupMap("file.path", Arrays.asList(new Object[] { "tmp", "pcu_test_5114232402156243118.doc" })); // KO
      //pathTerms.setTermListOrLookupMap("file.path", Arrays.asList(testFileParentPath.split("/")).stream().filter(pElt -> pElt.length() != 0).collect(Collectors.toList())); // KO
      //pathTerms.setTermListOrLookupMap("file.path", Arrays.asList(new Object[] { "tmp" })); // KO
      //pathTerms.setTermListOrLookupMap("file.path", Arrays.asList(new Object[] { "/tmp/pcu_test_5114232402156243118.doc" })); // also OK
      pathTerms.setTermListOrLookupMap("file.path.tree", Arrays.asList(new Object[] { "/tmp" })); // .tree !! https://www.elastic.co/guide/en/elasticsearch/guide/current/denormalization-concurrency.html
      pathMsg.setQuery(pathTerms);
      hits = searchEsApi.searchInType(index, type, pathMsg , null, null, null).getHits().getHits();
      assertTrue(!hits.isEmpty());
      assertTrue(pcuDoc.getId().equals(hits.get(0).get_id()));
      
      // query without being admin, not having right :
      ESQueryMessage rightsFilteredMsg = new ESQueryMessage();
      bool rightsFilteredBool = new bool();
      terms rightsTerms = new terms();
      List<String> userAuthorities = new ArrayList<String>();
      userAuthorities.add("guest");
      userAuthorities.add("myuserid");
      rightsTerms.setTermListOrLookupMap("rights.ar", userAuthorities);
      rightsFilteredBool.setFilter(Arrays.asList(new ESQuery[] { rightsTerms }));
      rightsFilteredMsg.setQuery(rightsFilteredBool);
      hits = searchEsApi.searchInType(index, type, rightsFilteredMsg , null, null, null).getHits().getHits();
      assertTrue(hits.isEmpty());
      
      // query without being admin, having right :
      userAuthorities.add("myproject_group");
      hits = searchEsApi.searchInType(index, type, rightsFilteredMsg , null, null, null).getHits().getHits();
      assertTrue(!hits.isEmpty());
      assertTrue(pcuDoc.getId().equals(hits.get(0).get_id()));
      
      // X. simulate tailing a file :
      String testAppendContent = "\nand another test content";
      // create = first append (no random access)
      try (FileInputStream testFileIn = new FileInputStream(testFile)) {
         fileRes = fileApi.appendContent(fileCrawler.getContentStore(), (String) pcuDoc.getByPath("path"), null, testFileIn); // globalFilePath
      }
      if (testContent != null)
      assertEquals(testContent, IOUtils.toString(fileApi.getContent(fileCrawler.getContentStore(), fileRes.getPath()), (Charset) null));
      // and index meta :
      pcuDoc.setByPath("content.store_path", fileRes.getPath());
      indexRes = searchApi.index(fileCrawler.getIndex(), pcuDoc);
      // then, actual append (using random access) :
      fileRes = fileApi.appendContent(fileCrawler.getContentStore(), fileRes.getPath(), testFile.length(), new ByteArrayInputStream(testAppendContent.getBytes()));
      if (testContent != null)
      assertEquals(testContent + testAppendContent, IOUtils.toString(fileApi.getContent(fileCrawler.getContentStore(), fileRes.getPath()), (Charset) null));
      // NB. don't index metas anymore, or ONLY WHAT DOESN'T CHANGE, besides that at most length or modified MIGHT be updated using (even batch ?) event system
      
      // Y. simulate indexing an app's business entities :
      // TODO
   /*}

   @Test
   public void testSimulateDocumentMetamodelCheck() throws Exception {*/
      
      // check indexed data :
      assertNotEquals(0, modelService.getTypeSchemaMap().size());
      Schema schema = modelService.getTypeSchemaMap().get(pcuDoc.getType());
      assertNotNull(schema);

      System.out.println("validating\n" + pcuApiPrettyMapper.writeValueAsString(pcuDoc));
      
      GenericRecord pcuDocRec = modelService.validatePcuEntityAgainstAvroSchema(pcuDoc);
      assertEquals(pcuDocRec.get("name"), pcuDoc.getProperties().get("name"));
      
      // check missing field :
      Object oldValue = pcuDoc.getProperties().remove("path");
      try {
         modelService.validatePcuEntityAgainstAvroSchema(pcuDoc);
         fail("removing a field should be incompatible with schema");
      } catch (AvroTypeException atex) {
         assertTrue(atex.getMessage().contains("Expected string"));
         pcuDoc.getProperties().put("path", oldValue);
      }
      
      // check retrocompatibillity :
      // add a field :
      pcuDoc.getProperties().put("newfield", "value");
      try {
         modelService.validatePcuEntityAgainstAvroSchema(pcuDoc);
      } catch (AvroTypeException atex) {
         fail("should be able to read new value using old schema");
      }
      // change a field :
      oldValue = pcuDoc.setByPath("file.name", 123);
      try {
         modelService.validatePcuEntityAgainstAvroSchema(pcuDoc);
         fail("removing a field should be incompatible with schema");
      } catch (AvroTypeException atex) {
         assertTrue(atex.getMessage().contains("Expected string"));
         pcuDoc.setByPath("file.name", oldValue);
      }
      
      // TODO check avro oldSchema.compat(newSchema) : add, remove, change
      ///typeSchemaMap.get("file").setFields(fields);
      
      /////////////////////////////////////
      boolean testWrappingInheritanceMetamodel = false;
      if (testWrappingInheritanceMetamodel) {
      
      //build meta :
      PcuType fileType = new PcuType("file");
      PcuField nameField = new PcuField("name", "string");
      fileType.addField(nameField);
      pcuTypes.put(fileType.getName(), fileType);
      PcuIndex fileIndex = new PcuIndex("files");
      PcuIndexField nameIndexField = new PcuIndexField(nameField);
      fileIndex.addField(nameIndexField);
      indexConfs.put(fileIndex.getName(), fileIndex);
      // check meta :
      checkMetamodel(fileIndex);

      // check that type in index :
      PcuType pcuType = getPcuType(fileType.getName());
      PcuIndex indexConf = getPcuIndex(fileType.getName());
      if (!indexConf.getTypes().contains(pcuType.getName())) {
         throw new RuntimeException("Can't find type " + pcuType.getName() + " in index " + indexConf.getName());
      }

      // check pcuDoc against type :
      for (Entry<String, Object> entry : pcuDoc.getProperties().entrySet()) {
         String name = entry.getKey();
         PcuField pcuField = pcuType.getFields().get(name);
         if (pcuField == null) {
            throw new RuntimeException("Can't find field " + name + " in type " + pcuType.getName());
         }
         Object value = entry.getValue();
         switch(pcuField.getType()) {
         case "int":
            if (!(value instanceof Integer)) {
               throw new RuntimeException("Value for field " + name + " in type " + pcuType.getName() + " should be of type " + pcuField.getType() + " but is " + value);
            }
            // TODO try parsing from string ?
         }
      }
      // TODO also check missing required fields
      
      }
   }


   /**
    * @obsolete
    * @param index
    * @return
    * @throws IOException
    */
   public FileCrawler buildFileCrawler(String index) throws IOException {
      FileCrawler fileCrawler = new FileCrawler();
      fileCrawler.setContentStore("fileCrawlerStore"); // TODO manage
      fileCrawler.setIndex(index);
      fileCrawler.setType("file");
      fileCrawler.init();
      
      return fileCrawler;
   }
   

   private HashMap<String,PcuIndex> indexConfs = new HashMap<String,PcuIndex>();
   private HashMap<String,PcuType> pcuTypes = new HashMap<String,PcuType>();
   public PcuType getPcuType(String type) {
      PcuType pcuType = pcuTypes.get(type);
      if (pcuType == null) {
         throw new RuntimeException("Can't find type " + type);
      }
      return pcuType;
   }
   public PcuIndex getPcuIndex(String name) {
      PcuIndex index = indexConfs.get(name);
      if (index == null) {
         throw new RuntimeException("Can't find index " + name);
      }
      return index;
   }
   public PcuIndex getPcuIndex(PcuIndexField indexField) {
      PcuIndex index = indexConfs.get(indexField.getIndexType());
      if (index == null) {
         throw new RuntimeException("Can't find index " + indexField.getIndexType() + " of index field " + indexField.getName());
      }
      return index;
   }
   public PcuField getPcuField(PcuIndexField indexField) {
      PcuIndex index = getPcuIndex(indexField);
      for (String type : index.getTypes()) {
         PcuType pcuType = getPcuType(type);
         PcuField pcuField = pcuType.getField(indexField.getName());
         if (pcuField != null) {
            return pcuField;
         }
      }
      return null;
   }
   public List<PcuField> getDuplicatePcuFields(PcuIndex index, PcuIndexField indexField) {
      List<PcuField> duplicatePcuFields = new ArrayList<PcuField>(3);
      for (String type : index.getTypes()) {
         PcuType pcuType = pcuTypes.get(type);
         if (pcuType == null) {
            throw new RuntimeException("Can't find type " + type + " of index " + index.getName());
         }
         PcuField pcuField = pcuType.getField(indexField.getName());
         if (pcuField != null) {
            duplicatePcuFields.add(pcuField);
         }
      }
      return duplicatePcuFields;
   }
   private void checkMetamodel(PcuIndex index) {
      for (PcuIndexField indexField : index.getFields().values()) {
         List<PcuField> duplicatePcuFields = getDuplicatePcuFields(index, indexField);
         if (duplicatePcuFields.isEmpty()) {
            throw new RuntimeException("Can't find PCU field for " + indexField.getName() + " of index " + index.getName()
               + " among PCU types " + index.getTypes());
         }
         if (duplicatePcuFields.size() > 1) {
            throw new RuntimeException("More than one PCU field for " + indexField.getName() + " of index " + index.getName()
               + " among PCU types " + index.getTypes() + " : exists in PCU types "
                  + duplicatePcuFields.stream().map(f -> f.getPcuType()).collect(Collectors.toList()));
         }
      }
   }
   
}
