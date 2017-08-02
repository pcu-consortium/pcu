package org.pcu.search.elasticsearch.api;

import java.util.LinkedHashMap;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class ESApiErrorRootCause {
   
   private String type;
   private String reason;
   private String index_uuid;
   private String index;
   
   /** ex. delete mapping error : "resource.type":"index_or_alias","resource.id":"files" */
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
   public String getIndex_uuid() {
      return index_uuid;
   }
   public void setIndex_uuid(String index_uuid) {
      this.index_uuid = index_uuid;
   }
   public String getIndex() {
      return index;
   }
   public void setIndex(String index) {
      this.index = index;
   }

}
