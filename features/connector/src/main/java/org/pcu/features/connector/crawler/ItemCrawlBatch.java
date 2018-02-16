package org.pcu.features.connector.crawler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.pcu.providers.file.api.PcuFileResult;
import org.pcu.providers.metadata.api.PcuMetadataResult;
import org.pcu.providers.search.api.PcuDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CrawlBatch where the type of what is crawlable is known.
 * @author mardut
 *
 * @param <T>
 */
public abstract class ItemCrawlBatch<T> extends CrawlBatch {

   private static final Logger log = LoggerFactory.getLogger(ItemCrawlBatch.class);
   
   /** TODO TODO REMOVE when Qwazr link extraction has been integrated */
   private Stack<List<String>> testLinksStack = new Stack<List<String>>() {{
      push(Arrays.asList(new String[] { "http://localhost:9200/file/_search", "http://localhost:9200", "file:///etc/hosts" }));
      push(Arrays.asList(new String[] { "file:./sample/20171206 POSS/PCU@POSS_20171206.pdf", "http://localhost:9200/file/_search" }));
   }};
   
   public ItemCrawlBatch(Crawler2 crawler) {
      super(crawler);
   }


   public abstract List<T> nextPageItems();
   
   @Override
   public PcuDocumentsMapResult buildNextPagePcuDocumentsAndCrawlBatches() {
      List<T> nextPageItems = this.nextPageItems();
      if (nextPageItems == null) {
         return null; // this batch is done
      }
      List<PcuDocument> pcuDocs = new ArrayList<PcuDocument>(nextPageItems.size());
      List<CrawlBatch> nextCrawlBatches = new ArrayList<CrawlBatch>();
      for (T toBeCrawled : nextPageItems) {
         try {
      
      // builds & extract :
      PcuDocumentsMapResult itemPcuDocsMapRes = this.buildPcuDocuments(toBeCrawled);
      if (itemPcuDocsMapRes != null) {
         if (itemPcuDocsMapRes.getPcuDocuments() != null) { // or hasDoc ?
            pcuDocs.addAll(itemPcuDocsMapRes.getPcuDocuments());
         }
         if (itemPcuDocsMapRes.getNextCrawlBatches() != null) {
            // get links and add to queue
            nextCrawlBatches.addAll(itemPcuDocsMapRes.getNextCrawlBatches());
         }
      } // else not to be indexed ex. folder
      
         } catch (Exception ex) {
            log.warn("Error crawling (file) " + toBeCrawled, ex);
            continue;
         }
      }
      return new PcuDocumentsMapResult(pcuDocs, nextCrawlBatches);
   }

   /** to be impl'd using buildPcuDocuments(file, url) below 
    * @throws IOException 
    * @throws FileNotFoundException */
   protected abstract PcuDocumentsMapResult buildPcuDocuments(T itemToBeCrawled) throws FileNotFoundException, IOException;
   

   ///@Override
   public PcuDocumentsMapResult buildPcuDocuments(File file, String url) throws FileNotFoundException, IOException {
      if (file != null && file.isDirectory()) {
         return new PcuDocumentsMapResult(null, // (for now) don't index directories
               Arrays.asList(new FileCrawlBatch(file, crawler))); // crawl files below
      }
      
      Map<String, List<String>> httpResponseHeaderFields = null;
      String netUrlMediaType = null; // without charset nor boundary (not accepted by qwazr) https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Type
      File tmpFile = null;
      if (file == null) {
         // TODO dontUploadIfNoExtension because then qwazr error WebApplicationException: No parser found, OR hack qwazr
         URL netUrl = new URL(url);
         if ("file".equals(netUrl.getProtocol())) {
            file = new File(netUrl.getPath()); // NOT new File(netUrl.toURI()) else URISyntaxException because of illegal characters ex. space !!
         } else {
            URLConnection urlConn = netUrl.openConnection();
            try (InputStream urlIn = urlConn.getInputStream()) { // TODO relative URLs file:./... (else file:/// FileNotFound, file:// UnknownHost)
               //tmpFile = new File(new File("."), netUrl.getFile().substring(netUrl.getFile().lastIndexOf('/') + 1)).getAbsolutePath(); // pb empty or conflicting names
               //tmpFile.createNewFile();
               int lastDotIndex = url.lastIndexOf('.', url.lastIndexOf('/') + 1);
               String urlFileExtension = (lastDotIndex == -1) ? "" : url.substring(lastDotIndex); // ex. .html ; NOT .bin else qwazr extractor WebApplicationException: No parser found
               tmpFile = File.createTempFile("pcu_conn_temp_", urlFileExtension, new File(".")); // TODO tmpDir
               file = tmpFile;
               tmpFile.deleteOnExit(); // in case it explodes in between (while parsing...)
               IOUtils.copy(urlIn, new FileOutputStream(tmpFile));
               httpResponseHeaderFields = urlConn.getHeaderFields(); // TODO
               netUrlMediaType = urlConn.getContentType(); // for qwazr extractor
               int semicolonIndex = netUrlMediaType.indexOf(';');
               netUrlMediaType = (semicolonIndex == -1) ? netUrlMediaType : netUrlMediaType.substring(0, semicolonIndex);
            } catch (IOException ioex) {
               crawler.log.debug("Error downloading " + url, ioex); // TODO move up
               throw ioex;
            }
         }
      }
      
      if (crawler.suffixFileFilter != null && !crawler.suffixFileFilter.accept(file)) {
         return null;
      }
      
      // upload content (if any) :
      ///String uploadedContentPath = uploadContent();
      String uploadedContentPath = null;
      try (InputStream contentIn = this.getContentInputStream(file)) {
         if (contentIn != null) {
            PcuFileResult fileRes = crawler.getConnector().getFileApi().storeContent(crawler.getContentStore(), contentIn);
            uploadedContentPath = fileRes.getPath();
         } // else ex. dir
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
         ///metadataRes = crawler.metadataExtractorApi.extract(file.toURI().toASCIIString()); // http://cache.media.education.gouv.fr/file/ICT/44/7/H2020-ICT-2017-1_Liste_projets_retenus_802447.pdf
         metadataRes = crawler.getConnector().getMetadataExtractorApi().extract(file.getName(), netUrlMediaType, new FileInputStream(file));
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
         
         // TODO javax.ws.rs.WebApplicationException: No parser found when gotten from downloaded tmp file with .bin extension
         
         if (log.isDebugEnabled()) {
            log.debug("Error extracting metadata from " + file, e);
         }
         // TODO remove log :
         //| main | 2017-12-01 19:53:15.736 | WARN  | PDSimpleFont:325 - No Unicode mapping for g367 (10) in font YNAHAD+Calibri
      }
      
      // build sample test pcuDoc including fulltext :
      PcuDocument pcuDoc = this.buildPcuDocument(file, url, textContent, uploadedContentPath, httpResponseHeaderFields);
      // and clean :
      pcuDoc.getProperties().remove("http");
      pcuDoc.getMapByPath("meta").clear();
      // TODO rights
      
      // TODO void sample prop values
      List<CrawlBatch> nextCrawlBatches = new ArrayList<CrawlBatch>();
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
         ///List<String> linkUrls = docMetadata.get("links");
         List<String> linkUrls = testLinksStack.isEmpty() ? Collections.emptyList() : testLinksStack.pop();
         ///pcuDoc.setByPath("meta.links", linkUrls); // or content.links ??
         nextCrawlBatches.add(new URLCrawlBatch(linkUrls, crawler)); // TODO depth
      }
      
      crawler.generateAndSetId(pcuDoc); // or in Crawler ?

      if (tmpFile != null) {
         tmpFile.delete();
      }
      
      return new PcuDocumentsMapResult(Arrays.asList(pcuDoc), nextCrawlBatches);
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

   public PcuDocument buildPcuDocument(File testFile, String url, String testContent, String storedFileResPath, Map<String, List<String>> httpResponseHeaderFields) {
      Crawler2 fileCrawler = crawler;
      
      PcuDocument pcuDoc = new PcuDocument();
      pcuDoc.setType(crawler.getType()); // 
      
      // file :
      String testFileAbsoluteSlashedPath = testFile.getAbsolutePath();
      if (File.pathSeparatorChar == '\\') {
         testFileAbsoluteSlashedPath = testFileAbsoluteSlashedPath.replace('\\', '/');
      }
      String globalFilePath = "/" + fileCrawler.getConnector().getConnectorComputerId() + /*url != null ? /host/protocol : /file*/ testFileAbsoluteSlashedPath;
      LinkedHashMap<String, Object> file = new LinkedHashMap<>();
      pcuDoc.getProperties().put("file", file); // TODO of type "file"
      file.put("last_modified", ZonedDateTime.ofInstant(Instant.ofEpochMilli(testFile.lastModified()), ZoneId.systemDefault())); // TODO Q or globally ?! or annotated as global prop @modified ? or LocalDateTime ?
      file.put("name", testFile.getName());
      file.put("path", (url != null) ? url.substring(url.indexOf("://") + 3) : testFileAbsoluteSlashedPath); // TODO analyze as a tree structure ?
      // TODO group, owner (fscrawler : in attributes/)
      // TODO Q also file url, "virtual" path, extension, indexed_chars ? content_type (or in content) ??
      
      // http : (gotten from HTTP request if any)
      LinkedHashMap<String, Object> http = new LinkedHashMap<>();
      pcuDoc.getProperties().put("http", http); // TODO of type "http"
      http.put("url", (url != null) ? url : testFile.toURI().toASCIIString()); // if any ; else file:/// ?? // "http://myserver/myfile.doc"
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
      rights.put("ar", new LinkedHashSet<>(Stream.concat(rights.get("r").stream(), new LinkedHashSet<String>(/*writers*/).stream())
            .collect(Collectors.toSet()))); // done auto in indexing pipeline, allows to inject in query a criteria that restricts to allowed readers
      
      // crawl : (or crawl.id, crawl.synced ?)
      pcuDoc.getProperties().put("crawl_id", "myCrawlSessionOrJobTaskId"); // allows to get more info about crawl session or wrapping crawl job task conf
      pcuDoc.getProperties().put("synced", ZonedDateTime.now()); // fscrawler : file.indexing_date ; or LocalDateTime.now() ?
      ///pcuDoc.getProperties().put("host_id", fileCrawler.getConnectorComputerId()); // ?? (binary) ; NOO already in (global) path
      pcuDoc.getProperties().put("readable_host", /*rootUrl != null ? rootUrl.getHost() : */fileCrawler.getConnector().getConnectorComputerHostName()); // (or readable_path ?)
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
      pcuDoc.getProperties().put("version"/*_local*/, nextLocalOrder()); // local ordering as version (else local_version) => JUG TimeBasedGenerator by cowtowncoder ensures this and is ordered so is enough !
      //pcuDoc.getProperties().put("version", uuidGen.fromString(pcuDoc.getId()).clockSequence());
      pcuDoc.getProperties().put("version_global", nextLamport(/*impactingExternalProcessLamports*/)); // global ordering (lamport timestamp) as version (else global_version)

      // TODO Q date as long timestamp (pcu) or formatted (fscrawler) ?
      
      return pcuDoc;
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
