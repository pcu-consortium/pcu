package org.pcu.search.elasticsearch.api.mapping;

import java.util.List;

/**
 * TODO a separate class per type ?! => WHEN REFACTORING TO UNIFIED SPI
 * TODO enums ?
 * 
 * Q primitive or object params ? object allow null, while primitive require a default value.
 * Q inited to default value ? clearer, but most of the time not possible
 * because param is not supported in a given type or defaults differ according to type
 * => TODO migrate primitive to objects (and find another way to record defaults)
 * 
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/analyzer.html
 * @author mdutoo
 *
 */
public class Analyzer {
   
   private String type; // custom, standard, whitespace, language, pattern ; also simple, stop, fingerprint https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-analyzers.html
   // and language analyzers : arabic, armenian, basque, brazilian, bulgarian, catalan, cjk, czech, danish, dutch, english, finnish, french, galician, german, greek, hindi, hungarian, indonesian, irish, italian, latvian, lithuanian, norwegian, persian, portuguese, romanian, russian, sorani, spanish, swedish, turkish, thai https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-lang-analyzer.html
   private String tokenizer; // standard, keyword (noop), letter, lowercase, whitespace, pattern, path_hierarchy, uax_url_email ; (edge_)ngram ; also classic, thai https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-tokenizers.html#analysis-tokenizers
   private List<String> filter; // token filters : lowercase, english_stop, asciifolding
   private List<String> char_filter; // html_strip, mapping, pattern_filter https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-charfilters.html
   
   // specific analyzer conf :
   // NB. superfluous, can be replaced by creating custom analyzers using conf'd
   // components (tokenizer & (char_)filter)
   
   // standard
   private Integer max_token_length;
   private String stopwords; // _none_ (default), _english_
   
   // pattern
   private String pattern; // Java regex
   private String flags; // |-separated Java regex flags
   private Boolean lowercase;
   // private String stopwords;
   private String stopwords_path; // stopwords file path
   
   // language
   //private String stopwords;
   //private String stopwords_path;
   private String[] stem_exclusion; // lowercase words that should not be stemmed
   
   public String getType() {
      return type;
   }
   public void setType(String type) {
      this.type = type;
   }
   public String getTokenizer() {
      return tokenizer;
   }
   public void setTokenizer(String tokenizer) {
      this.tokenizer = tokenizer;
   }
   public List<String> getFilter() {
      return filter;
   }
   public void setFilter(List<String> filter) {
      this.filter = filter;
   }
   public List<String> getChar_filter() {
      return char_filter;
   }
   public void setChar_filter(List<String> char_filter) {
      this.char_filter = char_filter;
   }
   public Integer getMax_token_length() {
      return max_token_length;
   }
   public void setMax_token_length(Integer max_token_length) {
      this.max_token_length = max_token_length;
   }
   public String getStopwords() {
      return stopwords;
   }
   public void setStopwords(String stopwords) {
      this.stopwords = stopwords;
   }
   public String getPattern() {
      return pattern;
   }
   public void setPattern(String pattern) {
      this.pattern = pattern;
   }
   public String getFlags() {
      return flags;
   }
   public void setFlags(String flags) {
      this.flags = flags;
   }
   public Boolean getLowercase() {
      return lowercase;
   }
   public void setLowercase(Boolean lowercase) {
      this.lowercase = lowercase;
   }
   public String getStopwords_path() {
      return stopwords_path;
   }
   public void setStopwords_path(String stopwords_path) {
      this.stopwords_path = stopwords_path;
   }
   public String[] getStem_exclusion() {
      return stem_exclusion;
   }
   public void setStem_exclusion(String[] stem_exclusion) {
      this.stem_exclusion = stem_exclusion;
   }

}
