package org.pcu.search.elasticsearch.api;

import java.util.List;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/common-options.ht
 * https://discuss.elastic.co/t/elasticsearch-index-issues/73871
 * @author mardut
 *
 */
public class ESApiError extends ESApiErrorCause {
   
   private List<ESApiErrorCause> root_cause;
   private ESApiErrorCause caused_by;

   public List<ESApiErrorCause> getRoot_cause() {
      return root_cause;
   }
   public void setRoot_cause(List<ESApiErrorCause> root_cause) {
      this.root_cause = root_cause;
   }
   public ESApiErrorCause getCaused_by() {
      return caused_by;
   }
   public void setCaused_by(ESApiErrorCause caused_by) {
      this.caused_by = caused_by;
   }

}
