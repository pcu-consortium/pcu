package org.pcu.search.elasticsearch.api;

import java.util.List;

public class ShardResult {
   private int total;
   private int failed;
   private int successful;
   private List<ShardFailure> failures;
   
   public int getTotal() {
      return total;
   }
   public void setTotal(int total) {
      this.total = total;
   }
   public int getFailed() {
      return failed;
   }
   public void setFailed(int failed) {
      this.failed = failed;
   }
   public int getSuccessful() {
      return successful;
   }
   public void setSuccessful(int successful) {
      this.successful = successful;
   }
   public List<ShardFailure> getFailures() {
      return failures;
   }
   public void setFailures(List<ShardFailure> failures) {
      this.failures = failures;
   }

}
