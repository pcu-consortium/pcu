package org.pcu.search.elasticsearch.api.query;

public class ESRescoreQuery {
   private ESQuery rescore_query;
   private float query_weight;
   private float rescore_query_weight;
   
   public ESQuery getRescore_query() {
      return rescore_query;
   }
   public void setRescore_query(ESQuery rescore_query) {
      this.rescore_query = rescore_query;
   }
   public float getQuery_weight() {
      return query_weight;
   }
   public void setQuery_weight(float query_weight) {
      this.query_weight = query_weight;
   }
   public float getRescore_query_weight() {
      return rescore_query_weight;
   }
   public void setRescore_query_weight(float rescore_query_weight) {
      this.rescore_query_weight = rescore_query_weight;
   }

}
