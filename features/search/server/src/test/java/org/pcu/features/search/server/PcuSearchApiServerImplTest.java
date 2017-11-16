package org.pcu.features.search.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
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

import javax.annotation.PostConstruct;

import org.apache.avro.AvroTypeException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pcu.features.search.server.meta.PcuField;
import org.pcu.features.search.server.meta.PcuIndex;
import org.pcu.features.search.server.meta.PcuIndexField;
import org.pcu.features.search.server.meta.PcuType;
import org.pcu.providers.file.api.PcuFileApi;
import org.pcu.providers.file.api.PcuFileResult;
import org.pcu.providers.search.api.PcuDocument;
import org.pcu.providers.search.api.PcuIndexResult;
import org.pcu.providers.search.api.PcuSearchApi;
import org.pcu.providers.search.api.PcuSearchEsClientApi;
import org.pcu.providers.search.elasticsearch.spi.ESSearchProviderConfiguration;
import org.pcu.search.elasticsearch.api.ESApiException;
import org.pcu.search.elasticsearch.api.ElasticSearchClientApi;
import org.pcu.search.elasticsearch.api.mapping.IndexMapping;
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
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.eaio.uuid.UUID;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


/**
 * Test of PCU search API and features
 * WARNING requires ElasticSearch 5.5 to have been started independently.
 * 
 * TODO do this test with AND without the proxy client 
 * @author mardut
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={PcuSearchServerConfiguration.class,
      ESSearchProviderConfiguration.class, PcuSearchApiServerImplTest.Conf.class},
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

   @Autowired @Qualifier("pcuSearchEsApiRestClient") //@Qualifier("pcuSearchEsApiImpl") //pcuSearchEsApiRestClient
   private PcuSearchEsClientApi searchEsApi;
   @Autowired @Qualifier("pcuSearchApiRestClient") //@Qualifier("pcuSearchApiImpl") //pcuSearchApiRestClient
   private PcuSearchApi searchApi;
   @Autowired @Qualifier("defaultFileProviderApiImpl") //defaultFileProviderApiImpl LocalFileProviderApiImpl pcuFileApiRestClient
   private PcuFileApi fileApi;

   @Autowired @Qualifier("pcuApiDateTimeFormatter")
   private DateTimeFormatter pcuApiDateTimeFormatter;
   @Autowired @Qualifier("pcuApiMapper")
   private ObjectMapper pcuApiMapper;
   @Autowired @Qualifier("pcuApiPrettyMapper")
   private ObjectMapper pcuApiPrettyMapper;

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
      pcuDoc.setId(URLEncoder.encode("mdutoo-laptop/home/mardut/dev/pcu/workspace/pcu/features/search/server/src/main/java/org/pcu/features/search/server/deeper/pcu/features/search/server/src/main/java/org/pcu/features/search/server/PcuSearchApiServerImpl.java", "UTF-8"));
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
   public void testSimulateCrawlHome() {
      // dataset :
      simulateCrawlFolder(new File("/home/mardut/Documents/projets/pcu/WP7 search/dataset"));
      // home :
      simulateCrawlFolder(new File(System.getProperty("user.home"))); // + File.separator + "Documents" // "/home/mardut/dev/pcu/workspace"
   }
   public void simulateCrawlFolder(File folder) {
      for (File file : folder.listFiles()) {
         if (file.isFile()) {
            if (file.canRead()) {
               try {
                  System.err.println("crawling " + file.getAbsolutePath());
                  String content = null; // disables fulltext & checks
                  if (file.getName().endsWith(".txt")) {
                     content = IOUtils.toString(new FileInputStream(file), (Charset) null);
                  }
                  simulateCrawl(file, content); // null
               } catch (AssertionError e) {
                  // test fails but it still works
               } catch (Exception e) {
                  // test fails but it still works
               }
            }
         } else if (file.isDirectory()) {
            if (file.canRead()) {
               simulateCrawlFolder(file);
            }
         } // else non-regular files : symlinks (TODO resolve), devices, pipes, sockets https://stackoverflow.com/a/21224032
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
      File testFile = File.createTempFile("pcu_test_", ".doc");
      testFile.deleteOnExit();
      try (FileOutputStream testFileOut = new FileOutputStream(testFile)) {
         IOUtils.write(testContent, testFileOut, (Charset) null);
      }
      simulateCrawl(testFile, testContent);
   }
   /**
    * 
    * @param testFile
    * @param testContent null disables fulltext & checks
    * @throws Exception
    */
   public void simulateCrawl(File testFile, String testContent) throws Exception {
      // init crawler :
      String store = "fileCrawlerStore"; // TODO manage
      String index = "files";
      //String fileType = "file";
      // server id : MAC address (else system info using OS-specific commands like dmesg or /proc & /sys) ; TODO Q or readable host ??
      String connectorComputerName = InetAddress.getLocalHost().getHostName(); // or IP by getCanonicalHostName(), or both ?
      String connectorComputerId = Base64.getEncoder().encodeToString(NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress());
      Collections.list(NetworkInterface.getNetworkInterfaces()).stream().forEach(itf -> System.out.println(itf.toString()));
      for (NetworkInterface itf : Collections.list(NetworkInterface.getNetworkInterfaces())) {
         try {
            System.out.println(itf + " " + itf.isVirtual() + " " + itf.isLoopback() + " " + itf.getHardwareAddress() + " " + itf.getMTU());
         } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      };
      
      // init crawl :
      String host = /*rootUrl != null ? rootUrl.getHost() : */connectorComputerName;
      
      // 1. upload crawled content :
      PcuFileResult fileRes;
      try (FileInputStream testFileIn = new FileInputStream(testFile)) {
         fileRes = fileApi.storeContent(store,testFileIn);
      }
      // and check :
      InputStream testFileInRes = fileApi.getContent(store, fileRes.getPath());
      if (testContent != null)
      assertEquals(testContent, IOUtils.toString(testFileInRes, (Charset) null));
      
      // 2. index crawled metadata :
      
      PcuDocument pcuDoc = new PcuDocument();
      pcuDoc.setType("file");
      
      // TODO Q date as long timestamp (pcu) or formatted (fscrawler) ?
      
      // id :
      pcuDoc.setId(new UUID().toString()); // id - best lucene id http://blog.mikemccandless.com/2014/05/choosing-fast-unique-identifier-uuid.html => NOT unique on 2 machines, rather https://github.com/cowtowncoder/java-uuid-generator TimeBasedGenerator gen = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
      // or use it to dedup identity resolution ? then with configurable policy : using or not connectorComputerId, url protocol / host / port, even scan root dir
      //String id = "file://server1/a/b/file.doc"; // NOO ES doesn't support slash, and not a good Lucene id anyway
      String testFileAbsoluteSlashedPath = testFile.getAbsolutePath();
      if (File.pathSeparatorChar == '\\') {
         testFileAbsoluteSlashedPath = testFileAbsoluteSlashedPath.replace('\\', '/');
      }
      String globalFilePath = "/" + connectorComputerId + /*url != null ? /host/protocol : /file*/ testFileAbsoluteSlashedPath;
      pcuDoc.setId(globalFilePath); // id - (connector) app's - (url or) server + path (fscrawler : local path only), allows dedup / identity resolution NOO no slash in ES id => 2 logical URLs file://host/path with host being MAC address or hostname
      pcuDoc.setId(md5(globalFilePath)); // id - (connector) app's - (url or) server + path MD5 (fscrawler : local path only), allows dedup / identity resolution => NO MD5 2000 chars length id is acceptable, small collision risk ; qwazr link will have local db of (crawled docs) stable ids ; have both technical (uuid smaller better for processing) & business id (MAC+path)
      //pcuDoc.setId(ecmDoc.getId()); // id - (ECM) app's, allows dedup / identity resolution
      
      // version & ordering :
      //pcuDoc.getProperties().put("version", esApi.getDocument("file", pcuDoc.getId())).getVersion(); // version if optimistic locking (not if pipeline)
      pcuDoc.getProperties().put("version"/*_local*/, nextLocalOrder()); // local ordering as version (else local_version) => JUG TimeBasedGenerator by cowtowncoder ensures this and is ordered so is enough !
      pcuDoc.getProperties().put("version_global", nextLamport(/*impactingExternalProcessLamports*/)); // global ordering (lamport timestamp) as version (else global_version)
      
      // crawl : (or crawl.id, crawl.synced ?)
      pcuDoc.getProperties().put("crawl_id", "myCrawlSessionOrJobTaskId"); // allows to get more info about crawl session or wrapping crawl job task conf
      pcuDoc.getProperties().put("synced", ZonedDateTime.now()); // fscrawler : file.indexing_date ; or LocalDateTime.now() ?
      pcuDoc.getProperties().put("host_id", connectorComputerId); // ?? (binary)
      pcuDoc.getProperties().put("readable_host", host);
      // TODO Q scan host, root path, start/end date, crawl job id, crawl conf... ? => only crawl session id, and all infos in the crawl job manager
      // or as a mixin only on root dir ?
      
      // file :
      LinkedHashMap<String, Object> file = new LinkedHashMap<>();
      pcuDoc.getProperties().put("file", file); // TODO of type "file"
      file.put("last_modified", ZonedDateTime.ofInstant(Instant.ofEpochMilli(testFile.lastModified()), ZoneId.systemDefault())); // TODO Q or globally ?! or annotated as global prop @modified ? or LocalDateTime ?
      file.put("name", testFile.getName());
      file.put("path", testFileAbsoluteSlashedPath); // TODO analyze as a tree structure ?
      // TODO group, owner (fscrawler : in attributes/)
      // TODO Q also file url, "virtual" path, extension, indexed_chars ? content_type (or in content) ??
      
      // http : (gotten from HTTP request if any)
      LinkedHashMap<String, Object> http = new LinkedHashMap<>();
      pcuDoc.getProperties().put("http", http); // TODO of type "http"
      http.put("url", "http://myserver/myfile.doc"); // if any ; else file:/// ??
      http.put("mimetype", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"); // fscrawler : rather in file ?!?
      // TODO Q also other headers : size, expire... ??
      
      // content :
      LinkedHashMap<String, Object> content = new LinkedHashMap<>();
      pcuDoc.getProperties().put("content", content); // TODO of type "content"
      content.put("length", testFile.length()); // fscrawler : filesize ; below file or content ?
      content.put("hash", fileRes.getPath()); // why not ; hash, md5, digest (nuxeo), checksum (fscrawler) ? below content, file or store ?
      content.put("store_path", store + "/" + fileRes.getPath()); // or 2 props ? store_path, store_id, id_in_store ? below top, file or content ?
      // TODO Q also detected language ??
      
      // meta : (or tika, pdfbox... ?)
      LinkedHashMap<String, Object> meta = new LinkedHashMap<>();
      pcuDoc.getProperties().put("meta", meta); // TODO of type "meta"
      meta.put("author", "Jane Dee");
      meta.put("title", "CV of John Doe");
      meta.put("date", ZonedDateTime.parse("2016-10-01T15:29:45.000+0000", pcuApiDateTimeFormatter)); // ES supports Z or ZZ (+0000 having millis) but not ZZZZ (GMT+02:00)
      meta.put("keywords", Arrays.asList(new Object[] { "cv" }));
      meta.put("language", "en"); // TODO Q meta or detected ??
      // TODO format, identifier, contributor, coverage, modifier, creator_tool, publisher, relation, rights, source, type,
      // description, created, print_date, metadata_date, latitude, longitude, altitude, rating, comments ?!
      // TODO dynamic field mappings for ex. "xmpDM:audioCompressor" : "MP3" => or could be enabled by Link scripts called by a top script or yaml gotten from server : zookeeper (tree nodes & listen, but small), ES, git (but no ACL) ?! for now mere content tree API

      if (testContent != null)
      pcuDoc.getProperties().put("fulltext", testContent); // parsed client-side by tika in connector crawler ; below top, meta, content ?? => could be too big ex. dictionary, which lucene can handle but probably not ES
      // OPT properties : alternatively parsed JSON (& XML) as nested complex objects ? => OR fulltext for each page in order to known which one, or for each successive language, or one fulltext_fr/en field by language, in addition to mere ascii fulltext
      
      // file treeS in ES :
      pcuDoc.getProperties().put("path", globalFilePath); // most exact tree
      // NB. is used to browse files. It's root / first path element is the connector computer / host's id, which can be displayed in a prettier way using "readadble_host".

      // rights : (TODO optional, guest/admin)
      LinkedHashMap<String, LinkedHashSet<String>> rights = new LinkedHashMap<>();
      pcuDoc.getProperties().put("rights", rights);
      rights.put("r", new LinkedHashSet<>(Arrays.asList(new String[] { "myproject_group" })));
      rights.put("w", new LinkedHashSet<String>()); // allows to modify entity (not in entreprise search, but in ecommerce would be nice to have)
      rights.put("o", new LinkedHashSet<String>());
      rights.put("ar", new LinkedHashSet<>(Stream.concat(rights.get("r").stream(), new LinkedHashSet<String>(/*writers*/).stream())
            .collect(Collectors.toSet()))); // done auto in indexing pipeline, allows to inject in query a criteria that restricts to allowed readers
      
      
      PcuIndexResult indexRes = searchApi.index(index, pcuDoc);
      
      // find : (TODO by path...)
      PcuDocument res = searchApi.get(index, pcuDoc.getId());
      assertEquals(pcuDoc.getId(), res.getId());
      //assertEquals(pcuDoc.getVersion(), res.getVersion());
      // get content :
      String[] storePath = ((String) pcuDoc.getByPath("content.store_path")).split("/", 2);
      InputStream foundContent = fileApi.getContent(storePath[0], storePath[1]);
      if (testContent != null)
      assertEquals(testContent, IOUtils.toString(foundContent, (Charset) null));
      
      // TODO test versionS & optimistic locking
      
      // query - on date range :
      ESQueryMessage dateRangeMsg = new ESQueryMessage();
      range dateRange = new range();
      dateRangeMsg.setQuery(dateRange);
      RangeFieldParameters rangeParams = new RangeFieldParameters();
      dateRange.setRangeParameters("file.last_modified", rangeParams);
      rangeParams.setGt(ZonedDateTime.parse("2017-01-01T00:00:01.000+0000", pcuApiDateTimeFormatter));
      rangeParams.setLte(ZonedDateTime.ofInstant(Instant.ofEpochMilli(testFile.lastModified()), ZoneId.systemDefault()));
      List<Hit> hits = searchEsApi.search(dateRangeMsg, null, null).getHits().getHits();
      assertTrue(!hits.isEmpty());
      assertTrue(pcuDoc.getId().equals(hits.get(0).get_id()));
      rangeParams.setLt(ZonedDateTime.parse("2016-01-01T00:00:01.000+0000", pcuApiDateTimeFormatter));
      rangeParams.setGt(null);
      hits = searchEsApi.search(dateRangeMsg, null, null).getHits().getHits();
      assertTrue(hits.isEmpty());

      // query - on mimetype :
      ESQueryMessage mimetypeMsg = new ESQueryMessage();
      multi_match mimetypeMultiMatch = new multi_match();
      mimetypeMultiMatch.setFields(Arrays.asList(new String[] {"http.mimetype"}));
      mimetypeMultiMatch.setQuery((String) http.get("mimetype"));
      mimetypeMsg.setQuery(mimetypeMultiMatch);
      hits = searchEsApi.search(mimetypeMsg , null, null).getHits().getHits();
      assertTrue(!hits.isEmpty());
      assertTrue(pcuDoc.getId().equals(hits.get(0).get_id()));

      // query - on path (prefix) :
      ESQueryMessage pathMsg = new ESQueryMessage();
      prefix pathPrefix = new prefix();
      PrefixFieldParameters prefixParameters = new PrefixFieldParameters();
      String testFileParentPath = testFileAbsoluteSlashedPath.substring(0, testFileAbsoluteSlashedPath.lastIndexOf('/')); 
      prefixParameters.setValue(testFileParentPath);
      pathPrefix.setPrefixParameters("file.path", prefixParameters );
      pathMsg.setQuery(pathPrefix);
      hits = searchEsApi.search(pathMsg , null, null).getHits().getHits();
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
      hits = searchEsApi.search(pathMsg , null, null).getHits().getHits();
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
      hits = searchEsApi.search(rightsFilteredMsg , null, null).getHits().getHits();
      assertTrue(hits.isEmpty());
      
      // query without being admin, having right :
      userAuthorities.add("myproject_group");
      hits = searchEsApi.search(rightsFilteredMsg , null, null).getHits().getHits();
      assertTrue(!hits.isEmpty());
      assertTrue(pcuDoc.getId().equals(hits.get(0).get_id()));
      
      // X. simulate tailing a file :
      String testAppendContent = "\nand another test content";
      // create = first append (no random access)
      try (FileInputStream testFileIn = new FileInputStream(testFile)) {
         fileRes = fileApi.appendContent(store, globalFilePath, null, testFileIn);
      }
      if (testContent != null)
      assertEquals(testContent, IOUtils.toString(fileApi.getContent(store, fileRes.getPath()), (Charset) null));
      // and index meta :
      content.put("store_path", fileRes.getPath());
      indexRes = searchApi.index(index, pcuDoc);
      // then, actual append (using random access) :
      fileRes = fileApi.appendContent(store, fileRes.getPath(), testFile.length(), new ByteArrayInputStream(testAppendContent.getBytes()));
      if (testContent != null)
      assertEquals(testContent + testAppendContent, IOUtils.toString(fileApi.getContent(store, fileRes.getPath()), (Charset) null));
      // NB. don't index metas anymore, or ONLY WHAT DOESN'T CHANGE, besides that at most length or modified MIGHT be updated using (even batch ?) event system
      
      // Y. simulate indexing an app's business entities :
      // TODO
   /*}

   @Test
   public void testSimulateDocumentMetamodelCheck() throws Exception {*/
      
      // check indexed data :
      assertEquals(1, typeSchemaMap.size());
      Schema schema = typeSchemaMap.get(pcuDoc.getType());
      assertNotNull(schema);

      System.out.println("validating\n" + pcuApiPrettyMapper.writeValueAsString(pcuDoc));
      
      GenericRecord pcuDocRec = validatePcuEntityAgainstAvroSchema(pcuDoc);
      assertEquals(pcuDocRec.get("name"), pcuDoc.getProperties().get("name"));
      
      // check missing field :
      Object oldValue = pcuDoc.getProperties().remove("path");
      try {
         validatePcuEntityAgainstAvroSchema(pcuDoc);
         fail("removing a field should be incompatible with schema");
      } catch (AvroTypeException atex) {
         pcuDoc.getProperties().put("path", oldValue);
      }
      
      // check retrocompatibillity :
      // add a field :
      pcuDoc.getProperties().put("newfield", "value");
      try {
         validatePcuEntityAgainstAvroSchema(pcuDoc);
      } catch (AvroTypeException atex) {
         fail("should be able to read new value using old schema");
      }
      // change a field :
      oldValue = pcuDoc.setByPath("file.name", 123);
      try {
         validatePcuEntityAgainstAvroSchema(pcuDoc);
         fail("removing a field should be incompatible with schema");
      } catch (AvroTypeException atex) {
         pcuDoc.setByPath("file.name", oldValue);
      }
      
      // TODO check avro oldSchema.compat(newSchema) : add, remove, change
      ///typeSchemaMap.get("file").setFields(fields);
      
      /////////////////////////////////////
      
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
      PcuType type = getPcuType(fileType.getName());
      PcuIndex indexConf = getPcuIndex(fileType.getName());
      if (!indexConf.getTypes().contains(type.getName())) {
         throw new RuntimeException("Can't find type " + type.getName() + " in index " + indexConf.getName());
      }

      // check pcuDoc against type :
      for (Entry<String, Object> entry : pcuDoc.getProperties().entrySet()) {
         String name = entry.getKey();
         PcuField pcuField = type.getFields().get(name);
         if (pcuField == null) {
            throw new RuntimeException("Can't find field " + name + " in type " + type.getName());
         }
         Object value = entry.getValue();
         switch(pcuField.getType()) {
         case "int":
            if (!(value instanceof Integer)) {
               throw new RuntimeException("Value for field " + name + " in type " + type.getName() + " should be of type " + pcuField.getType() + " but is " + value);
            }
            // TODO try parsing from string ?
         }
      }
      // TODO also check missing required fields
   }
   
   protected static final Logger log = LoggerFactory.getLogger(PcuSearchApiServerImplTest.class);
   private HashMap<String,Schema> fileSchemaMap = new HashMap<String,Schema>();
   private HashMap<String,Schema> typeSchemaMap = new HashMap<String,Schema>();
   @PostConstruct
   public void initMeta() throws JsonParseException, JsonMappingException, IOException {
      // init ES :
      for (Resource esIndexMappingResource : resourceLoader.getResources("classpath*:bootstrap/es/index/mapping/*.json")) {
         IndexMapping indexMapping = elasticSearchMapper.readValue(esIndexMappingResource.getInputStream(), IndexMapping.class);
         String index = esIndexMappingResource.getFilename();
         index = index.substring(0, index.lastIndexOf('.'));

         try {
            LinkedHashMap<String, IndexMapping> existing = searchEsApi.getMapping(index);
            // BEWARE ES can't update mapping, only add new types or fields
            // TODO check if backward compatible, and :
            // - if it is (and only new types or fields), update, save if identical
            // - else in production mode fail
            searchEsApi.deleteMapping(index);
         } catch (ESApiException esex) {
            assertTrue(esex.getAsJson().contains("index_not_found_exception"));
         }
         try {
            searchEsApi.putMapping(index, indexMapping);
         } catch (ESApiException esex) {
            log.error("Failed to update conf of index " + index, esex);
         }
      }
      
      // init meta :
      // TODO check backward compatible, else replace or in production mode fail
      for (Resource avroSchemaResource : resourceLoader.getResources("classpath*:bootstrap/avro/*.avsc")) {
         try (InputStream avroSchemaResourceIs = avroSchemaResource.getInputStream()) {
            Schema schema = new Schema.Parser().parse(avroSchemaResourceIs);
            fileSchemaMap.put(schema.getFullName(), schema);
            for (Schema typeSchema : schema.getTypes()) {
               typeSchemaMap.put(typeSchema.getName(), typeSchema); // TODO fullName
            }
         } catch (IOException ioex) {
            log.error("Failed to load avro schema " + avroSchemaResource.getFilename(), ioex);
         }
      }
      
      // check metamodel vs ES conf consistency :
      // TODO
   }
   
   /**
    * For now writes pcuDoc to JSON and reads this as avro.
    * LATER don't write to JSON, using custom Jackson Deserializer ?
    * @param pcuDoc
    * @return
    * @throws AvroTypeException
    */
   public GenericRecord validatePcuEntityAgainstAvroSchema(PcuDocument pcuDoc) throws AvroTypeException {
      Schema schema = typeSchemaMap.get(pcuDoc.getType());
      if (schema == null) {
         throw new RuntimeException("Unknown schema type " + pcuDoc.getType());
      }
      try {
         // convert instance to JSON : (TODO skip this step...)
         String pcuDocumentAvroJson = pcuApiAvroMapper.writeValueAsString(pcuDoc); // Jackson should not fail since comes from REST API
         // validate instance :
         DatumReader<GenericRecord> genericDatumReader = new GenericDatumReader<GenericRecord>(schema); // TODO cache
         Decoder decoder = DecoderFactory.get().jsonDecoder(schema, new ByteArrayInputStream(pcuDocumentAvroJson.getBytes()));
         GenericRecord pcuDocRec = genericDatumReader.read(null, decoder); // AvroTypeException
         return pcuDocRec;
      } catch (IOException ioex) {
         // shouldn't happen since everything is written in memory
         throw new RuntimeException("IO error validating schema type " + schema.getType()
               + " of PCU document " + pcuDoc.getId(), ioex);
      }
   }

   @Autowired @Qualifier("pcuApiAvroMapper")
   private ObjectMapper pcuApiAvroMapper;
   @Configuration
   static class Conf {
      @Bean
      public ObjectMapper pcuApiAvroMapper(@Qualifier("pcuApiMapper") ObjectMapper pcuApiMapper) {
         // custom JSON conversion because of dates :
         return pcuApiMapper.copy().enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
         // and timestamp-millis avro annotation (optional) https://avro.apache.org/docs/1.8.0/spec.html#Timestamp+%28millisecond+precision%29
         // else AvroTypeException: Expected long. Got VALUE_STRING
      }
   }
   

   private String md5(String s) {
      try {
         return new BigInteger(1, MessageDigest.getInstance("MD5").digest(s.getBytes())).toString(16);
      } catch (NoSuchAlgorithmException nsaex) {
         throw new RuntimeException("Can't compute md5, error initing hash / digest", nsaex); // TODO
      }
   }

   //@Autowired
   //private ResourceLoader resourceLoader;
   private PathMatchingResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver(); // TODO @Bean
   

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

   private long currentLocalOrder = 0; // or Math.BigInteger ?
   private long currentTime = 0; // or Math.BigInteger ? TODO rather load from fs ??
   /** version / order based on crawl local time
    * TODO threaded (to avoid synchronized)
    * TODO or readable using date string ?? */
   private long nextLocalOrder() {
      long time = System.nanoTime();
      if (time == currentTime) {
         currentLocalOrder = currentLocalOrder + 1;
      } else {
         currentTime = time;
         currentLocalOrder = currentTime * 5000;
      }
      return currentLocalOrder;
   }
   private long currentLamport = 0; // or Math.BigInteger ? TODO rather load from fs ??
   /** version / order based on "modified" meta ???
    * TODO threaded (to avoid synchronized) */
   private Object nextLamport(long ... lamports) {
      // TODO ? if (lamports.length == 0) {
      for (long lamport : lamports) {
         if  (lamport > currentLamport) {
            currentLamport = lamport;
         }
      }
      currentLamport++;
      return currentLamport;
   }
   
}
