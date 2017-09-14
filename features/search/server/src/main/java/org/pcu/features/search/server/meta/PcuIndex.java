package org.pcu.features.search.server.meta;

import java.util.HashMap;
import java.util.HashSet;


/**
 * TODO rather generic collection of field Map metas ?
 * @author mardut
 *
 */
public class PcuIndex {
   private String name;
   private HashMap<String,PcuIndexField> fields = new HashMap<String,PcuIndexField>();
   /** leaf types are enough */
   private HashSet<String> types = new HashSet<String>(); // base leaf allAllowed

   public PcuIndex(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }
   public void setName(String name) {
      this.name = name;
   }
   public HashMap<String, PcuIndexField> getFields() {
      return fields;
   }
   public void setFields(HashMap<String, PcuIndexField> fields) {
      this.fields = fields;
   }
   public HashSet<String> getTypes() {
      return types;
   }
   public void setTypes(HashSet<String> types) {
      this.types = types;
   }
   
   public PcuIndexField getField(String name) {
      return this.fields.get(name);
   }
   public void addField(PcuIndexField field) {
      this.fields.put(field.getName(), field);
      field.setIndexType(this.name);
   }
   
}
