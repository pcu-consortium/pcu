package org.pcu.features.connector.crawler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class URLCrawlBatch extends ItemCrawlBatch<String> {

   private List<String> toCrawl;
   private String previousPageLastCrawled = null;

   /** for REST ser only */
   public URLCrawlBatch() {
      super(null);
   }
   
   public URLCrawlBatch(List<String> toCrawl, Crawler2 crawler) {
      super(crawler);
      this.toCrawl = toCrawl;
   }

   @Override
   public List<String> nextPageItems() {
      if (previousPageLastCrawled == toCrawl.get(toCrawl.size() - 1)) {
         return null;
      }
      this.toCrawl = this.toCrawl.stream()
            .filter(url -> !crawler.getUrlBloomFilter().mightContain(url)).collect(Collectors.toList());
      if (this.toCrawl.isEmpty()) {
         return null; // done
      }
      this.previousPageLastCrawled = toCrawl.get(toCrawl.size() - 1);
      return toCrawl;
   }

   protected PcuDocumentsMapResult buildPcuDocuments(String toBeCrawled) throws FileNotFoundException, IOException {
      PcuDocumentsMapResult res = this.buildPcuDocuments(null, toBeCrawled);
      crawler.getUrlBloomFilter().put(toBeCrawled);
      return res;
   }

}
