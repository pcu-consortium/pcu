package org.pcu.providers.search.spi;

import java.util.LinkedHashMap;

public class _SpiDocument {

   /** mandatory in SPI (?) */
   private String type;
   /** mandatory in SPI (?) (create using TODO) */
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
