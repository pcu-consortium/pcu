package org.pcu.features.connector;

import static org.junit.Assert.assertEquals;
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
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.avro.AvroTypeException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pcu.features.search.server.meta.PcuField;
import org.pcu.features.search.server.meta.PcuIndex;
import org.pcu.features.search.server.meta.PcuIndexField;
import org.pcu.features.search.server.meta.PcuType;
import org.pcu.platform.model.ModelServiceImpl;
import org.pcu.platform.model.PcuModelConfiguration;
import org.pcu.providers.file.api.PcuFileApi;
import org.pcu.providers.file.api.PcuFileResult;
import org.pcu.providers.file.local.spi.LocalFileProviderConfiguration;
import org.pcu.providers.search.api.PcuDocument;
import org.pcu.providers.search.api.PcuIndexResult;
import org.pcu.providers.search.api.PcuSearchApi;
import org.pcu.providers.search.api.PcuSearchEsClientApi;
import org.pcu.providers.search.elasticsearch.spi.ESSearchProviderConfiguration;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.eaio.uuid.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;


/**
 * Test of PCU search API and features
 * WARNING requires ElasticSearch 5.5 to have been started independently.
 * 
 * TODO do this test with AND without the proxy client 
 * @author mardut
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={PcuConnectorConfiguration.class, // client
      ESSearchProviderConfiguration.class, LocalFileProviderConfiguration.class, PcuModelConfiguration.class}, // server
      initializers = ConfigFileApplicationContextInitializer.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.DEFINED_PORT, properties="server.port=45665")
//rather than @SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT, properties="server_port=45665")
//which would require listening to an ApplicationEvent and therefore using a Provider pattern
//see https://docs.spring.io/spring-boot/docs/current/reference/html/howto-embedded-servlet-containers.html https://stackoverflow.com/questions/30312058/spring-boot-how-to-get-the-running-port
//or with autoconf redefine cxf.jaxrs.client.address
@ActiveProfiles("test")
public class PcuConnectorImplTest /*extends PcuSearchApiClientTest */{
   @LocalServerPort
   protected int serverPort;

   @Autowired @Qualifier("pcuSearchEsApiRestClient") //@Qualifier("pcuSearchEsApiImpl") //pcuSearchEsApiRestClient //defaultSearchProviderEsApi
   private PcuSearchEsClientApi searchEsApi;
   @Autowired @Qualifier("pcuSearchApiRestClient") //@Qualifier("pcuSearchApiImpl") //pcuSearchApiRestClient
   private PcuSearchApi searchApi;
   @Autowired @Qualifier("defaultFileProviderApi") //defaultFileProviderApi LocalFileProviderApiImpl pcuFileApiRestClient
   private PcuFileApi fileApi;

   @Autowired @Qualifier("pcuApiDateTimeFormatter")
   private DateTimeFormatter pcuApiDateTimeFormatter;
   @Autowired @Qualifier("pcuApiMapper")
   private ObjectMapper pcuApiMapper;
   @Autowired @Qualifier("pcuApiPrettyMapper")
   private ObjectMapper pcuApiPrettyMapper;

   @Autowired
   private ModelServiceImpl modelService;

   /** for tests */
   @Autowired @Qualifier("elasticSearchRestClient")
   private ElasticSearchClientApi elasticSearchRestClient;
   @Autowired @Qualifier("elasticSearchMapper")
   private ObjectMapper elasticSearchMapper;
   
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
   public void testCrawler() {
      pcuConnector.defaultCrawl();
   }

   @Test
   public void testSimulateCrawlHome() throws UnknownHostException, SocketException {
      // init crawler :
      String index = "files";
      FileCrawler fileCrawler = buildFileCrawler(index);
      ///fileCrawler.seFilter();
      FileFilter txtFileFilter = new SuffixFileFilter(".txt"); // ONLY for .txt else too slow & big for demo
      
      // dataset :
      recursiveCrawlFolder(fileCrawler, new File("/home/mardut/Documents/projets/pcu/WP7 search/dataset"), txtFileFilter);
      // pcu
      //simulateCrawlFolder(fileCrawler, new File("/home/mardut/Documents/projets/pcu"));
      //simulateCrawlFolder(fileCrawler, new File("/home/mardut/dev/pcu"));
      // home :
      recursiveCrawlFolder(fileCrawler, new File(System.getProperty("user.home")), txtFileFilter); // + File.separator + "Documents" // "/home/mardut/dev/pcu/workspace"
   }
   public void simulateCrawlFolder(FileCrawler fileCrawler, File folder) {
      this.recursiveCrawlFolder(fileCrawler, folder, null); // , PcuConnectorImplTest::simulateCrawlFile
   }
   protected static final Logger log = LoggerFactory.getLogger(PcuConnectorImplTest.class);
   public void recursiveCrawlFolder(FileCrawler fileCrawler, File folder, FileFilter fileFilter) {
      for (File file : folder.listFiles()) {
         if (file.isFile()) {
            if (file.canRead()) {
               if (fileFilter.accept(file)) {
                  try {
                     log.debug("crawling " + file.getAbsolutePath());
                     simulateCrawlFile(fileCrawler, file);
                  } catch (Exception e) {
                     // test fails but it still works
                  }
               }
            }
         } else if (file.isDirectory()) {
            if (file.canRead()) {
               simulateCrawlFolder(fileCrawler, file);
            }
         } // else non-regular files : symlinks (TODO resolve), devices, pipes, sockets https://stackoverflow.com/a/21224032
      }
   }
   public void simulateCrawlFile(FileCrawler fileCrawler, File file) throws Exception {
      try {
         System.err.println("crawling " + file.getAbsolutePath());
         ///String content = null; // disables fulltext & checks
         String content = IOUtils.toString(new FileInputStream(file), (Charset) null);
         simulateCrawl(fileCrawler, file, content); // null
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
      String index = "files";
      FileCrawler fileCrawler = buildFileCrawler(index);
      
      simulateCrawl(fileCrawler, testFile, testContent);
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
   public void simulateCrawl(FileCrawler fileCrawler, File testFile, String testContent) throws Exception {
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

      PcuDocument pcuDoc = fileCrawler.buildPcuDocument(testFile, testContent, fileRes.getPath());
      
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
      rangeParams.setGt(null);
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
      assertEquals(1, modelService.getTypeSchemaMap().size());
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

   
   public FileCrawler buildFileCrawler(String index) throws UnknownHostException, SocketException {
      FileCrawler fileCrawler = new FileCrawler();
      fileCrawler.setContentStore("fileCrawlerStore"); // TODO manage
      fileCrawler.setIndex(index);
      fileCrawler.setType("file");
      // server id : MAC address (else system info using OS-specific commands like dmesg or /proc & /sys) ; TODO Q or readable host ??
      fileCrawler.setConnectorComputerHostName(InetAddress.getLocalHost().getHostName()); // or IP by getCanonicalHostName(), or both ?
      fileCrawler.setConnectorComputerId(Base64.getEncoder().encodeToString(NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress()));
      Collections.list(NetworkInterface.getNetworkInterfaces()).stream().forEach(itf -> System.out.println(itf.toString()));
      for (NetworkInterface itf : Collections.list(NetworkInterface.getNetworkInterfaces())) {
         try {
            System.out.println(itf + " " + itf.isVirtual() + " " + itf.isLoopback() + " " + itf.getHardwareAddress() + " " + itf.getMTU());
         } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      };
      
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
