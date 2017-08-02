package org.pcu.search.elasticsearch.api.query.clause;

import org.pcu.search.elasticsearch.api.query.ESQuery;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * not analyzed
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-script-query.html
 * @author mdutoo
 *
 */
@JsonTypeInfo(include=As.WRAPPER_OBJECT, use=Id.NAME)
public class script extends ESQuery { // ESTermsQuery
   
   private ESScriptScript script;

   public ESScriptScript getScript() {
      return script;
   }
   public void setScript(ESScriptScript script) {
      this.script = script;
   }
   
}
