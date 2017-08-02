package org.pcu.search.elasticsearch.api.query;

import java.util.LinkedHashSet;

/**
 * 
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-body.html
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-from-size.html
 * @author mdutoo
 *
 */
public class ESQueryMessage {
   private ESQuery query;
   private Highlight highlight;
   /** stats groups*/
   private LinkedHashSet<String> stats;

   private String timeout; // ex. 1s
   private int from;
   private int size = 10;
   private Integer terminate_after; // default none
   private Integer batched_reduce_size;
   
   private boolean version = false; // return versions, TODO true in PCU https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-version.html
   
   public ESQuery getQuery() {
      return query;
   }
   public void setQuery(ESQuery query) {
      this.query = query;
   }
   public Highlight getHighlight() {
      return highlight;
   }
   public void setHighlight(Highlight highlight) {
      this.highlight = highlight;
   }
   public LinkedHashSet<String> getStats() {
      return stats;
   }
   public void setStats(LinkedHashSet<String> stats) {
      this.stats = stats;
   }

   public String getTimeout() {
      return timeout;
   }
   public void setTimeout(String timeout) {
      this.timeout = timeout;
   }
   public int getFrom() {
      return from;
   }
   public void setFrom(int from) {
      this.from = from;
   }
   public int getSize() {
      return size;
   }
   public void setSize(int size) {
      this.size = size;
   }
   public Integer getTerminate_after() {
      return terminate_after;
   }
   public void setTerminate_after(Integer terminate_after) {
      this.terminate_after = terminate_after;
   }
   public Integer getBatched_reduce_size() {
      return batched_reduce_size;
   }
   public void setBatched_reduce_size(Integer batched_reduce_size) {
      this.batched_reduce_size = batched_reduce_size;
   }
   public boolean isVersion() {
      return version;
   }
   public void setVersion(boolean version) {
      this.version = version;
   }

}
