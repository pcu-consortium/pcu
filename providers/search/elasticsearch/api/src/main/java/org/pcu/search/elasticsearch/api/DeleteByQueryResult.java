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

}
