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
 * @author mardut
 *
 */
public class TokenFilter {
   
   private String type; // usually : stop, lower/uppercase, phonetic (plugin), elision (l'avion), asciifolding (é)
   // stemming : stemmer (unified ; language=english/light_french...) porter_stem, shingle (token n-grams), nGram, edgeNGram, kstem, snowball, hunspell (dictionary)
   // also decimal_digit, pattern(_capture), length, trim, classic, reverse, truncate, apostrophe (turkish), flatten_graph, standard (noop) https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-tokenfilters.html
   // TODO pattern_replace(replacement), word_delimiter, word_delimiter_graph, stemmer(_override), keyword_marker/repeat, synonym_graph, compound (german...), truncate (length=10), unique (only_on_same_position=false), hunspell(locale, dictionary, dedup, longest_only), common_grams, cjk_width/bigram, delimited_payload_filter, limit, keep(_types), min_hash(hash/bucket_count, hash_set_size, with_rotation), fingerprint(separator, max_output_size),
   // TODO normalization : arabic/german/hindi/indic/sorani/persian/scandinavian/serbian
   // synonym ONLY FOR QUERY TIME
   
   // asciifolding https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-asciifolding-tokenfilter.html
   private Boolean preserve_original;
   
   // length https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-length-tokenfilter.html
   private int min = 0;
   private int max = Integer.MAX_VALUE;
   
   // nGram https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-ngram-tokenfilter.html
   private int min_gram = 1;
   private int max_gram = 2;
   // edgeNGram : side deprecated
   
   // shingle https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-shingle-tokenfilter.html
   private int max_shingle_size = 2;
   private int min_shingle_size = 2;
   private boolean output_unigrams = true;
   private boolean output_unigrams_if_no_shingles = false;
   private String token_separator = " ";
   private String filter_token = "_";
   private String stop = "_";
   
   // stop https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-stop-tokenfilter.html
   private String stopwords; // _none_ (default, to disable), _arabic_, _armenian_, _basque_, _brazilian_, _bulgarian_, _catalan_, _czech_, _danish_, _dutch_, _english_, _finnish_, _french_, _galician_, _german_, _greek_, _hindi_, _hungarian_, _indonesian_, _irish_, _italian_, _latvian_, _norwegian_, _persian_, _portuguese_, _romanian_, _russian_, _sorani_, _spanish_, _swedish_, _thai_, _turkish_
   private String[] stopword_list; // TODO also from the "stopwords" prop ; OR parsed from its JSON ??
   private String stopwords_path; // stopwords file path
   private boolean ignore_case = false;
   private boolean remove_trailing = true;

   // word_delimiter https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-word-delimiter-tokenfilter.html
   private Boolean generate_word_parts; // default true
   private Boolean generate_number_parts; // default true
   private Boolean catenate_words; // default false
   private Boolean catenate_numbers; // default false
   private Boolean catenate_all; // default false
   private Boolean split_on_case_change; // default true
   //private Boolean preserve_original; // default false
   private Boolean split_on_numerics; // default true
   private Boolean stem_english_possessive; // default true
   // TODO protected_words, type_table
   
   // TODO after word_delimiter... https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-word-delimiter-tokenfilter.html
   
   // stemmer https://www.elastic.co/guide/en/elasticsearch/reference/2.4/analysis-stemmer-tokenfilter.html
   //private String language; // english, light_french...
   
   // snowball https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-snowball-tokenfilter.html
   private String language; // Armenian, Basque, Catalan, Danish, Dutch, English, Finnish, French, German, German2, Hungarian, Italian, Kp, Lithuanian, Lovins, Norwegian, Porter, Portuguese, Romanian, Russian, Spanish, Swedish, Turkish
   
   // synonym ONLY FOR QUERY TIME https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-synonym-tokenfilter.html#analysis-synonym-tokenfilter
   private String format; // = "solr"; // wordnet (rather than default solr)
   private String synonyms_path;
   //private boolean ignore_case = false;
   private boolean expand = true;
   
   // phonetic https://www.elastic.co/guide/en/elasticsearch/plugins/5.5/analysis-phonetic-token-filter.html
   private String encoder; // metaphone (default), doublemetaphone, soundex, refinedsoundex, caverphone1, caverphone2, cologne, nysiis, koelnerphonetik, haasephonetik, beidermorse, daitch_mokotoff
   private boolean replace = true; // not supported by beidermorse
   private int max_code_len = 4; // doublemetaphone
   private String rule_type = "approx"; // beider_morse ; exact
   private String name_type = "generic" ; // beider_morse ; ashkenazi, sephardic
   private List<String> languageset; // any, comomon, cyrillic, english, french, german, hebrew, hungarian, polish, romanian, russian, spanish
   
   // elision https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-elision-tokenfilter.html
   private List<String> articles;
   
   public String getType() {
      return type;
   }
   public void setType(String type) {
      this.type = type;
   }
   public Boolean getPreserve_original() {
      return preserve_original;
   }
   public void setPreserve_original(Boolean preserve_original) {
      this.preserve_original = preserve_original;
   }
   public int getMin() {
      return min;
   }
   public void setMin(int min) {
      this.min = min;
   }
   public int getMax() {
      return max;
   }
   public void setMax(int max) {
      this.max = max;
   }
   public int getMin_gram() {
      return min_gram;
   }
   public void setMin_gram(int min_gram) {
      this.min_gram = min_gram;
   }
   public int getMax_gram() {
      return max_gram;
   }
   public void setMax_gram(int max_gram) {
      this.max_gram = max_gram;
   }
   public int getMax_shingle_size() {
      return max_shingle_size;
   }
   public void setMax_shingle_size(int max_shingle_size) {
      this.max_shingle_size = max_shingle_size;
   }
   public int getMin_shingle_size() {
      return min_shingle_size;
   }
   public void setMin_shingle_size(int min_shingle_size) {
      this.min_shingle_size = min_shingle_size;
   }
   public boolean isOutput_unigrams() {
      return output_unigrams;
   }
   public void setOutput_unigrams(boolean output_unigrams) {
      this.output_unigrams = output_unigrams;
   }
   public boolean isOutput_unigrams_if_no_shingles() {
      return output_unigrams_if_no_shingles;
   }
   public void setOutput_unigrams_if_no_shingles(boolean output_unigrams_if_no_shingles) {
      this.output_unigrams_if_no_shingles = output_unigrams_if_no_shingles;
   }
   public String getToken_separator() {
      return token_separator;
   }
   public void setToken_separator(String token_separator) {
      this.token_separator = token_separator;
   }
   public String getFilter_token() {
      return filter_token;
   }
   public void setFilter_token(String filter_token) {
      this.filter_token = filter_token;
   }
   public String getStop() {
      return stop;
   }
   public void setStop(String stop) {
      this.stop = stop;
   }
   public String getStopwords() {
      return stopwords;
   }
   public void setStopwords(String stopwords) {
      this.stopwords = stopwords;
   }
   public String[] getStopword_list() {
      return stopword_list;
   }
   public void setStopword_list(String[] stopword_list) {
      this.stopword_list = stopword_list;
   }
   public String getStopwords_path() {
      return stopwords_path;
   }
   public void setStopwords_path(String stopwords_path) {
      this.stopwords_path = stopwords_path;
   }
   public boolean isIgnore_case() {
      return ignore_case;
   }
   public void setIgnore_case(boolean ignore_case) {
      this.ignore_case = ignore_case;
   }
   public boolean isRemove_trailing() {
      return remove_trailing;
   }
   public void setRemove_trailing(boolean remove_trailing) {
      this.remove_trailing = remove_trailing;
   }
   public Boolean getGenerate_word_parts() {
      return generate_word_parts;
   }
   public void setGenerate_word_parts(Boolean generate_word_parts) {
      this.generate_word_parts = generate_word_parts;
   }
   public Boolean getGenerate_number_parts() {
      return generate_number_parts;
   }
   public void setGenerate_number_parts(Boolean generate_number_parts) {
      this.generate_number_parts = generate_number_parts;
   }
   public Boolean getCatenate_words() {
      return catenate_words;
   }
   public void setCatenate_words(Boolean catenate_words) {
      this.catenate_words = catenate_words;
   }
   public Boolean getCatenate_numbers() {
      return catenate_numbers;
   }
   public void setCatenate_numbers(Boolean catenate_numbers) {
      this.catenate_numbers = catenate_numbers;
   }
   public Boolean getCatenate_all() {
      return catenate_all;
   }
   public void setCatenate_all(Boolean catenate_all) {
      this.catenate_all = catenate_all;
   }
   public Boolean getSplit_on_case_change() {
      return split_on_case_change;
   }
   public void setSplit_on_case_change(Boolean split_on_case_change) {
      this.split_on_case_change = split_on_case_change;
   }
   public Boolean getSplit_on_numerics() {
      return split_on_numerics;
   }
   public void setSplit_on_numerics(Boolean split_on_numerics) {
      this.split_on_numerics = split_on_numerics;
   }
   public Boolean getStem_english_possessive() {
      return stem_english_possessive;
   }
   public void setStem_english_possessive(Boolean stem_english_possessive) {
      this.stem_english_possessive = stem_english_possessive;
   }
   public String getLanguage() {
      return language;
   }
   public void setLanguage(String language) {
      this.language = language;
   }
   public String getFormat() {
      return format;
   }
   public void setFormat(String format) {
      this.format = format;
   }
   public String getSynonyms_path() {
      return synonyms_path;
   }
   public void setSynonyms_path(String synonyms_path) {
      this.synonyms_path = synonyms_path;
   }
   public boolean isExpand() {
      return expand;
   }
   public void setExpand(boolean expand) {
      this.expand = expand;
   }
   public String getEncoder() {
      return encoder;
   }
   public void setEncoder(String encoder) {
      this.encoder = encoder;
   }
   public boolean isReplace() {
      return replace;
   }
   public void setReplace(boolean replace) {
      this.replace = replace;
   }
   public int getMax_code_len() {
      return max_code_len;
   }
   public void setMax_code_len(int max_code_len) {
      this.max_code_len = max_code_len;
   }
   public String getRule_type() {
      return rule_type;
   }
   public void setRule_type(String rule_type) {
      this.rule_type = rule_type;
   }
   public String getName_type() {
      return name_type;
   }
   public void setName_type(String name_type) {
      this.name_type = name_type;
   }
   public List<String> getLanguageset() {
      return languageset;
   }
   public void setLanguageset(List<String> languageset) {
      this.languageset = languageset;
   }
   public List<String> getArticles() {
      return articles;
   }
   public void setArticles(List<String> articles) {
      this.articles = articles;
   }
   
}
