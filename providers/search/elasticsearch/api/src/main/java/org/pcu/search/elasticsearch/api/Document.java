package org.pcu.search.elasticsearch.api;

import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiModel;


/**
 * Used to provide (index, but also more_like_this & term vectors's "artificial" documents) or get (query hit) documents.
 * @author mardut
 *
 */
@ApiModel(value = "A PCU search Document")
@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.ANY, setterVisibility = Visibility.NONE)
// NB. getter but not fields to allow extending classes such as PcuConfiguration to add their own data without adding an explicit "properties" field
public class Document {

   /* ?????????
   @ApiModelProperty(value = "id", position=0, required=true)
   private String id; // 1, 6a8ca01c-7896-48e9-81cc-9f70661fcb32
   //@ApiModelProperty(value = ">= 0, and less than around 9.2e+18", position=1, required=true) // TODO Q required even for indexing ???
   private long version;
   */

   /** Document actual (business) properties. They are of the types supported by JSON (on Jackson) :
    * String, Boolean, Double, Map, List
    * see http://en.wikipedia.org/wiki/JSON#Data_types.2C_syntax_and_example */
   //@JsonIgnore // NO error 204 no content, rather not visible and explicitly @JsonProperty actual fields
   private LinkedHashMap<String,Object> properties; // Map ???

   // TODO to unmarshall embedded documents as (Sub)Documents rather than maps ??
   // (and if possible same for embedded maps ???) BUT can't know when is embedded resource or map
   @JsonSubTypes({ @JsonSubTypes.Type(String.class), @JsonSubTypes.Type(Boolean.class),
      @JsonSubTypes.Type(Double.class), @JsonSubTypes.Type(ZonedDateTime.class),
      @JsonSubTypes.Type(Map.class), @JsonSubTypes.Type(List.class) })
   ///   @JsonSubTypes.Type(DCSubResource(Map).class), @JsonSubTypes.Type(DCList.class) })
   @JsonAnyGetter
   public Map<String, Object> getProperties() {
      return this.properties;
   }
   @JsonSubTypes({ @JsonSubTypes.Type(String.class), @JsonSubTypes.Type(Boolean.class),
      @JsonSubTypes.Type(Double.class), @JsonSubTypes.Type(ZonedDateTime.class),
      @JsonSubTypes.Type(Map.class), @JsonSubTypes.Type(List.class) })
   ///   @JsonSubTypes.Type(DCSubResource(Map).class), @JsonSubTypes.Type(DCList.class) })
   @JsonAnySetter
   public void setProperty(String name, Object value) {
      this.properties.put(name, value);
   }
   public void setProperties(LinkedHashMap<String, Object> properties) {
      this.properties = properties;
   }

   public Document() {
      this.properties = new LinkedHashMap<String,Object>();
   }
   public Document(LinkedHashMap<String,Object> properties) {
      this.properties = properties;
   }
   

   /**
    * Helper for building Document lists
    * ex. doc.listBuilder().add("1").add("2").build()
    * @return
    */
   /*public static ImmutableList.Builder<Object> listBuilder() {
      return new ImmutableList.Builder<Object>();
   }*/
   
   /** mutable map, to help update / enrich props
    * TODO outside in utils */
   public static class MapBuilder<K,V> {
      private LinkedHashMap<K,V> map;
      public MapBuilder(LinkedHashMap<K,V> map) {
         this.map = map;
      }
      public MapBuilder() {
         this.map = new LinkedHashMap<K,V>();
      }
      public MapBuilder(int initialCapacity) {
         this.map = new LinkedHashMap<K,V>(initialCapacity);
      }
      public MapBuilder<K, V> put(K key, V value) {
         this.map.put(key, value);
         return this;
      }
      public MapBuilder<K, V> putAll(LinkedHashMap<K,V> map) {
         this.map.putAll(map);
         return this;
      }
      public Map<K, V> build() {
         return map;
      }
   }
   
   // TODO ?
   private static ObjectMapper documentObjectMapper = new ObjectMapper(); // TODO custom ?
   public String toString() {
      try {
         return documentObjectMapper.writeValueAsString(this);
      } catch (JsonProcessingException e) {
         return "Document[" + properties.toString() + ", bad json]";
      }
   }
   
}
