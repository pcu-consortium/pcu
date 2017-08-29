package org.pcu.features.search.api;

import java.util.LinkedHashMap;

/**
 * TODO better : pcu id, version (and / or global increasing id / lamport timestamp), other metadata (?) ...
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
   private String version;
   // TODO other metas ? as another map or wrapping object ?!
   private LinkedHashMap<String,Object> properties;

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
   public String getVersion() {
      return version;
   }
   public void setVersion(String version) {
      this.version = version;
   }
   public LinkedHashMap<String,Object> getProperties() {
      return properties;
   }
   public void setProperties(LinkedHashMap<String,Object> properties) {
      this.properties = properties;
   }

}
