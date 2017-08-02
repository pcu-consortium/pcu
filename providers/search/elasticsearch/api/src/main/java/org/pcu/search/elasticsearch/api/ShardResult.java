package org.pcu.search.elasticsearch.api;

public class ShardResult {
   private int total;
   private int failed;
   private int successful;
   
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

}
