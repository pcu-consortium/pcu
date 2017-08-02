package org.pcu.search.elasticsearch.api.mapping;

import java.util.LinkedHashMap;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-update.html
 * @author mdutoo
 *
 */
public class Script {
   /**
    * In addition to _source, the following variables are available through the ctx map: _index, _type, _id, _version, _routing, _parent, and _now (the current timestamp).
    */
   private String inline; // ex. "ctx._source.counter += params.count"
   private String lang; // enum ex. painless
   private LinkedHashMap<String,Object> params; // ex. "count":4

}
