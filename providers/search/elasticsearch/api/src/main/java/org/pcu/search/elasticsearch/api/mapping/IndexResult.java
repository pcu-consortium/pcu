package org.pcu.search.elasticsearch.api.mapping;

import org.pcu.search.elasticsearch.api.EmptyDocumentResult;
import org.pcu.search.elasticsearch.api.ShardResult;

/**
 * TODO Q or also Update/DeleteResult ?
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-bulk.html
 * @author mardut
 *
 */
public class IndexResult extends EmptyDocumentResult {
   
   private ShardResult _shards;
   private Boolean created; // not when UpdateDocument
   private Boolean found; // only when delete
   private String result; // created, noop ; ???
   private int status; // only in _bulk

   public ShardResult get_shards() {
      return _shards;
   }
   public void set_shards(ShardResult _shards) {
      this._shards = _shards;
   }
   /** null if error ex. bulk 409 Conflict */
   public boolean isCreated() {
      return created != null && created;
   }
   public void setCreated(boolean created) {
      this.created = created;
   }
   public String getResult() {
      return result;
   }
   public void setResult(String result) {
      this.result = result;
   }
   public Boolean getFound() {
      return found != null && found;
   }
   public void setFound(Boolean found) {
      this.found = found;
   }
   public int getStatus() {
      return status;
   }
   public void setStatus(int status) {
      this.status = status;
   }
}
