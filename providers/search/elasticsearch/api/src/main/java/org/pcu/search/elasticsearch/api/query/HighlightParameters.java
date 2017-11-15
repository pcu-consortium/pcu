package org.pcu.search.elasticsearch.api.query;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-highlighting.html
 * @author mardut
 *
 */
public class HighlightParameters {
   // TODO type : not supported because default is unified (best), else fvh, postings, plain (Lucene's). If term_vector=with_positions_offsets (but bigger index), the fast vector highlighter will be used instead by default.
   
   /** ex. to highlight against rescore query */
   private ESQuery highlight_query;
   
   private Integer number_of_fragments; // ex. 3
   private Integer fragment_size; // ex. 150
   // TODO Q fragmenter=span (default), simple ? force_source ?
   
   // TODO Q require_field_match=true ?
   // TODO Q matched_fields ?
   // TODO Q boundary_scanner=word/sentence rather than char ?
   // TODO Q no_match_size ? phrase_limit ? max_fragment_length ?
   // probably not pre/post_tags, tags_schema, , encoder
   
   public ESQuery getHighlight_query() {
      return highlight_query;
   }
   public void setHighlight_query(ESQuery highlight_query) {
      this.highlight_query = highlight_query;
   }
   public Integer getNumber_of_fragments() {
      return number_of_fragments;
   }
   public void setNumber_of_fragments(Integer number_of_fragments) {
      this.number_of_fragments = number_of_fragments;
   }
   public Integer getFragment_size() {
      return fragment_size;
   }
   public void setFragment_size(Integer fragment_size) {
      this.fragment_size = fragment_size;
   }
   
}
