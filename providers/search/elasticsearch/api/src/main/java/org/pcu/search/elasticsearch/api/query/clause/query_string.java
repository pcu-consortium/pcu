package org.pcu.search.elasticsearch.api.query.clause;

import org.pcu.search.elasticsearch.api.query.ESQuery;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * query using native Lucene syntax
 * http://lucene.apache.org/core/3_5_0/queryparsersyntax.html
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-query-string-query.html
 * 
 * TODO more params ? analyzer, analyze_wildcard, fuzziness, fuzzy_max_expansions, fuzzy_prefix_length...
 * 
 * @author mdutoo
 *
 */
@JsonTypeInfo(include=As.WRAPPER_OBJECT, use=Id.NAME)
public class query_string implements ESQuery { // EsQueryStringQuery
   
   private String query;

   public String getQuery() {
      return query;
   }

   public void setQuery(String query) {
      this.query = query;
   }
   
}
