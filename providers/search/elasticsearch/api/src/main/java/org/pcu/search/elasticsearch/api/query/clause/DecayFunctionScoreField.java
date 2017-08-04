package org.pcu.search.elasticsearch.api.query.clause;


/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-function-score-query.html#function-decay
 * @author mardut
 *
 */
public class DecayFunctionScoreField {
   /** numeric : any number, geo : geo point, date : formatted date */
   private String origin;
   /** numeric : any number, geo : 1km, 12m..., date : 10d, 1h... */
   private String scale;
   private float offset = 0f;
   /** score at "scale" distance */
   private float decay = 0.5f;
   
   public String getOrigin() {
      return origin;
   }
   public void setOrigin(String origin) {
      this.origin = origin;
   }
   public String getScale() {
      return scale;
   }
   public void setScale(String scale) {
      this.scale = scale;
   }
   public float getOffset() {
      return offset;
   }
   public void setOffset(float offset) {
      this.offset = offset;
   }
   public float getDecay() {
      return decay;
   }
   public void setDecay(float decay) {
      this.decay = decay;
   }

}
