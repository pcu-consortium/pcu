package org.pcu.search.elasticsearch.api;

import java.util.LinkedHashMap;
import java.util.List;

import org.pcu.search.elasticsearch.api.mapping.IndexResult;

/**
 * {"took":14,"errors":false,"items":[{"index":{"_index":"files","_type":"file","_id":"AV29mXuNUcOH3LaOK3tq","_version":1,"result":"created","_shards":{"total":2,"successful":1,"failed":0},"created":true,"status":201}},{"index":{"_index":"files","_type":"file","_id":"AV29mXuNUcOH3LaOK3tr","_version":1,"result":"created","_shards":{"total":2,"successful":1,"failed":0},"created":true,"status":201}}]}
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-bulk.html
 * @author mardut
 *
 */
public class BulkResult {

   private int took; // server-side processing time
   private boolean errors = false;
   private List<LinkedHashMap<String,IndexResult>> items;
   
   public int getTook() {
      return took;
   }
   public void setTook(int took) {
      this.took = took;
   }
   public boolean isErrors() {
      return errors;
   }
   public void setErrors(boolean errors) {
      this.errors = errors;
   }
   public List<LinkedHashMap<String, IndexResult>> getItems() {
      return items;
   }
   public void setItems(List<LinkedHashMap<String, IndexResult>> items) {
      this.items = items;
   }

}
