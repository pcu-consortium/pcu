package org.pcu.features.search.server.meta;

import java.util.LinkedHashMap;

/**
 * TODO rather (with ?) avro ?
 * @author mardut
 *
 */
public class PcuType {
   
   private String name;
   /** order meaningful for overrides (if any) */
   private LinkedHashMap<String,PcuField> fields = new LinkedHashMap<String,PcuField>();
   
   public PcuType(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }
   public void setName(String name) {
      this.name = name;
   }
   public LinkedHashMap<String, PcuField> getFields() {
      return fields;
   }
   public void setFields(LinkedHashMap<String, PcuField> fields) {
      this.fields = fields;
   }
   
   public PcuField getField(String name) {
      return this.fields.get(name);
   }
   public void addField(PcuField field) {
      this.fields.put(field.getName(), field);
      field.setPcuType(this.name);
   }

}
