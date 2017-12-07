package org.pcu.providers.search.api;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;

import io.swagger.annotations.ApiModel;

/**
 * TODO better : pcu id, version (and / or global increasing id / lamport timestamp), other metadata (?) ...
 * OR RATHER {pcu:{id,version}, http:{mime}, file:{name,path}, tika:{title,fulltext}} => like JSON-LD !?
 * right now : {"type":"file","id":"myid","properties":{"name":"a.doc"}}
 * @author mardut
 *
 */
@ApiModel(value = "A PCU data document")
//@JsonAutoDetect(fieldVisibility = Visibility.NONE) // , getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE
public class PcuDocument {
   
   /** mandatory in PCU */
   private String type;
   /** mandatory in PCU (create in client connector using TODO) */
   private String id;
   /** mandatory ?? */
   private Long version;
   // TODO or all are optional / meta ??
   private Long global_version;
   /** content as props if JSON, XML, props... */
   @JsonIgnore
   private LinkedHashMap<String,Object> properties = new LinkedHashMap<String,Object>(); // or props, fields ?
   
   // crawler case :
   /*
    * base 64 (?), mostly until processed by pipeline *
   private String raw;
   //private PcuDocumentProperties pcu; // or rather this ?
    * metas extracted by client-side providers / stages / facets ex. http (mimetype, url), tika (title, fulltext), ?content, ?alfresco
    * (TODO Q or merged, or as a single or wrapping object ?) *
   private LinkedHashMap<String,LinkedHashMap<String,Object>> metadataGroups;
   //private LinkedHashMap<String,PcuMetaPropertyGroup> metadataGroupsCache; // TODO Q also ??
   */

   public String getType() {
      return type;
   }
   public void setType(String type) {
      this.type = type;
   }
   public String getId() {
      return id;
   }
   public void setId(String id) {
      this.id = id;
   }
   public Long getVersion() {
      return version;
   }
   public void setVersion(Long version) {
      this.version = version;
   }
   public Long getGlobal_version() {
      return global_version;
   }
   public void setGlobal_version(Long global_version) {
      this.global_version = global_version;
   }
   @JsonSubTypes({ @JsonSubTypes.Type(String.class), @JsonSubTypes.Type(Boolean.class),
      @JsonSubTypes.Type(Double.class), @JsonSubTypes.Type(LocalDateTime.class),
      @JsonSubTypes.Type(Map.class), @JsonSubTypes.Type(List.class) })
   @JsonAnyGetter
   public LinkedHashMap<String,Object> getProperties() {
      return properties;
   }
   @JsonIgnore
   public void setProperties(LinkedHashMap<String,Object> properties) {
      this.properties = properties;
   }
   @JsonSubTypes({ @JsonSubTypes.Type(String.class), @JsonSubTypes.Type(Boolean.class),
      @JsonSubTypes.Type(Double.class), @JsonSubTypes.Type(LocalDateTime.class),
      @JsonSubTypes.Type(Map.class), @JsonSubTypes.Type(List.class) })
   @JsonAnySetter
   public void setProperty(String name, Object value) {
      this.properties.put(name, value);
   }
   /** get by JSON path (dotted), doesn't work with List */
   @JsonIgnore
   public Object getByPath(String path) {
      String[] pathNames = path.split("\\."); // TODO better using indexOf()
      return getMapByPath(pathNames, pathNames.length - 1).get(pathNames[pathNames.length - 1]);
   }
   /** set by JSON path (dotted), if null removes existing value, doesn't work with List */
   @JsonIgnore
   public Object setByPath(String path,  Object value) {
      return setByPath(path, value, false);
   }
   /** set by JSON path (dotted), if null removes existing value unless evenIfNull (which sets null value), doesn't work with List */
   @JsonIgnore
   public Object setByPath(String path,  Object value, boolean evenIfNull) {
      String[] pathNames = path.split("\\."); // TODO better using indexOf()
      LinkedHashMap<String, Object> map = getMapByPath(pathNames, pathNames.length - 1);
      String name = pathNames[pathNames.length - 1];
      if (!evenIfNull && value == null) {
         if (map.containsKey(name)) {
            return map.remove(name);
         } else {
            return null;
         }
      }
      return map.put(name, value);
   }
   @SuppressWarnings("unchecked")
   @JsonIgnore
   public LinkedHashMap<String,Object> getMapByPath(String[] pathNames, int depth) {
      LinkedHashMap<String, Object> map = properties;
      for (int i = 0; i < depth; i++) {
         LinkedHashMap<String, Object> submap = (LinkedHashMap<String, Object>) map.get(pathNames[i]);
         if (submap == null) {
            submap = new LinkedHashMap<String, Object>();
            map.put(pathNames[i], submap);
         }
         map = submap;
      }
      return map;
   }
   @JsonIgnore
   public LinkedHashMap<String,Object> getMapByPath(String path) {
      String[] pathNames = path.split("\\."); // TODO better using indexOf()
      LinkedHashMap<String, Object> map = getMapByPath(pathNames, pathNames.length);
      return map;
   }
   /*public String getRaw() {
      return raw;
   }
   public void setRaw(String raw) {
      this.raw = raw;
   }
   public LinkedHashMap<String,LinkedHashMap<String,Object>> getMetadataGroups() {
      return metadataGroups;
   }
   public void setMetadataGroups(LinkedHashMap<String,LinkedHashMap<String,Object>> metadataGroups) {
      this.metadataGroups = metadataGroups;
   }*/

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
   
}
