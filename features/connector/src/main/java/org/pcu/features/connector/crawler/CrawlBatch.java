package org.pcu.features.connector.crawler;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * A batch (page when paginated) of crawls to be done.
 * @author mardut
 *
 */
public abstract class CrawlBatch {

   /** the index to fill with PCU documents if not the crawler's */
   private String index = null;
   
   ///@JsonIgnore
   @JsonBackReference
   protected Crawler2 crawler;
   
   /*
   public List<CrawlBatch> doNextPageItemsAndReturnNextCrawlBatches() {
      PcuDocumentsMapResult pcuDocsMapRes = buildNextFilePcuDocumentsAndCrawlBatches();

      if (pcuDocsMapRes.getPcuDocuments() != null) { // or hasDoc ?
         crawler.registerForIndexing(pcuDocsMapRes.getPcuDocuments());
      }
      
      return pcuDocsMapRes.getNextCrawlBatches();
   }
   */

   /** for REST ser only */
   public CrawlBatch() {
      
   }
   /** when outside conf ex. in tests */
   public CrawlBatch(Crawler2 crawler) {
      this.crawler = crawler;
   }

   public abstract PcuDocumentsMapResult buildNextPagePcuDocumentsAndCrawlBatches();

   
   public String getIndex() {
      return index;
   }
   public void setIndex(String index) {
      this.index = index;
   }
   /** for deser */
   public void setCrawler(Crawler2 crawler) {
      this.crawler = crawler;
   }
   public Crawler2 getCrawler() {
      return crawler;
   }

}
