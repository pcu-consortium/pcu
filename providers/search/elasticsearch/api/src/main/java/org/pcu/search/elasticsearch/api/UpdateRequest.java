package org.pcu.search.elasticsearch.api;

import org.pcu.search.elasticsearch.api.mapping.Script;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-update.html
 * @author mdutoo
 *
 */
public class UpdateRequest {
   private Script script;
   private Document doc;
   private boolean detect_noop;
   private Document upsert;
   private boolean scripted_upsert;
   private boolean doc_as_upsert;
}
