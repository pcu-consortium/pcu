package org.pcu.search.elasticsearch.api.query;

import java.util.LinkedHashMap;

/**
 * Inherited HighlightParameters allow to configure defaults for all fields
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-highlighting.html
 * @author mardut
 *
 */
public class Highlight extends HighlightParameters {
   // TODO order https://stackoverflow.com/questions/32489012/return-elasticsearch-highlight-results-in-position-order
   private LinkedHashMap<String,HighlightParameters> fields; // TODO field pattern to conf // https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-all-field.html

   public LinkedHashMap<String,HighlightParameters> getFields() {
      return fields;
   }
   public void setFields(LinkedHashMap<String,HighlightParameters> fields) {
      this.fields = fields;
   }
   
}
