package org.pcu.features.connector.crawler;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.pcu.features.configuration.api.PcuComponent;
import org.pcu.features.connector.PcuConnector;
import org.pcu.providers.file.api.PcuFileApi;
import org.pcu.providers.metadata.api.PcuMetadataApi;
import org.pcu.providers.search.api.PcuDocument;
import org.pcu.providers.search.api.PcuSearchApi;
import org.pcu.providers.search.api.PcuSearchEsClientApi;
import org.pcu.search.elasticsearch.api.BulkAction;
import org.pcu.search.elasticsearch.api.BulkMessage;
import org.pcu.search.elasticsearch.api.BulkResult;
import org.pcu.search.elasticsearch.api.ESApiException;
import org.pcu.search.elasticsearch.api.IndexAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

public class Crawler2 extends PcuComponent {

   protected final Logger log = LoggerFactory.getLogger(this.getClass().getName());
   
   /** main (or default) index */
   protected String index;
   /** main (or default) type (but is not enough for dynamic schema ex. JDBC) */
   protected String type;
   private String contentStore;
   /** 0 means no bulk, 2 allows to test bulk */
   private int bulkSize = 2; // TODO default 1000
   
   /** changes between crawl sessions / job tasks */
   private String crawlId = null;


   @JsonIgnore
   private PcuConnector connector;

   /** for (de)ser */
   public Crawler2() {
      
   }
   /** when outside deser, ex. in tests or hardcodedDefaultConf() */
   public Crawler2(PcuConnector connector) {
      this.connector = connector;
   }
   /** to access it from within crawlBatches, so they can access too its features */
   public PcuConnector getConnector() {
      return connector;
   }
   /** used after deser */
   public void setConnector(PcuConnector connector) {
      this.connector = connector;
   }
   
   // state :
   @JsonManagedReference
   protected LinkedBlockingQueue<CrawlBatch> toBeCrawledQeue = new LinkedBlockingQueue<CrawlBatch>(); // NB. no fixed capacity
   private ArrayList<PcuDocument> toBeIndexedPcuDocuments = new ArrayList<PcuDocument>(1000);
   /** for differential updates (and therefore continuous crawling) */
   private Object lastCompletedCrawlStartedDate = null;
   
   // stats :
   protected/*private*/ long crawled = 0;
   private long indexed = 0;


   public void initSuper() {
      // TODO if already running KO
      
      if (this.contentStore == null) {
         this.contentStore = this.index;
      }
      
      this.crawlId = this.getConnector().getUuidGenerator().generate().toString(); // unique, contains start time (timestamp)
      
      // TODO LATER load state from persisted
      this.lastCompletedCrawlStartedDate = null;
   }
   
   // TODO LATER Kafka topics to scale it up !(?)
   // TODO Q also links or specific ?
   /** ex. dir.listChildren() or db.nextPage() */
   ///protected abstract List<File> getNextToBeCrawled(T toBeCrawledOrLastCrawled);
   /** TODO LATER several contents per crawled item ? */
   ///protected abstract InputStream getContentInputStream(T toBeCrawled) throws Exception;
   /** Mapper & meta extractor : returns indexable mapped doc, and optional dependent docs ex. category... */
   // & if doc then extract
   ///protected abstract List<PcuDocument> buildPcuDocuments(T crawled, String uploadPath);
   
   public void crawlIteration() throws CrawlCompletedException, Exception {
      if (this.crawlId == null) {
         throw new Exception("Not inited");
      }
      
      CrawlBatch toBeCrawledCrawlBatch = toBeCrawledQeue.poll();
      if (toBeCrawledCrawlBatch == null) {
         throw new CrawlCompletedException();
      }
      
      ///List<CrawlBatch> nextToBeCrawledBatches = toBeCrawledCrawlBatch.doNextPageItemsAndReturnNextCrawlBatches();
      PcuDocumentsMapResult pcuDocsMapRes = toBeCrawledCrawlBatch.buildNextPagePcuDocumentsAndCrawlBatches();
      if (pcuDocsMapRes != null) {
         if (pcuDocsMapRes.getPcuDocuments() != null) { // or hasDoc ?
            this.registerForIndexing(pcuDocsMapRes.getPcuDocuments(), toBeCrawledCrawlBatch.getIndex());
         }
         List<CrawlBatch> nextToBeCrawledBatches = pcuDocsMapRes.getNextCrawlBatches();
         /*
         List<Object> nextPageItems = toBeCrawledCrawlBatch.nextPageItems();
         if (nextPageItems == null) {
            return; // this batch is done
         }
         for (Object toBeCrawled : nextPageItems) {
         
         // upload content (if any) :
         String uploadedContentPath = null;
         try (InputStream contentIn = toBeCrawledCrawlBatch.getContentInputStream(toBeCrawled)) {
            if (contentIn != null) {
               PcuFileResult fileRes = fileApi.storeContent(this.getContentStore(), contentIn);
               uploadedContentPath = fileRes.getPath();
            } // else ex. dir
         }
         
         // builds & extract :
         List<PcuDocument> pcuDocs = toBeCrawledCrawlBatch.buildPcuDocuments(toBeCrawled, uploadedContentPath);
         if (pcuDocs != null) { // or hasDoc ?
            // TODO get links and add to queue
            registerForIndexing(pcuDocs);
         } // else ex. folder
         
         ///CrawlBatch nextToBeCrawled = getNextToBeCrawled(toBeCrawled);
         CrawlBatch nextToBeCrawledBatch = new CrawlBatch(toBeCrawled);
         toBeCrawledQeue.add(nextToBeCrawledBatch); // TODO check that inserted at the tail
         
         }
         */
         
         if (nextToBeCrawledBatches != null) {
            toBeCrawledQeue.addAll(nextToBeCrawledBatches); // TODO check that inserted at the tail
         }
      }
   }
   
   /** client 
    * @param index */
   /*private*/public void registerForIndexing(List<PcuDocument> pcuDocs, String index) {
      if (index == null) {
         index = this.index; // default
      }
      
      this.crawled++;
      this.toBeIndexedPcuDocuments.addAll(pcuDocs);
      if (this.toBeIndexedPcuDocuments.size() > this.bulkSize) {
         BulkMessage bulkMessage = new BulkMessage();
         List<BulkAction> bulkActions = new ArrayList<BulkAction>(this.toBeIndexedPcuDocuments.size());
         
         // TODO rather use bulk API (which indexes each doc independently just the same)
         for (PcuDocument pcuDoc : this.toBeIndexedPcuDocuments) {
            /*try {
               PcuIndexResult indexRes = searchApi.index(index, pcuDoc);
               // TODO how to check if failure ?
               this.indexed++;
            } catch (Exception ex) {
               // ex. bad avro schema
               log.debug("error indexing doc " + pcuDoc.getId(), ex);
            }*/// TODO rm
            BulkAction indexBulkAction = new BulkAction();
            indexBulkAction.setDoc(pcuDoc.toEsDocument());
            IndexAction indexAction = new IndexAction();
            indexAction.set_index(index);
            indexAction.set_type(pcuDoc.getType());
            indexAction.set_id(pcuDoc.getId());
            if (pcuDoc.getVersion() != null) {
               indexAction.set_version(pcuDoc.getVersion());
            }
            indexBulkAction.getKindToAction().put("index", indexAction);
            bulkActions.add(indexBulkAction);
         }
         
         bulkMessage.setActions(bulkActions);
         try {
            BulkResult bulkRes = this.getConnector().getSearchEsApi().bulk(bulkMessage, null, null);
            // TODO filter bulkRes and try to reindex failed ones !? ex. bad avro schema
            this.indexed += bulkRes.getItems().size();
         } catch (ESApiException ex) {
            // TODO Q which kind of errors ??
            log.debug("error indexing docs " + bulkActions.stream()
               .map(a -> a.getKindToAction().values().iterator().next().get_id())
               .collect(Collectors.toList()), ex);
         }
         
         toBeIndexedPcuDocuments.clear(); // TODO Q do something with those in error ?
      }
      if (log.isDebugEnabled()) {
         log.debug("crawled and indexed " + pcuDocs.get(0).getProperties()); // TODO
      }
   }

   /**
    * Generates & sets id & other generic PCU props.
    * As id, uses global business "path" if any (if less than 2000 chars URL encoded since ES doesn't accept slashes, else md5 hash'd)
    * in order to get "free" identify resolution / deduplication,
    * else generates (Jackson) UUID using : IEEE 802 address of the machine, timestamp
    * (100-nanosecond units since midnight, October 15, 1582 UTC.), clockSequence int.
    * Could be overwritten by inheritors (ex. using (md5 hashed-) path), but if it does not contain this info anymore
    * inheritors should implement them on their own (timestamp => synced, clockSequence => localOrder)
    * @param pcuDoc
    */
   public void generateAndSetId(PcuDocument pcuDoc) {
      UUID uuidWithTimestampAndLocalOrder = this.getConnector().getUuidGenerator().generate();
      String businessPath = (String) pcuDoc.getProperties().get("path"); // path-like business id ex. globalFilePath for a file
      String id;
      if (businessPath != null) {
         if (businessPath.length() <= 2000) {
            try {
               id = URLEncoder.encode(businessPath, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
               // should not happen
               throw new RuntimeException("Error encoding businessPath", e);
            }
         } else {
            id = CrawlUtils.md5(businessPath);
         }
      } else {
         id = uuidWithTimestampAndLocalOrder.toString();
      }
      pcuDoc.setId(id);
      pcuDoc.setVersion(uuidWithTimestampAndLocalOrder.timestamp()); // implicit version, precision = 100 ns which is enough for crawling
      // TODO version_global
      // TODO host...
      pcuDoc.setProperty("crawl_id", crawlId); // myCrawlSessionOrJobTaskId
      pcuDoc.setProperty("synced", ZonedDateTime.now());
   }
   
   public String getIndex() {
      return index;
   }
   public void setIndex(String index) {
      this.index = index;
   }
   public String getType() {
      return type;
   }
   public void setType(String type) {
      this.type = type;
   }
   public String getContentStore() {
      return contentStore;
   }
   public void setContentStore(String contentStore) {
      this.contentStore = contentStore;
   }

   // for stats only :
   public LinkedBlockingQueue<CrawlBatch> getToBeCrawledQeue() {
      return toBeCrawledQeue;
   }
   public ArrayList<PcuDocument> getToBeIndexedPcuDocuments() {
      return toBeIndexedPcuDocuments;
   }
   public long getCrawled() {
      return crawled;
   }
   public long getIndexed() {
      return indexed;
   }
   
   
   

   private List<String> roots;
   /** TODO regex, dir (hidden...) ; used to ser & deser suffixFileFilter
    * (which is FOR now readonly & filled from classpath) */
   private List<String> suffixes = null;
   /** only cache */
   @JsonIgnore
   protected/*private*/ SuffixFileFilter suffixFileFilter = null;

   

   /** try to prevent looping links  */
   @JsonIgnore
   private BloomFilter<String> urlBloomFilter;

   /** if no suffixFileFilter yet, lazy inits it from suffixes */
   public SuffixFileFilter getSuffixFileFilter() {
      if (this.suffixFileFilter == null && this.suffixes != null) {
         this.suffixFileFilter = new SuffixFileFilter(suffixes);
      }
      return suffixFileFilter;
   }
   public BloomFilter<String> getUrlBloomFilter() {
      return urlBloomFilter;
   }
   
   ///@Override
   public void init() {
      this.initSuper();
      // TODO if already running KO

      if (roots != null) {
         this.toBeCrawledQeue.addAll(roots.stream().map(p -> new File(p)) // TODO TODO in connector
               .filter(f -> f.exists()).map(f -> new FileCrawlBatch(f, this)).collect(Collectors.toList())); // else erroneously non-existing files from conf
      }
      ///this.toBeCrawledQeue.add(new CrawlBatch(new File("/home/mardut/Documents"), this));///
      if (suffixes != null) {
         this.suffixFileFilter = new SuffixFileFilter(suffixes);
      }
      
      this.urlBloomFilter = BloomFilter.<String>create(Funnels.stringFunnel(Charset.forName("UTF-8")), 100000, 0.05);
   }
   
   // TODO pagination (boundary value) vs children vs links (depth)
   ///@Override
   /*
   public List<File> getNextToBeCrawled(File fileLastCrawled) {
      if (fileLastCrawled.isFile() || fileLastCrawled.isDirectory()) {
         if (fileLastCrawled.canRead()) {
            File[] children = fileLastCrawled.listFiles();
            if (children != null) {
               return Arrays.asList(children);
            }
         }
      }
      return Collections.emptyList();
   }
   */
   
   ///@Override
   /*
   public List<PcuDocument> buildPcuDocuments(File file, String uploadedContentPath) {
      if (file.isDirectory()) {
         return null; // (for now) don't index directories
      }
      
      if (suffixFileFilter != null && !suffixFileFilter.accept(file)) {
         return null;
      }
      
      // extract meta including fulltext content :
      // see refs Dublin Core http://www.dublincore.org/documents/usageguide/qualifiers/ and Qwazr https://www.qwazr.com/documentation/QWAZR/qwazr-extractor/
      // TODO also / or on server-side, using uploaded content & URL
      PcuMetadataResult metadataRes = null;
      String textContent = null;
      try {
         if (log.isDebugEnabled()) {
            log.debug("Extracting metadata from " + file);
         }
         // NB. Qwazr extractorService supports optimization in case of a file URL (doesn't download to temp file)
         metadataRes = metadataExtractorApi.extract(file.toURI().toASCIIString()); // http://cache.media.education.gouv.fr/file/ICT/44/7/H2020-ICT-2017-1_Liste_projets_retenus_802447.pdf
         @SuppressWarnings("unchecked")
         String textContentTmp = (String) metadataRes.getContent().stream()
               .map(pageContent -> pageContent.get("content")).filter(pcc -> pcc != null).flatMap(pcc -> ((List<String>) pcc).stream())
               .collect(Collectors.joining(" "));
         textContent = textContentTmp;
         
      } catch (Exception e) { // WebApplicationException, ex. :
         // Caused by: org.apache.poi.hssf.record.RecordInputStream$LeftoverDataException: Initialisation of record 0x1D(SelectionRecord) left 2 bytes remaining still to be read.
         
         // Caused by: org.apache.xmlbeans.XmlException: error: The document is not a theme@http://schemas.openxmlformats.org/drawingml/2006/main: document element namespace mismatch expected "http://schemas.openxmlformats.org/drawingml/2006/main" got "http://schemas.openxmlformats.org/drawingml/2006/3/main"
         
         // java.lang.IllegalArgumentException: The document is really a UNKNOWN file
         
         // java.lang.IllegalArgumentException: The document is really a OOXML file
         
         // java.lang.NullPointerException: null
         // at com.qwazr.extractor.ParserAbstract.extractField(ParserAbstract.java:95)
         // => OK patched in newer extractor
         
         // NoSuchMethodException: org.openxmlformats.schemas.wordprocessingml.x2006.main.impl.CTPictureBaseImpl.<init>(org.apache.xmlbeans.SchemaType, boolean)
         // at org.apache.xmlbeans.impl.schema.SchemaTypeImpl.getJavaImplConstructor2(SchemaTypeImpl.java:1817)
         // => because XML does not follow MS XML format http://www.datypic.com/sc/ooxml/t-w_CT_PictureBase.html
         // TODO Qwazr see how to get a better error
         
         if (log.isDebugEnabled()) {
            log.debug("Error extracting metadata from " + file, e);
         }
         // TODO remove log :
         //| main | 2017-12-01 19:53:15.736 | WARN  | PDSimpleFont:325 - No Unicode mapping for g367 (10) in font YNAHAD+Calibri
      }
      
      // build sample test pcuDoc including fulltext :
      PcuDocument pcuDoc = this.buildPcuDocument(file, textContent, uploadedContentPath);
      // and clean :
      pcuDoc.getProperties().remove("http");
      pcuDoc.getMapByPath("meta").clear();
      // TODO rights
      
      // TODO void sample prop values
      if (metadataRes != null) {
         
         // add read doc meta :
         Map<String, Object> docMetadata = metadataRes.getMetas();
         if (docMetadata != null) {
            pcuDoc.setByPath("http.mimetype", docMetadata.get("mime_type")); // TODO better
            pcuDoc.setByPath("meta.title", docMetadata.get("title"));
            pcuDoc.setByPath("meta.author", docMetadata.get("author"));
            //pcuDoc.setByPath("meta.subject", docMetadata.get("subject"));
            //pcuDoc.setByPath("meta.number_of_pages", docMetadata.get("producer")); // Adobe PDF Library 11.0
            //pcuDoc.setByPath("meta.number_of_pages", docMetadata.get("number_of_pages"));
            pcuDoc.setByPath("meta.language", docMetadata.get("language"));
            pcuDoc.setByPath("meta.date", docMetadata.get("creation_date")); // or only "created" ?
            pcuDoc.setByPath("meta.created", docMetadata.get("creation_date"));
            pcuDoc.setByPath("meta.modified", docMetadata.get("modification_date"));
         }
         
         // add extracted meta, only if not among read meta already :
         // BEWARE some extractors can't extract a meta even if it is among read meta (ex. pdf language)
         @SuppressWarnings("unchecked")
         LinkedHashMap<String, Object> pcuDocMeta = (LinkedHashMap<String, Object>) pcuDoc.getProperties().get("meta");
         Map<String, Object> firstPage = metadataRes.getContent().get(0);
         if (pcuDocMeta.get("language") == null) {
            @SuppressWarnings("unchecked")
            List<String> lang_detection = (List<String>) firstPage.get("lang_detection");
            if (lang_detection != null && !lang_detection.isEmpty()) {
               pcuDocMeta.put("language", lang_detection.get(0));
            }
         }
         // TODO charset_detection (character_count, rotation)
         
         // TODO extracted links : add meta and register to be crawled (or not)
         ///pcuDoc.setByPath("meta.links", docMetadata.get("links")); // or content.links ??
         ///this.toBeCrawledQeue.addAll((List<URL>) docMetadata.get("links")); // TODO depth
      }
      
      this.generateAndSetId(pcuDoc); // or in Crawler ?
      
      pcuDoc.getProperties().put("readable_host", this.getConnectorComputerHostName());
      
      return Arrays.asList(pcuDoc);
   }

   ///@Override
   protected InputStream getContentInputStream(File file) throws FileNotFoundException {
      if (file.isFile()) {
         return new FileInputStream(file);
      }
      return null;
   }
   
   ////////////////////////////////////
   // TODO refactor below

   public PcuDocument buildPcuDocument(File testFile, String testContent, String storedFileResPath) {
      Crawler2 fileCrawler = this;
      
      PcuDocument pcuDoc = new PcuDocument();
      pcuDoc.setType(type); // "file"
      
      // file :
      String testFileAbsoluteSlashedPath = testFile.getAbsolutePath();
      if (File.pathSeparatorChar == '\\') {
         testFileAbsoluteSlashedPath = testFileAbsoluteSlashedPath.replace('\\', '/');
      }
      String globalFilePath = "/" + fileCrawler.getConnectorComputerId() + / url != null ? /host/protocol : /file / testFileAbsoluteSlashedPath;
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
      content.put("hash", storedFileResPath); // why not : path is its hash ; hash, md5, digest (nuxeo), checksum (fscrawler) ? below content, file or store ?
      content.put("store_path", fileCrawler.getContentStore() + "/" + storedFileResPath); // or 2 props ? store_path, store_id, id_in_store ? below top, file or content ?
      // TODO Q also detected language ??
      
      // meta : (or tika, pdfbox... ?)
      // see refs Dublin Core http://www.dublincore.org/documents/usageguide/qualifiers/ and Qwazr https://www.qwazr.com/documentation/QWAZR/qwazr-extractor/
      LinkedHashMap<String, Object> meta = new LinkedHashMap<>();
      pcuDoc.getProperties().put("meta", meta); // TODO of type "meta"
      meta.put("author", "Jane Dee");
      meta.put("title", "CV of John Doe");
      ///meta.put("created", null); // ZonedDateTime.parse("2016-10-01T15:29:45.000+0000", pcuApiDateTimeFormatter)); // ES supports Z or ZZ (+0000 having millis) but not ZZZZ (GMT+02:00)
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
      rights.put("ar", new LinkedHashSet<>(Stream.concat(rights.get("r").stream(), new LinkedHashSet<String>(/ writers /).stream())
            .collect(Collectors.toSet()))); // done auto in indexing pipeline, allows to inject in query a criteria that restricts to allowed readers
      
      // crawl : (or crawl.id, crawl.synced ?)
      pcuDoc.getProperties().put("crawl_id", "myCrawlSessionOrJobTaskId"); // allows to get more info about crawl session or wrapping crawl job task conf
      pcuDoc.getProperties().put("synced", ZonedDateTime.now()); // fscrawler : file.indexing_date ; or LocalDateTime.now() ?
      ///pcuDoc.getProperties().put("host_id", fileCrawler.getConnectorComputerId()); // ?? (binary) ; NOO already in (global) path
      pcuDoc.getProperties().put("readable_host", / rootUrl != null ? rootUrl.getHost() :  /fileCrawler.getConnectorComputerHostName()); // (or readable_path ?)
      // TODO Q scan host, root path, start/end date, crawl job id, crawl conf... ? => only crawl session id, and all infos in the crawl job manager
      // or as a mixin only on root dir ?
      
      // id :
      ////pcuDoc.setId(new UUID().toString()); // id - best lucene id http://blog.mikemccandless.com/2014/05/choosing-fast-unique-identifier-uuid.html => NOT unique on 2 machines, rather https://github.com/cowtowncoder/java-uuid-generator TimeBasedGenerator gen = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
      // or use it to dedup identity resolution ? then with configurable policy : using or not connectorComputerId, url protocol / host / port, even scan root dir
      //String id = "file://server1/a/b/file.doc"; // NOO ES doesn't support slash, and not a good Lucene id anyway
      pcuDoc.setId(globalFilePath); // id - (connector) app's - (url or) server + path (fscrawler : local path only), allows dedup / identity resolution NOO no slash in ES id => 2 logical URLs file://host/path with host being MAC address or hostname
      pcuDoc.setId(CrawlUtils.md5(globalFilePath)); // id - (connector) app's - (url or) server + path MD5 (fscrawler : local path only), allows dedup / identity resolution => NO MD5 2000 chars length id is acceptable, small collision risk ; qwazr link will have local db of (crawled docs) stable ids ; have both technical (uuid smaller better for processing) & business id (MAC+path)
      //pcuDoc.setId(ecmDoc.getId()); // id - (ECM) app's, allows dedup / identity resolution
      //fileCrawler.generateAndSetId(pcuDoc);
      
      // version & ordering :
      //pcuDoc.getProperties().put("version", esApi.getDocument("file", pcuDoc.getId())).getVersion(); // version if optimistic locking (not if pipeline)
      pcuDoc.getProperties().put("version"/ _local /, nextLocalOrder()); // local ordering as version (else local_version) => JUG TimeBasedGenerator by cowtowncoder ensures this and is ordered so is enough !
      //pcuDoc.getProperties().put("version", uuidGen.fromString(pcuDoc.getId()).clockSequence());
      pcuDoc.getProperties().put("version_global", nextLamport(/ impactingExternalProcessLamports /)); // global ordering (lamport timestamp) as version (else global_version)

      // TODO Q date as long timestamp (pcu) or formatted (fscrawler) ?
      
      return pcuDoc;
   }
*/

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

   public List<String> getRoots() {
      return roots;
   }
   public void setRoots(List<String> roots) {
      this.roots = roots;
   }
   public List<String> getSuffixes() {
      return suffixes;
   }
   public void setSuffixes(List<String> suffixes) {
      this.suffixes = suffixes;
   }
   
   
   
}