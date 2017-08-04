package org.pcu.search.elasticsearch.api.query;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-rescore.html
 * @author mardut
 *
 */
public class ESRescore {
   
   private ESRescoreQuery query = new ESRescoreQuery();
   /** defaults to to from and size */
   private Integer window_size;
   
   public ESRescoreQuery getQuery() {
      return query;
   }
   public void setQuery(ESRescoreQuery query) {
      this.query = query;
   }
   public Integer getWindow_size() {
      return window_size;
   }
   public void setWindow_size(Integer window_size) {
      this.window_size = window_size;
   }

}
