package org.pcu.search.elasticsearch.api;

import java.util.LinkedHashMap;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

/**
 * see error_trace=true in https://www.elastic.co/guide/en/elasticsearch/reference/current/common-options.html
 * @author mardut
 *
 */
public class ESApiErrorCause {
   
   private String type;
   private String reason;
   /** enabled by error_trace=true common param https://www.elastic.co/guide/en/elasticsearch/reference/current/common-options.html */
   private String stack_trace;
   
   /** ex.
    * _search error : index_uuid, index, resource.type, resource.id https://discuss.elastic.co/t/elasticsearch-index-issues/73871
    * delete mapping error : "resource.type":"index_or_alias","resource.id":"files"
    * script error : "script_stack":["org.elasticsearch.search.lookup.LeafDocLookup.g...", ...],"script":"doc['my_field'].value...","lang":"painless","caused_by":{"type":"illegal_argument_exception","reason":"No field found for [my_field] in mapping with types []"}}
    */
   private LinkedHashMap<String,Object> properties;
   @JsonAnyGetter
   public LinkedHashMap<String, Object> getProperties() {
      return this.properties;
   }
   @JsonAnySetter
   public void setProperty(String name, Object value) {
      if (this.properties == null) {
         this.properties = new LinkedHashMap<String,Object>();
      }
      this.properties.put(name, value);
   }
   public void setProperties(LinkedHashMap<String, Object> properties) {
      this.properties = properties;
   }
   
   public String getType() {
      return type;
   }
   public void setType(String type) {
      this.type = type;
   }
   public String getReason() {
      return reason;
   }
   public void setReason(String reason) {
      this.reason = reason;
   }
   public String getStack_trace() {
      return stack_trace;
   }
   public void setStack_trace(String stack_trace) {
      this.stack_trace = stack_trace;
   }

}
