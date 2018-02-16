package org.pcu.features.configuration.api;

import org.pcu.search.elasticsearch.api.EmptyDocumentResult;

/**
 * Nothing more required from GetResult
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-get.html#get-stored-fields
 * @author mdutoo
 *
 */
public class PcuConnectorResult extends EmptyDocumentResult {

   private PcuConfiguration _source;

   public PcuConfiguration get_source() {
      return _source;
   }
   public void set_source(PcuConfiguration _source) {
      this._source = _source;
   }
   
}