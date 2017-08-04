package org.pcu.search.elasticsearch.api.query;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import org.pcu.search.elasticsearch.api.query.clause.ESScript;

/**
 * 
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-body.html
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-from-size.html
 * @author mdutoo
 *
 */
public class ESQueryMessage {
   private ESQuery query;
   /** _score or field name to asc or desc map. If set, always add _score : desc at the end.
    * Doesn't support ES' min/max/sum/avg/median "mode" for list fields https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-sort.html */
   private LinkedHashMap<String,String> sort;
   /** https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-post-filter.html */
   private ESQuery post_filter;
   private Highlight highlight;
   private List<ESRescore> rescore;
   /** stats groups*/
   private LinkedHashSet<String> stats;
   private LinkedHashMap<String,ESScript> script_fields;

   private boolean explain = false; // https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-explain.html
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
   public LinkedHashMap<String, String> getSort() {
      return sort;
   }
   public void setSort(LinkedHashMap<String, String> sort) {
      this.sort = sort;
   }
   public ESQuery getPost_filter() {
      return post_filter;
   }
   public void setPost_filter(ESQuery post_filter) {
      this.post_filter = post_filter;
   }
   public Highlight getHighlight() {
      return highlight;
   }
   public void setHighlight(Highlight highlight) {
      this.highlight = highlight;
   }
   public List<ESRescore> getRescore() {
      return rescore;
   }
   public void setRescore(List<ESRescore> rescore) {
      this.rescore = rescore;
   }
   public LinkedHashSet<String> getStats() {
      return stats;
   }
   public void setStats(LinkedHashSet<String> stats) {
      this.stats = stats;
   }
   public LinkedHashMap<String, ESScript> getScript_fields() {
      return script_fields;
   }
   public void setScript_fields(LinkedHashMap<String, ESScript> script_fields) {
      this.script_fields = script_fields;
   }
   
   public boolean isExplain() {
      return explain;
   }
   public void setExplain(boolean explain) {
      this.explain = explain;
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
