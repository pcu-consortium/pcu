package org.pcu.search.elasticsearch.api.query;

import java.util.List;

public class Hits {
   
   private int total;
   private float max_score;
   private List<Hit> hits;
   
   public int getTotal() {
      return total;
   }
   public void setTotal(int total) {
      this.total = total;
   }
   public float getMax_score() {
      return max_score;
   }
   public void setMax_score(float max_score) {
      this.max_score = max_score;
   }
   public List<Hit> getHits() {
      return hits;
   }
   public void setHits(List<Hit> hits) {
      this.hits = hits;
   }

}
