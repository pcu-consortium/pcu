package org.pcu.search.elasticsearch.api;

/**
 * {"took":11,"timed_out":false,"_shards":{"total":10,"successful":9,"failed":1,"failures":[{"shard":2,"index":"files","node":"VhRFe_2wTDixrDmkQJke3Q","reason":{"type":"script_exception","reason":"runtime error","script_stack":["org.elasticsearch.search.lookup.LeafDocLookup.get(LeafDocLookup.java:77)","org.elasticsearch.search.lookup.LeafDocLookup.get(LeafDocLookup.java:36)","doc['my_field'].value * params.multiplier","    ^---- HERE"],"script":"doc['my_field'].value * params.multiplier","lang":"painless","caused_by":{"type":"illegal_argument_exception","reason":"No field found for [my_field] in mapping with types []"}}}]},"hits":{"total":0,"max_score":null,"hits":[]}}
 * @author mardut
 *
 */
public class ShardFailure {
   
   private int shard;
   private String index;
   private String node;
   /** {"type":"script_exception","reason":"runtime error","script_stack":["org.elasticsearch.search.lookup.LeafDocLookup.get(LeafDocLookup.java:77)","org.elasticsearch.search.lookup.LeafDocLookup.get(LeafDocLookup.java:36)","doc['my_field'].value * params.multiplier","    ^---- HERE"],"script":"doc['my_field'].value * params.multiplier","lang":"painless","caused_by":{"type":"illegal_argument_exception","reason":"No field found for [my_field] in mapping with types []"}} */
   private ESApiError reason;
   
   public int getShard() {
      return shard;
   }
   public void setShard(int shard) {
      this.shard = shard;
   }
   public String getIndex() {
      return index;
   }
   public void setIndex(String index) {
      this.index = index;
   }
   public String getNode() {
      return node;
   }
   public void setNode(String node) {
      this.node = node;
   }
   public ESApiError getReason() {
      return reason;
   }
   public void setReason(ESApiError reason) {
      this.reason = reason;
   }

}
