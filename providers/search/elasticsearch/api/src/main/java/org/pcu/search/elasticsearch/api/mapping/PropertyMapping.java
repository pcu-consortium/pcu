package org.pcu.search.elasticsearch.api.mapping;

import java.util.LinkedHashMap;
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
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-params.html
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-put-mapping.html
 * https://github.com/elastic/elasticsearch/tree/master/core/src/main/java/org/elasticsearch/index/mapper
 * @author mdutoo
 *
 */
public class PropertyMapping extends TypeMapping { // for object/nested type
   
   private String type; // text, keyword ; long, integer, short, byte, double, float, half_float, scaled_float ; date, boolean, binary
   // integer_range, float_range, long_range, double_range, date_range ; object, nested ; geo_point/shape
   // ip, completion, token_count, murmur3, attachment, percolator (Query DSL)
   // TODO multi, array
   private String format; // ||-separated formats ; date : strict_date_optional_time, epoch_millis https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping.html
   private String analyzer; // standard, whitespace, keyword (noop) ; also language, pattern, fingerprint, simple, stop (simple + stopwords) https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-analyzers.html
   private String search_analyzer;
   private String search_quote_analyzer;
   private String normalizer;
   
   // not supported : boost (use at query time instead)
   private String coerce;
   private List<String> copy_to;
   private String doc_values;
   private String dynamic;
   private Boolean enabled; // to disable _all (only, else Mapping definition ... unsupported parameters) https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-all-field.html
   private Boolean fielddata; // else error on script_fields, aggregations, sorting : Fielddata is disabled on text fields by default. https://www.elastic.co/guide/en/elasticsearch/reference/master/fielddata.html
   private Integer ignore_above; // https://www.elastic.co/guide/en/elasticsearch/reference/current/ignore-above.html
   private Boolean ignore_malformed; // defaults to false, BUT not allowed on text (can't be malformed) https://www.elastic.co/guide/en/elasticsearch/reference/current/ignore-malformed.html
   private Boolean include_in_all; // defaults to true, unless index is set to no https://www.elastic.co/guide/en/elasticsearch/reference/current/include-in-all.html
   private String index_options; // TODO docs freqs positions offsets https://www.elastic.co/guide/en/elasticsearch/reference/current/index-options.html
   private Boolean index; // defaults to true, but not supported by nested/object https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-index.html
   private LinkedHashMap<String,PropertyMapping> fields; // multi-field https://www.elastic.co/guide/en/elasticsearch/reference/current/multi-fields.html
   private Boolean norms; // caches but takes disk size ; default to false but not supported by ex. long https://www.elastic.co/guide/en/elasticsearch/reference/current/norms.html
   private String null_value; // ex. NULL, to search null values https://www.elastic.co/guide/en/elasticsearch/reference/current/null-value.html
   private Integer position_increment_gap; // defaults to 100, but not supported by all types https://www.elastic.co/guide/en/elasticsearch/reference/current/position-increment-gap.html
   private String similarity; // classic (default), BM25, boolean, TODO duke's https://www.elastic.co/guide/en/elasticsearch/reference/current/similarity.html
   private Boolean store; // for GET?stored_fields ; not for object/nested, default true https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-get.html
   private String term_vector; // no, yes, with_positions, with_offsets, with_positions_offsets
   
   public String getType() {
      return type;
   }
   public void setType(String type) {
      this.type = type;
   }
   public String getFormat() {
      return format;
   }
   public void setFormat(String format) {
      this.format = format;
   }
   public String getAnalyzer() {
      return analyzer;
   }
   public void setAnalyzer(String analyzer) {
      this.analyzer = analyzer;
   }
   public String getSearch_analyzer() {
      return search_analyzer;
   }
   public void setSearch_analyzer(String search_analyzer) {
      this.search_analyzer = search_analyzer;
   }
   public String getSearch_quote_analyzer() {
      return search_quote_analyzer;
   }
   public void setSearch_quote_analyzer(String search_quote_analyzer) {
      this.search_quote_analyzer = search_quote_analyzer;
   }
   public String getNormalizer() {
      return normalizer;
   }
   public void setNormalizer(String normalizer) {
      this.normalizer = normalizer;
   }
   public String getCoerce() {
      return coerce;
   }
   public void setCoerce(String coerce) {
      this.coerce = coerce;
   }
   public List<String> getCopy_to() {
      return copy_to;
   }
   public void setCopy_to(List<String> copy_to) {
      this.copy_to = copy_to;
   }
   public String getDoc_values() {
      return doc_values;
   }
   public void setDoc_values(String doc_values) {
      this.doc_values = doc_values;
   }
   public String getDynamic() {
      return dynamic;
   }
   public void setDynamic(String dynamic) {
      this.dynamic = dynamic;
   }
   public Boolean isEnabled() {
      return enabled;
   }
   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
   public Boolean getFielddata() {
      return fielddata;
   }
   public void setFielddata(Boolean fielddata) {
      this.fielddata = fielddata;
   }
   public Integer getIgnore_above() {
      return ignore_above;
   }
   public void setIgnore_above(Integer ignore_above) {
      this.ignore_above = ignore_above;
   }
   public Boolean getIgnore_malformed() {
      return ignore_malformed;
   }
   public void setIgnore_malformed(Boolean ignore_malformed) {
      this.ignore_malformed = ignore_malformed;
   }
   public Boolean getInclude_in_all() {
      return include_in_all;
   }
   public void setInclude_in_all(Boolean include_in_all) {
      this.include_in_all = include_in_all;
   }
   public String getIndex_options() {
      return index_options;
   }
   public void setIndex_options(String index_options) {
      this.index_options = index_options;
   }
   public Boolean getIndex() {
      return index;
   }
   public void setIndex(Boolean index) {
      this.index = index;
   }
   public LinkedHashMap<String,PropertyMapping> getFields() {
      return fields;
   }
   public void setFields(LinkedHashMap<String,PropertyMapping> fields) {
      this.fields = fields;
   }
   public Boolean getNorms() {
      return norms;
   }
   public void setNorms(Boolean norms) {
      this.norms = norms;
   }
   public String getNull_value() {
      return null_value;
   }
   public void setNull_value(String null_value) {
      this.null_value = null_value;
   }
   public Integer getPosition_increment_gap() {
      return position_increment_gap;
   }
   public void setPosition_increment_gap(Integer position_increment_gap) {
      this.position_increment_gap = position_increment_gap;
   }
   public String getSimilarity() {
      return similarity;
   }
   public void setSimilarity(String similarity) {
      this.similarity = similarity;
   }
   public Boolean isStore() {
      return store;
   }
   public void setStore(Boolean store) {
      this.store = store;
   }
   public String getTerm_vector() {
      return term_vector;
   }
   public void setTerm_vector(String term_vector) {
      this.term_vector = term_vector;
   }
   
}
