package org.pcu.search.elasticsearch.api;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-get.html#get-stored-fields
 * @author mdutoo
 *
 */
public class DocumentResult extends EmptyDocumentResult {

   private Document _source;

   public Document get_source() {
      return _source;
   }
   public void set_source(Document _source) {
      this._source = _source;
   }
   
}
