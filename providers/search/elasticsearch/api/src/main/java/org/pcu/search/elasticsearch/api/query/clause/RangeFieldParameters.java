package org.pcu.search.elasticsearch.api.query.clause;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonSubTypes;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-range-query.html
 * TODO Q or templatized by type (String, Numeric, DateTime though also date math ex. now-1d/d) ?
 * @author mardut
 *
 */
public class RangeFieldParameters {

   @JsonSubTypes({ @JsonSubTypes.Type(String.class), @JsonSubTypes.Type(Long.class),
      @JsonSubTypes.Type(Double.class), @JsonSubTypes.Type(ZonedDateTime.class) })
   private Object gte;
   @JsonSubTypes({ @JsonSubTypes.Type(String.class), @JsonSubTypes.Type(Long.class),
      @JsonSubTypes.Type(Double.class), @JsonSubTypes.Type(ZonedDateTime.class) })
   private Object gt;
   @JsonSubTypes({ @JsonSubTypes.Type(String.class), @JsonSubTypes.Type(Long.class),
      @JsonSubTypes.Type(Double.class), @JsonSubTypes.Type(ZonedDateTime.class) })
   private Object lte;
   @JsonSubTypes({ @JsonSubTypes.Type(String.class), @JsonSubTypes.Type(Long.class),
      @JsonSubTypes.Type(Double.class), @JsonSubTypes.Type(ZonedDateTime.class) })
   private Object lt;
   private float boost = 1.0f;
   
   public Object getGte() {
      return gte;
   }
   public void setGte(Object gte) {
      this.gte = gte;
   }
   public Object getGt() {
      return gt;
   }
   public void setGt(Object gt) {
      this.gt = gt;
   }
   public Object getLte() {
      return lte;
   }
   public void setLte(Object lte) {
      this.lte = lte;
   }
   public Object getLt() {
      return lt;
   }
   public void setLt(Object lt) {
      this.lt = lt;
   }
   public float getBoost() {
      return boost;
   }
   public void setBoost(float boost) {
      this.boost = boost;
   }

}
