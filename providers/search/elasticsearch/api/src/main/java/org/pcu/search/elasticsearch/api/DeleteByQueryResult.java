package org.pcu.search.elasticsearch.api;

import java.util.List;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-delete-by-query.html
 * @author mdutoo
 *
 */
public class DeleteByQueryResult {
   
   private int took; // server-side processing time
   private boolean timed_out;
   private long deleted;
   private int batches; // default 1000
   private long version_conflicts;
   private long noops;
   private RetriesResult retries;
   private int throttled_millis;
   private float requests_per_second; // -1.0
   private int throttled_until_millis;
   private long total;
   private List<ResultFailure> failures;
   
   public int getTook() {
      return took;
   }
   public void setTook(int took) {
      this.took = took;
   }
   public boolean isTimed_out() {
      return timed_out;
   }
   public void setTimed_out(boolean timed_out) {
      this.timed_out = timed_out;
   }
   public long getDeleted() {
      return deleted;
   }
   public void setDeleted(long deleted) {
      this.deleted = deleted;
   }
   public int getBatches() {
      return batches;
   }
   public void setBatches(int batches) {
      this.batches = batches;
   }
   public long getVersion_conflicts() {
      return version_conflicts;
   }
   public void setVersion_conflicts(long version_conflicts) {
      this.version_conflicts = version_conflicts;
   }
   public long getNoops() {
      return noops;
   }
   public void setNoops(long noops) {
      this.noops = noops;
   }
   public RetriesResult getRetries() {
      return retries;
   }
   public void setRetries(RetriesResult retries) {
      this.retries = retries;
   }
   public int getThrottled_millis() {
      return throttled_millis;
   }
   public void setThrottled_millis(int throttled_millis) {
      this.throttled_millis = throttled_millis;
   }
   public float getRequests_per_second() {
      return requests_per_second;
   }
   public void setRequests_per_second(float requests_per_second) {
      this.requests_per_second = requests_per_second;
   }
   public int getThrottled_until_millis() {
      return throttled_until_millis;
   }
   public void setThrottled_until_millis(int throttled_until_millis) {
      this.throttled_until_millis = throttled_until_millis;
   }
   public long getTotal() {
      return total;
   }
   public void setTotal(long total) {
      this.total = total;
   }
   public List<ResultFailure> getFailures() {
      return failures;
   }
   public void setFailures(List<ResultFailure> failures) {
      this.failures = failures;
   }
   
}
