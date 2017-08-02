package org.pcu.search.elasticsearch.api.mapping;

import org.pcu.search.elasticsearch.api.EmptyDocumentResult;
import org.pcu.search.elasticsearch.api.ShardResult;

public class IndexResult extends EmptyDocumentResult {
   
   private ShardResult _shards;
   private boolean created; // not when UpdateDocument
   private String result; // created, noop ; ???

   public ShardResult get_shards() {
      return _shards;
   }
   public void set_shards(ShardResult _shards) {
      this._shards = _shards;
   }
   public boolean isCreated() {
      return created;
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
}
