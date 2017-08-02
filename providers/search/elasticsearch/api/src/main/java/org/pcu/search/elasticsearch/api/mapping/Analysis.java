package org.pcu.search.elasticsearch.api.mapping;

import java.util.LinkedHashMap;

public class Analysis {
   
   private LinkedHashMap<String, Analyzer> analyzer;
   /** same as analyzer, except only for keyword-typed fields and should only return a single token */
   private LinkedHashMap<String, Analyzer> normalizer; // https://www.elastic.co/guide/en/elasticsearch/reference/current/normalizer.html
   
   private LinkedHashMap<String, Tokenizer> tokenizer;
   private LinkedHashMap<String, CharFilter> char_filter;
   private LinkedHashMap<String, TokenFilter> filter;
   
   public LinkedHashMap<String, Analyzer> getAnalyzer() {
      return analyzer;
   }
   public void setAnalyzer(LinkedHashMap<String, Analyzer> analyzer) {
      this.analyzer = analyzer;
   }
   public LinkedHashMap<String, Analyzer> getNormalizer() {
      return normalizer;
   }
   public void setNormalizer(LinkedHashMap<String, Analyzer> normalizer) {
      this.normalizer = normalizer;
   }
   public LinkedHashMap<String, Tokenizer> getTokenizer() {
      return tokenizer;
   }
   public void setTokenizer(LinkedHashMap<String, Tokenizer> tokenizer) {
      this.tokenizer = tokenizer;
   }
   public LinkedHashMap<String, CharFilter> getChar_filter() {
      return char_filter;
   }
   public void setChar_filter(LinkedHashMap<String, CharFilter> char_filter) {
      this.char_filter = char_filter;
   }
   public LinkedHashMap<String, TokenFilter> getFilter() {
      return filter;
   }
   public void setFilter(LinkedHashMap<String, TokenFilter> filter) {
      this.filter = filter;
   }

}
