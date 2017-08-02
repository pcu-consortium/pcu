package org.pcu.search.elasticsearch.api;

import java.util.List;

public class ESApiError extends ESApiErrorRootCause {
   
   private List<ESApiErrorRootCause> root_cause;

   public List<ESApiErrorRootCause> getRoot_cause() {
      return root_cause;
   }
   public void setRoot_cause(List<ESApiErrorRootCause> root_cause) {
      this.root_cause = root_cause;
   }

}
