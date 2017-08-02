package org.pcu.search.elasticsearch.api.mapping;

import java.util.LinkedHashMap;

public class IndexMapping {

   private IndexSettings settings;
   private LinkedHashMap<String, TypeMapping> mappings;

   public IndexSettings getSettings() {
      return settings;
   }
   public void setSettings(IndexSettings settings) {
      this.settings = settings;
   }
   public LinkedHashMap<String, TypeMapping> getMappings() {
      return mappings;
   }
   public void setMappings(LinkedHashMap<String, TypeMapping> mappings) {
      this.mappings = mappings;
   }
   
}
