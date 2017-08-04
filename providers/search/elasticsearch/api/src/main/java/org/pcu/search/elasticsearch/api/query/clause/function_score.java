package org.pcu.search.elasticsearch.api.query.clause;

import java.util.List;

import org.pcu.search.elasticsearch.api.query.ESQuery;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * Either "functions" or one of the leaf functions (see ESQuery) must be set.
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-function-score-query.html
 * @author mdutoo
 *
 */
@JsonTypeInfo(include=As.WRAPPER_OBJECT, use=Id.NAME)
public class function_score extends FunctionScoreFunctions implements ESQuery { // ESFunctionScoreQuery
   
   private ESQuery query;
   private float boost = 1.0f;
   
   // multi functions :
   private List<FunctionScoreFilter> functions;
   /** how the computed scores are combined ; multiply (default), sum, avg, first, max, min */
   private String score_mode;
   //private Integer weight; in FunctionScoreFunctions
   /** multiply (default), replace, sum, avg, max, min */
   private String boost_mode;
   
   public ESQuery getQuery() {
      return query;
   }
   public void setQuery(ESQuery query) {
      this.query = query;
   }
   public float getBoost() {
      return boost;
   }
   public void setBoost(float boost) {
      this.boost = boost;
   }
   public List<FunctionScoreFilter> getFunctions() {
      return functions;
   }
   public void setFunctions(List<FunctionScoreFilter> functions) {
      this.functions = functions;
   }
   public String getScore_mode() {
      return score_mode;
   }
   public void setScore_mode(String score_mode) {
      this.score_mode = score_mode;
   }
   public String getBoost_mode() {
      return boost_mode;
   }
   public void setBoost_mode(String boost_mode) {
      this.boost_mode = boost_mode;
   }
   
}
