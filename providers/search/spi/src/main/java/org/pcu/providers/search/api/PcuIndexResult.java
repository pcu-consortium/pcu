package org.pcu.providers.search.api;

/**
 * @author mardut
 * only if sync (i.e. CRUD, not pipeline)
 */
public class PcuIndexResult extends PcuResult {
   
   private Boolean created; // not when UpdateDocument
   private Long version; // NOO only if sync (i.e. CRUD, not pipeline)
   // TODO bulk result status

   public Boolean getCreated() {
      return created;
   }
   public void setCreated(Boolean created) {
      this.created = created;
   }
   public Long getVersion() {
      return version;
   }
   public void setVersion(Long version) {
      this.version = version;
   }

}
