package org.pcu.features.search.api;

/**
 * @author mardut
 *
 */
public class PcuIndexResult {
   
   private Boolean created; // not when UpdateDocument
   // TODO bulk result status

   public Boolean getCreated() {
      return created;
   }
   public void setCreated(Boolean created) {
      this.created = created;
   }

}
