package org.pcu.search.elasticsearch.api.query.clause;

import java.util.List;

import org.pcu.search.elasticsearch.api.query.ESQuery;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;


/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-bool-query.html
 * @author mdutoo
 *
 */
@JsonTypeInfo(include=As.WRAPPER_OBJECT, use=Id.NAME)
public class bool extends ESQuery { // ESBoolQuery
   public List<ESQuery> must; // TODO DONE ? accept single element without array (required on server-side only)
   public List<ESQuery> must_not;
   public List<ESQuery> should;
   public List<ESQuery> filter;
   public String minimum_should_match; // "2<-25% 9<3"
   public float boost = 1.0f;
   
   public List<ESQuery> getMust() {
      return must;
   }
   public void setMust(List<ESQuery> must) {
      this.must = must;
   }
   public List<ESQuery> getMust_not() {
      return must_not;
   }
   public void setMust_not(List<ESQuery> must_not) {
      this.must_not = must_not;
   }
   public List<ESQuery> getShould() {
      return should;
   }
   public void setShould(List<ESQuery> should) {
      this.should = should;
   }
   public List<ESQuery> getFilter() {
      return filter;
   }
   public void setFilter(List<ESQuery> filter) {
      this.filter = filter;
   }
   public String getMinimum_should_match() {
      return minimum_should_match;
   }
   public void setMinimum_should_match(String minimum_should_match) {
      this.minimum_should_match = minimum_should_match;
   }
   public float getBoost() {
      return boost;
   }
   public void setBoost(float boost) {
      this.boost = boost;
   }
   
}
