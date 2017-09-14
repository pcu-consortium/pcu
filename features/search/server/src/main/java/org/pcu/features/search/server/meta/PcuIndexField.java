package org.pcu.features.search.server.meta;

import java.util.HashMap;

public class PcuIndexField {
   private String name;
   /** TODO cache only */
   private String indexType;
   private HashMap<String,Object> conf = new HashMap<String,Object>();

   public PcuIndexField(String name) {
      this.name = name;
   }
   public PcuIndexField(PcuField nameField) {
      this.name = nameField.getName();
   }

   public String getName() {
      return name;
   }
   public void setName(String name) {
      this.name = name;
   }
   public HashMap<String, Object> getConf() {
      return conf;
   }
   public void setConf(HashMap<String, Object> conf) {
      this.conf = conf;
   }
   public String getIndexType() {
      return indexType;
   }
   public void setIndexType(String indexType) {
      this.indexType = indexType;
   }
   
}
