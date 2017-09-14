package org.pcu.search.elasticsearch.api.query.clause;

import java.util.LinkedHashMap;

import org.pcu.search.elasticsearch.api.query.ESQuery;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-prefix-query.html
 * @author mdutoo
 *
 */
@JsonTypeInfo(include=As.WRAPPER_OBJECT, use=Id.NAME)
public class prefix implements ESQuery { // ESTermsQuery
   
   private LinkedHashMap<String,PrefixFieldParameters> fieldToPrefixParameters = new LinkedHashMap<String,PrefixFieldParameters>(3); // Map ???
   @JsonAnyGetter
   public LinkedHashMap<String, PrefixFieldParameters> getFieldToPrefixParameters() {
      return fieldToPrefixParameters;
   }
   @JsonAnySetter
   public void setPrefixParameters(String name, PrefixFieldParameters value) {
      this.fieldToPrefixParameters.put(name, value);
   }
   public void setFieldToPrefixParameters(LinkedHashMap<String, PrefixFieldParameters> fieldToPrefixParameters) {
      this.fieldToPrefixParameters = fieldToPrefixParameters;
   }
   
}
