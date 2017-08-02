package org.pcu.search.elasticsearch.api.mapping;

public class Tokenizer {
   
   private String type; // standard, keyword (noop), letter, lowercase, whitespace, pattern, path_hierarchy, uax_url_email ; (edge_)ngram ; also classic, thai https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-tokenizers.html#analysis-tokenizers

   // standard
   private int max_token_length;
}
