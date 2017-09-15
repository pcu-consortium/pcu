package org.pcu.search.elasticsearch.api.query.clause;

import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.pcu.search.elasticsearch.api.query.ESQuery;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * exact value (analyzed if "text", not analyzed if "keyword")
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-terms-query.html
 * NB. use this instead of ESTermQuery
 * ex.
 * curl 'localhost:9200/files/file/_search?pretty' -H 'Content-Type: application/json' -d'
{
  "query" : {
    "terms" : {
        "content.length" : [1234123]
    }
  }
}
'
 * @author mdutoo
 *
 */
@JsonTypeInfo(include=As.WRAPPER_OBJECT, use=Id.NAME)
public class terms implements ESQuery { // ESTermsQuery
   
   /** ex. list : [ "kimchy", "elasticsearch" ]
    * ex. lookup : { "index" : "users", "type" : "user", "id" : "2", "path" : "followers"(, routing:"...") }
    * They are of the types supported by JSON (on Jackson) :
    * String, Boolean, Double, Map, List
    * see http://en.wikipedia.org/wiki/JSON#Data_types.2C_syntax_and_example */
   //@JsonIgnore // NO error 204 no content, rather not visible and explicitly @JsonProperty actual fields
   private LinkedHashMap<String,Object> fieldToTermListOrLookupMap = new LinkedHashMap<String,Object>(3); // Map ???
   @JsonSubTypes({ @JsonSubTypes.Type(String.class), @JsonSubTypes.Type(Boolean.class),
      @JsonSubTypes.Type(Double.class), @JsonSubTypes.Type(ZonedDateTime.class),
      @JsonSubTypes.Type(Map.class), @JsonSubTypes.Type(List.class) })
   @JsonAnyGetter
   public LinkedHashMap<String, Object> getFieldToTermListOrLookupMap() {
      return fieldToTermListOrLookupMap;
   }
   @JsonSubTypes({ @JsonSubTypes.Type(String.class), @JsonSubTypes.Type(Boolean.class),
      @JsonSubTypes.Type(Double.class), @JsonSubTypes.Type(ZonedDateTime.class),
      @JsonSubTypes.Type(Map.class), @JsonSubTypes.Type(List.class) })
   @JsonAnySetter
   public void setTermListOrLookupMap(String name, Object value) {
      this.fieldToTermListOrLookupMap.put(name, value);
   }
   public void setFieldToTermListOrLookupMap(LinkedHashMap<String, Object> fieldToTermListOrLookupMap) {
      this.fieldToTermListOrLookupMap = fieldToTermListOrLookupMap;
   }
   
}
