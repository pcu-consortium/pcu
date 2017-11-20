package org.pcu.search.elasticsearch.api.query.clause;

import java.util.List;

import org.pcu.search.elasticsearch.api.QueryDocument;
import org.pcu.search.elasticsearch.api.query.ESQuery;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * NB. (for now) doesn't support mere String-typed "like" ({"fields":["firstname", "lastname"], "like":"John Doe"}),
 * rather use provided / "artificial" documents syntax :
 * like: [{ _index: hit._index, _type: hit._type, doc:{ "firstname":"John Doe", "lastname":John Doe" } }]
 * https://www.elastic.co/guide/en/elasticsearch/reference/5.5/search-more-like-this.html
 * ex. :
 * { query : { more_like_this: { like: [{ _index: hit._index, _type: hit._type, _id: hit._id }] } } }
 * 
 * NB. The fields on which to perform MLT must be indexed and of type text or keyword`.
 * Additionally, when using like with documents, either _source must be enabled (which is the default)
 * or the fields must be stored or store term_vector ("term_vector": "yes").
 * In order to speed up analysis, it could help to store term vectors at index time.
 * 
 * @author mdutoo
 *
 */
@JsonTypeInfo(include=As.WRAPPER_OBJECT, use=Id.NAME)
public class more_like_this implements ESQuery { // ESMoreLikeThisQuery
   
   /** Not required, but way faster than on all fields (default) */
   private List<String> fields = null;
   private List<QueryDocument> like = null;
   private int max_query_terms = 25;
   /** set it to ex. 1 when terms occur less than 5 times, ex. in unit test */
   private int min_term_freq = 5;
   /** set it to ex. 1 when terms occur in less than 5 docs, ex. in unit test https://stackoverflow.com/questions/40236844/elasticsearch-more-like-this-no-result */
   private int min_doc_freq = 5;
   //private int max_doc_freq = 0; // NOO problematic : makes query fail in unit tests, and absolute rather than freq https://github.com/elastic/elasticsearch/issues/14114
   private int min_word_length = 0;
   private int max_word_length = 0;
   // TODO min/max_word_length, stop_words, analyzer

   public List<String> getFields() {
      return fields;
   }
   public void setFields(List<String> fields) {
      this.fields = fields;
   }
   public List<QueryDocument> getLike() {
      return like;
   }
   public void setLike(List<QueryDocument> like) {
      this.like = like;
   }
   public int getMax_query_terms() {
      return max_query_terms;
   }
   public void setMax_query_terms(int max_query_terms) {
      this.max_query_terms = max_query_terms;
   }
   public int getMin_term_freq() {
      return min_term_freq;
   }
   public void setMin_term_freq(int min_term_freq) {
      this.min_term_freq = min_term_freq;
   }
   public int getMin_doc_freq() {
      return min_doc_freq;
   }
   public void setMin_doc_freq(int min_doc_freq) {
      this.min_doc_freq = min_doc_freq;
   }
   public int getMin_word_length() {
      return min_word_length;
   }
   public void setMin_word_length(int min_word_length) {
      this.min_word_length = min_word_length;
   }
   public int getMax_word_length() {
      return max_word_length;
   }
   public void setMax_word_length(int max_word_length) {
      this.max_word_length = max_word_length;
   }
   
}
