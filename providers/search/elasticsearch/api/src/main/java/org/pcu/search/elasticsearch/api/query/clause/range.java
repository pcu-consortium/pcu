package org.pcu.search.elasticsearch.api.query.clause;

import java.util.LinkedHashMap;

import org.pcu.search.elasticsearch.api.query.ESQuery;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-range-query.html
 * 
 * @author mdutoo
 *
 */
@JsonTypeInfo(include=As.WRAPPER_OBJECT, use=Id.NAME)
public class range implements ESQuery { // EsQueryStringQuery

   private LinkedHashMap<String,RangeFieldParameters> fieldToRangeParameters = new LinkedHashMap<String,RangeFieldParameters>(3); // Map ???
   @JsonAnyGetter
   public LinkedHashMap<String, RangeFieldParameters> getFieldToRangeParameters() {
      return fieldToRangeParameters;
   }
   @JsonAnySetter
   public void setRangeParameters(String name, RangeFieldParameters rangeParams) {
      this.fieldToRangeParameters.put(name, rangeParams);
   }
   public void setFieldToRangeParameters(LinkedHashMap<String, RangeFieldParameters> fieldToRangeParameters) {
      this.fieldToRangeParameters = fieldToRangeParameters;
   }
   
}
