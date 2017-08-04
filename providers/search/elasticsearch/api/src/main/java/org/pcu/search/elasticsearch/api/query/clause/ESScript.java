package org.pcu.search.elasticsearch.api.query.clause;


/**
 * required outside script query :
 * - script_fields https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-script-fields.html
 * - 
 * @author mardut
 *
 */
public class ESScript {
   
   private ESScriptScript script = new ESScriptScript();

   public ESScriptScript getScript() {
      return script;
   }
   public void setScript(ESScriptScript script) {
      this.script = script;
   }
   
}
