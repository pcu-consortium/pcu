package org.pcu.features.connector;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import org.pcu.providers.file.api.PcuFileApi;
import org.pcu.providers.file.api.PcuFileResult;
import org.pcu.providers.metadata.api.PcuMetadataApi;
import org.pcu.providers.search.api.PcuDocument;
import org.pcu.providers.search.api.PcuSearchApi;
import org.pcu.providers.search.api.PcuSearchEsClientApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

public abstract class Crawler<T> {

   protected final Logger log = LoggerFactory.getLogger(this.getClass().getName());
   
   /** main (or default) index */
   protected String index;
   /** main (or default) type */
   protected String type;
   private String contentStore;
   private int bulkSize = 0;
   private String connectorComputerHostName;
   private String connectorComputerId;
   
   /** changes between crawl sessions / job tasks */
   private String crawlId;
   
   protected PcuSearchEsClientApi searchEsApi;
   protected PcuSearchApi searchApi;
   private PcuFileApi fileApi;
   protected PcuMetadataApi metadataExtractorApi;

   protected TimeBasedGenerator uuidGenerator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
   
   protected LinkedBlockingQueue<T> toBeCrawledQeue = new LinkedBlockingQueue<T>(); // NB. no fixed capacity
   private ArrayList<PcuDocument> toBeIndexedPcuDocuments = new ArrayList<PcuDocument>(1000);
   
   // stats :
   private long crawled = 0;
   private long indexed = 0;


   public void init() {
      // TODO if already running KO
      
      if (this.contentStore == null) {
         this.contentStore = this.index;
      }
      try {
         // TODO or in constructor ?
         this.setConnectorComputerHostName(CrawlUtils.hostName()); // or IP by getCanonicalHostName(), or both ?
         //this.setConnectorComputerId(uuidGenerator.getEthernetAddress().toString()); // Ethernet address not good enough
         this.setConnectorComputerId(CrawlUtils.macAddress());
      } catch (IOException ioex) {
         // TODO rather use conf'd values
         throw new RuntimeException("Error initing crawl " + this, ioex);
      }
      
      this.crawlId = this.uuidGenerator.generate().toString(); // unique, contains start time (timestamp)
   }
   
   // TODO LATER Kafka topics to scale it up !(?)
   // TODO Q also links or specific ?
   /** ex. dir.listChildren() or db.nextPage() */
   protected abstract List<T> getNextToBeCrawled(T toBeCrawledOrLastCrawled);
   /** TODO LATER several contents per crawled item ? */
   protected abstract InputStream getContentInputStream(T toBeCrawled) throws Exception;
   /** returns indexable mapped doc, and optional dependent docs ex. category... */
   // & if doc then extract
   protected abstract List<PcuDocument> buildPcuDocuments(T crawled, String uploadPath);
   
   public void crawlIteration() throws CrawlCompletedException, Exception {
      T toBeCrawled = toBeCrawledQeue.poll();
      if (toBeCrawled == null) {
         throw new CrawlCompletedException();
      }
      
      // upload content :
      String uploadedContentPath = null;
      try (InputStream contentIn = getContentInputStream(toBeCrawled)) {
         if (contentIn != null) {
            PcuFileResult fileRes = fileApi.storeContent(this.getContentStore(), contentIn);
            uploadedContentPath = fileRes.getPath();
         } // else ex. dir
      }
      
      // builds & extract :
      List<PcuDocument> pcuDocs = buildPcuDocuments(toBeCrawled, uploadedContentPath);
      if (pcuDocs != null) { // or hasDoc ?
         // TODO get links and add to queue
         registerForIndexing(pcuDocs);
      } // else ex. folder
      
      this.crawled++;
      List<T> nextToBeCrawled = getNextToBeCrawled(toBeCrawled);
      toBeCrawledQeue.addAll(nextToBeCrawled); // TODO check that inserted at the tail
   }
   
   private void registerForIndexing(List<PcuDocument> pcuDocs) {
      toBeIndexedPcuDocuments.addAll(pcuDocs);
      if (this.toBeIndexedPcuDocuments.size() > this.bulkSize) {
         // TODO bulk
         searchApi.index(index, pcuDocs.get(0));
         // TODO check result & log
         this.indexed += toBeIndexedPcuDocuments.size();
         toBeIndexedPcuDocuments.clear();
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
      UUID uuidWithTimestampAndLocalOrder = uuidGenerator.generate();
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
   
   public String getConnectorComputerHostName() {
      return connectorComputerHostName;
   }
   public void setConnectorComputerHostName(String connectorComputerHostName) {
      this.connectorComputerHostName = connectorComputerHostName;
   }
   public String getConnectorComputerId() {
      return connectorComputerId;
   }
   public void setConnectorComputerId(String connectorComputerId) {
      this.connectorComputerId = connectorComputerId;
   }

   // for stats only :
   public LinkedBlockingQueue<T> getToBeCrawledQeue() {
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

   public void setSearchEsApi(PcuSearchEsClientApi searchEsApi) {
      this.searchEsApi = searchEsApi;
   }
   public void setSearchApi(PcuSearchApi searchApi) {
      this.searchApi = searchApi;
   }
   public void setFileApi(PcuFileApi fileApi) {
      this.fileApi = fileApi;
   }
   public void setMetadataExtractorApi(PcuMetadataApi metadataExtractorApi) {
      this.metadataExtractorApi = metadataExtractorApi;
   }
   
}