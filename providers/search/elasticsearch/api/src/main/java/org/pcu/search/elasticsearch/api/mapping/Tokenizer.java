package org.pcu.search.elasticsearch.api.mapping;

public class Tokenizer {
   
   private String type; // standard, keyword (noop), letter, lowercase, whitespace, pattern, path_hierarchy, uax_url_email ; (edge_)ngram ; also classic, thai https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-tokenizers.html#analysis-tokenizers

   // standard
   private int max_token_length;

   public String getType() {
      return type;
   }
   public void setType(String type) {
      this.type = type;
   }
   public int getMax_token_length() {
      return max_token_length;
   }
   public void setMax_token_length(int max_token_length) {
      this.max_token_length = max_token_length;
   }
   
   
}
