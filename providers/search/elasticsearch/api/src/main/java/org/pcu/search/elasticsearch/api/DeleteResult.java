package org.pcu.search.elasticsearch.api;

import java.util.LinkedHashMap;

public class DeleteResult extends EmptyDocumentResult {
   
   private LinkedHashMap<String,ShardResult> _shards;
   private boolean found;
   private String result; // deleted

}
