package org.pcu.search.elasticsearch.api.mapping;

/**
 * {"acknowledged":true,"shards_acknowledged":true}
 * TODO more attributes ? or mere map ??
 * @author mdutoo
 *
 */
public class PutMappingResult extends DeleteMappingResult {

   private boolean shards_acknowledged = false;
   
   public boolean isShards_acknowledged() {
      return shards_acknowledged;
   }
   public void setShards_acknowledged(boolean shards_acknowledged) {
      this.shards_acknowledged = shards_acknowledged;
   }
   
}
