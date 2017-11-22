package org.pcu.features.search.server;

public class FileCrawler extends Crawler {
   private String crawledFileHost;
   public String getCrawledFileHost() {
      return crawledFileHost;
   }
   public void setCrawledFileHost(String crawledFileHost) {
      this.crawledFileHost = crawledFileHost;
   }
}