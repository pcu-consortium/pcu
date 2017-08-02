package org.pcu.search.elasticsearch.api.mapping;

import java.util.List;

public class CharFilter {

   private String type; // html_strip, mapping, pattern_filter https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-charfilters.html
   
   // html_strip https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-htmlstrip-charfilter.html
   private List<String> escaped_tags;
   
   // mapping, ex. for emoticons https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-mapping-charfilter.html
   private List<String> mappings; // "key => value" mappings
   private String mappings_path; // UTF8 key => value mapping file path

   // pattern
   private String pattern; // Java regex
   private String replacement; // can reference $i capture groups
   private String flags; // |-separated Java regex flags
   
   public String getType() {
      return type;
   }
   public void setType(String type) {
      this.type = type;
   }
   public List<String> getEscaped_tags() {
      return escaped_tags;
   }
   public void setEscaped_tags(List<String> escaped_tags) {
      this.escaped_tags = escaped_tags;
   }
   public List<String> getMappings() {
      return mappings;
   }
   public void setMappings(List<String> mappings) {
      this.mappings = mappings;
   }
   public String getMappings_path() {
      return mappings_path;
   }
   public void setMappings_path(String mappings_path) {
      this.mappings_path = mappings_path;
   }
   public String getPattern() {
      return pattern;
   }
   public void setPattern(String pattern) {
      this.pattern = pattern;
   }
   public String getReplacement() {
      return replacement;
   }
   public void setReplacement(String replacement) {
      this.replacement = replacement;
   }
   public String getFlags() {
      return flags;
   }
   public void setFlags(String flags) {
      this.flags = flags;
   }
   
}
