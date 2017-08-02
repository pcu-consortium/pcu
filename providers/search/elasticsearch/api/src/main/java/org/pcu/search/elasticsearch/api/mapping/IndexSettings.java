package org.pcu.search.elasticsearch.api.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules.html
 * @author mdutoo
 *
 */
public class IndexSettings {
   
   private Analysis analysis;
   
   @JsonProperty("index.requests.cache.enable")
   private boolean indexRequestsCacheEnable = true;
   @JsonProperty("index.number_of_replicas")
   private int index_number_of_replicas = 1;
   @JsonProperty("index.refresh_interval")
   private String index_refresh_interval = "1s";
   // TODO more
   
   public Analysis getAnalysis() {
      return analysis;
   }
   public void setAnalysis(Analysis analysis) {
      this.analysis = analysis;
   }
   public boolean isIndexRequestsCacheEnable() {
      return indexRequestsCacheEnable;
   }
   public void setIndexRequestsCacheEnable(boolean indexRequestsCacheEnable) {
      this.indexRequestsCacheEnable = indexRequestsCacheEnable;
   }
   public int getIndex_number_of_replicas() {
      return index_number_of_replicas;
   }
   public void setIndex_number_of_replicas(int index_number_of_replicas) {
      this.index_number_of_replicas = index_number_of_replicas;
   }
   public String getIndex_refresh_interval() {
      return index_refresh_interval;
   }
   public void setIndex_refresh_interval(String index_refresh_interval) {
      this.index_refresh_interval = index_refresh_interval;
   }

}
