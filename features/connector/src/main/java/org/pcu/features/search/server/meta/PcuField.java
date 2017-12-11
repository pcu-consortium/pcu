package org.pcu.features.search.server.meta;

public class PcuField {

   private String name;
   /** TODO cache only */
   private String pcuType;
   private String type; // TODO ENUM
   // TODO id (rather than required)
   
   public PcuField(String name, String type) {
      this.name = name;
      this.type = type;
   }
   
   public String getName() {
      return name;
   }
   public void setName(String name) {
      this.name = name;
   }
   public String getPcuType() {
      return pcuType;
   }
   public void setPcuType(String pcuType) {
      this.pcuType = pcuType;
   }
   public String getType() {
      return type;
   }
   public void setType(String type) {
      this.type = type;
   }
   
}
