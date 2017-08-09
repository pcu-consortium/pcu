package org.pcu.search.elasticsearch.api;

/**
 * https://github.com/elastic/elasticsearch/issues/14012#issuecomment-146655616
 * @author mdutoo
 *
 */
public class ResultFailure {
   private String index;
   private int shard;
   private int status;
   private String reason;
   
   public String getIndex() {
      return index;
   }
   public void setIndex(String index) {
      this.index = index;
   }
   public int getShard() {
      return shard;
   }
   public void setShard(int shard) {
      this.shard = shard;
   }
   public int getStatus() {
      return status;
   }
   public void setStatus(int status) {
      this.status = status;
   }
   public String getReason() {
      return reason;
   }
   public void setReason(String reason) {
      this.reason = reason;
   }

}
