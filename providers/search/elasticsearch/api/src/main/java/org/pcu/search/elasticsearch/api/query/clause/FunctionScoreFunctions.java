package org.pcu.search.elasticsearch.api.query.clause;

import java.util.LinkedHashMap;

/**
 * only one of this class' fields must be set (the chosen function), save weight within a FunctionScoreFilter
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-function-score-query.html#score-functions
 * @author mardut
 *
 */
public abstract class FunctionScoreFunctions {
   
   private ESScript script_score;
   /** function_score weight function, OR FunctionScoreFilter comparative weight
    * (the only case where 2 fields may be set in this class) */
   private Integer weight;
   //private random_score random_score; // not supported, because costly : Using this feature will load field data for _uid, which can be a memory intensive operation since the values are unique.
   private FieldValueFactorFunctionScore field_value_factor;
   
   // decay functions :
   /** map of the SINGLE field name to its DecayFunctionScoreField, in case of gauss decay function */
   private LinkedHashMap<String,DecayFunctionScoreFieldParameters> gauss; // TODO SingleFieldLinkedHashMap
   /** map of the SINGLE field name to its DecayFunctionScoreField, in case of exp decay function */
   private LinkedHashMap<String,DecayFunctionScoreFieldParameters> exp; // TODO SingleFieldLinkedHashMap
   /** map of the SINGLE field name to its DecayFunctionScoreField, in case of linear decay function */
   private LinkedHashMap<String,DecayFunctionScoreFieldParameters> linear; // TODO SingleFieldLinkedHashMap
   
   public ESScript getScript_score() {
      return script_score;
   }
   public void setScript_score(ESScript script_score) {
      this.script_score = script_score;
   }
   public Integer getWeight() {
      return weight;
   }
   public void setWeight(Integer weight) {
      this.weight = weight;
   }
   public FieldValueFactorFunctionScore getField_value_factor() {
      return field_value_factor;
   }
   public void setField_value_factor(FieldValueFactorFunctionScore field_value_factor) {
      this.field_value_factor = field_value_factor;
   }
   public LinkedHashMap<String, DecayFunctionScoreFieldParameters> getGauss() {
      return gauss;
   }
   public void setGauss(LinkedHashMap<String, DecayFunctionScoreFieldParameters> gauss) {
      this.gauss = gauss;
   }
   public LinkedHashMap<String, DecayFunctionScoreFieldParameters> getExp() {
      return exp;
   }
   public void setExp(LinkedHashMap<String, DecayFunctionScoreFieldParameters> exp) {
      this.exp = exp;
   }
   public LinkedHashMap<String, DecayFunctionScoreFieldParameters> getLinear() {
      return linear;
   }
   public void setLinear(LinkedHashMap<String, DecayFunctionScoreFieldParameters> linear) {
      this.linear = linear;
   }
   
}
