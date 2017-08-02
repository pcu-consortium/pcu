package org.pcu.search.elasticsearch.api.mapping;

import java.util.LinkedHashMap;

public class TypeMapping {
   
   private LinkedHashMap<String, PropertyMapping> properties;

   public LinkedHashMap<String, PropertyMapping> getProperties() {
      return properties;
   }
   public void setProperties(LinkedHashMap<String, PropertyMapping> properties) {
      this.properties = properties;
   }

}
