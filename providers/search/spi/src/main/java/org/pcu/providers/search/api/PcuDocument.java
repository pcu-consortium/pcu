package org.pcu.providers.search.api;

import java.util.LinkedHashMap;

/**
 * TODO better : pcu id, version (and / or global increasing id / lamport timestamp), other metadata (?) ...
 * OR RATHER {pcu:{id,version}, http:{mime}, file:{name,path}, tika:{title,fulltext}} => like JSON-LD !?
 * right now : {"type":"file","id":"myid","properties":{"name":"a.doc"}}
 * @author mardut
 *
 */
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
   private LinkedHashMap<String,Object> properties; // or props, fields ?
   
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
   public LinkedHashMap<String,Object> getProperties() {
      return properties;
   }
   public void setProperties(LinkedHashMap<String,Object> properties) {
      this.properties = properties;
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

}
