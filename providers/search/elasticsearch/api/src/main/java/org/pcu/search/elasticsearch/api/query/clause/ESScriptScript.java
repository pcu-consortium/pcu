package org.pcu.search.elasticsearch.api.query.clause;

import java.util.LinkedHashMap;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-scripting-using.html
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
