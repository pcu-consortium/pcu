package org.pcu.search.elasticsearch.api.query.clause;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-function-score-query.html#function-field-value-factor
 * @author mardut
 *
 */
public class FieldValueFactorFunctionScore {
   private String field;
   private float factor = 1;
   /** none (default), log, log1p (to avoid log(0)...), log2p, ln, ln1p, ln2p, square, sqrt, reciprocal */
   private String modifier = "none";
   /** value if no field */
   private float missing = 1; // 1 default https://github.com/elastic/elasticsearch/issues/7788
   
   public String getField() {
      return field;
   }
   public void setField(String field) {
      this.field = field;
   }
   public float getFactor() {
      return factor;
   }
   public void setFactor(float factor) {
      this.factor = factor;
   }
   public String getModifier() {
      return modifier;
   }
   public void setModifier(String modifier) {
      this.modifier = modifier;
   }
   public float getMissing() {
      return missing;
   }
   public void setMissing(float missing) {
      this.missing = missing;
   }
   
}
