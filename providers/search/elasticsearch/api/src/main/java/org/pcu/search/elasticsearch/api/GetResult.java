package org.pcu.search.elasticsearch.api;

import java.util.LinkedHashMap;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-get.html#get-stored-fields
 * @author mdutoo
 *
 */
public class GetResult extends DocumentResult {
   
   private boolean found;
   private String _routing; // in GET only if asked for as param
   private LinkedHashMap<String,Object[]> fields; // rather than _source, if stored_fields
   
   public boolean isFound() {
      return found;
   }
   public void setFound(boolean found) {
      this.found = found;
   }
   public String get_routing() {
      return _routing;
   }
   public void set_routing(String _routing) {
      this._routing = _routing;
   }
   public LinkedHashMap<String, Object[]> getFields() {
      return fields;
   }
   public void setFields(LinkedHashMap<String, Object[]> fields) {
      this.fields = fields;
   }

}
