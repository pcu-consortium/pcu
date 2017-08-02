package org.pcu.search.elasticsearch.api.query;

import org.pcu.search.elasticsearch.api.ShardResult;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-body.html
 * @author mdutoo
 *
 */
public class SearchResult {

   private int took; // server-side processing time
   private boolean timed_out;
   private boolean terminated_early = false;
   private ShardResult _shards;
   private Hits hits;
   
   public int getTook() {
      return took;
   }
   public void setTook(int took) {
      this.took = took;
   }
   public boolean isTimed_out() {
      return timed_out;
   }
   public void setTimed_out(boolean timed_out) {
      this.timed_out = timed_out;
   }
   public boolean isTerminated_early() {
      return terminated_early;
   }
   public void setTerminated_early(boolean terminated_early) {
      this.terminated_early = terminated_early;
   }
   public ShardResult get_shards() {
      return _shards;
   }
   public void set_shards(ShardResult _shards) {
      this._shards = _shards;
   }
   public Hits getHits() {
      return hits;
   }
   public void setHits(Hits hits) {
      this.hits = hits;
   }

}
