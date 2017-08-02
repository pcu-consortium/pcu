package org.pcu.search.elasticsearch.api.query.clause;

import java.util.List;

import org.pcu.search.elasticsearch.api.query.ESQuery;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * multi_match
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-multi-match-query.html
 * @author mdutoo
 *
 */
@JsonTypeInfo(include=As.WRAPPER_OBJECT, use=Id.NAME)
public class multi_match extends ESQuery { // ESMultiMatchQuery

   private String query;
   private List<String> fields;
   private String type; // best_fields (default), most_fields (multi fields), cross_fields (dedup), phrase, phrase_prefix
   private Float tie_breaker;
   
   private String analyzer; // also phrase(_prefix)
   private String boost; // also phrase(_prefix)
   private String operator; // or (default), and
   private int minimum_should_match;
   private String fuzziness;
   private boolean lenient = false; // also phrase(_prefix)
   private String prefix_length; // (fuzzy)
   private String max_expansions; // (fuzzy) also phrase_prefix
   private String rewrite;
   private String zero_terms_query; // none (default), all ; also phrase(_prefix)
   private String cutoff_frequency; // (dynamic stopwords)
   
   private String slop; // phrase(_prefix)
   
   // only match query : fuzzy_transpositions, rewrite method = top_terms_blended_freqs_${max_expansions}

   public String getQuery() {
      return query;
   }
   public void setQuery(String query) {
      this.query = query;
   }
   public List<String> getFields() {
      return fields;
   }
   public void setFields(List<String> fields) {
      this.fields = fields;
   }
   public String getType() {
      return type;
   }
   public void setType(String type) {
      this.type = type;
   }
   public Float getTie_breaker() {
      return tie_breaker;
   }
   public void setTie_breaker(Float tie_breaker) {
      this.tie_breaker = tie_breaker;
   }
   public String getAnalyzer() {
      return analyzer;
   }
   public void setAnalyzer(String analyzer) {
      this.analyzer = analyzer;
   }
   public String getBoost() {
      return boost;
   }
   public void setBoost(String boost) {
      this.boost = boost;
   }
   public String getOperator() {
      return operator;
   }
   public void setOperator(String operator) {
      this.operator = operator;
   }
   public int getMinimum_should_match() {
      return minimum_should_match;
   }
   public void setMinimum_should_match(int minimum_should_match) {
      this.minimum_should_match = minimum_should_match;
   }
   public String getFuzziness() {
      return fuzziness;
   }
   public void setFuzziness(String fuzziness) {
      this.fuzziness = fuzziness;
   }
   public boolean isLenient() {
      return lenient;
   }
   public void setLenient(boolean lenient) {
      this.lenient = lenient;
   }
   public String getPrefix_length() {
      return prefix_length;
   }
   public void setPrefix_length(String prefix_length) {
      this.prefix_length = prefix_length;
   }
   public String getMax_expansions() {
      return max_expansions;
   }
   public void setMax_expansions(String max_expansions) {
      this.max_expansions = max_expansions;
   }
   public String getRewrite() {
      return rewrite;
   }
   public void setRewrite(String rewrite) {
      this.rewrite = rewrite;
   }
   public String getZero_terms_query() {
      return zero_terms_query;
   }
   public void setZero_terms_query(String zero_terms_query) {
      this.zero_terms_query = zero_terms_query;
   }
   public String getCutoff_frequency() {
      return cutoff_frequency;
   }
   public void setCutoff_frequency(String cutoff_frequency) {
      this.cutoff_frequency = cutoff_frequency;
   }
   public String getSlop() {
      return slop;
   }
   public void setSlop(String slop) {
      this.slop = slop;
   }
   
}
