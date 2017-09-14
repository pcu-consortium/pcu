package org.pcu.search.elasticsearch.api.query.clause;

public class PrefixFieldParameters {
   
   private String value;
   private float boost = 1.0f;
   
   public String getValue() {
      return value;
   }
   public void setValue(String value) {
      this.value = value;
   }
   public float getBoost() {
      return boost;
   }
   public void setBoost(float boost) {
      this.boost = boost;
   }

}
