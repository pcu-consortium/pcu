package org.pcu.search.elasticsearch.api.query.clause;

import java.util.LinkedHashMap;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-scripting-using.html
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-script-query.html
 * OBSOLETE https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-scripting-using.html
 * @author mardut
 *
 */
public class ESScriptScript {
   private String inline;
   private String file;
   /**
    * stored script
    */
   private String code;
   
   private String lang = "painless"; // groovy (& expression, mustache, also builtin), javascript, python (plugins)
   /** heartily advised, else too easy to get circuit_breaking_exception
    * [script] Too many dynamic script compilations within one minute, max: [15/min]; please use on-disk, indexed, or scripts with parameters instead; this limit can be changed by the [script.max_compilations_per_minute] setting
    */
   private LinkedHashMap<String,Object> params;
   
   public String getInline() {
      return inline;
   }
   public void setInline(String inline) {
      this.inline = inline;
   }
   public String getFile() {
      return file;
   }
   public void setFile(String file) {
      this.file = file;
   }
   public String getCode() {
      return code;
   }
   public void setCode(String code) {
      this.code = code;
   }
   public String getLang() {
      return lang;
   }
   public void setLang(String lang) {
      this.lang = lang;
   }
   public LinkedHashMap<String, Object> getParams() {
      return params;
   }
   public void setParams(LinkedHashMap<String, Object> params) {
      this.params = params;
   }

}
