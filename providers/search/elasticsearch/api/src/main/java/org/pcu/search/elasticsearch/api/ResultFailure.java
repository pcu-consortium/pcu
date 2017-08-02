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

}
