package org.pcu.search.elasticsearch.api.mapping;

/**
 * {"acknowledged":true}
 * TODO more attributes ? or mere map ??
 * @author mdutoo
 *
 */
public class DeleteMappingResult {

   private boolean acknowledged = false;
   
   public boolean isAcknowledged() {
      return acknowledged;
   }
   public void setAcknowledged(boolean acknowledged) {
      this.acknowledged = acknowledged;
   }
   
}
