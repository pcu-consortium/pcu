package org.pcu.features.connector.crawler;

import java.util.List;

import org.pcu.providers.search.api.PcuDocument;

public class PcuDocumentsMapResult {
   private List<PcuDocument> pcuDocuments;
   private List<CrawlBatch> nextCrawlBatches;
   public PcuDocumentsMapResult(List<PcuDocument> pcuDocuments, List<CrawlBatch> nextCrawlBatches) {
      this.pcuDocuments = pcuDocuments;
      this.nextCrawlBatches = nextCrawlBatches;
   }
   public List<PcuDocument> getPcuDocuments() {
      return pcuDocuments;
   }
   public List<CrawlBatch> getNextCrawlBatches() {
      return nextCrawlBatches;
   }
}