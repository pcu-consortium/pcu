package org.pcu.features.configuration.api;

import org.pcu.search.elasticsearch.api.Document;
import org.pcu.search.elasticsearch.api.GetResult;

public class GetConfigurationResult extends GetResult {

   private PcuConfiguration _source;

   public PcuConfiguration get_source() {
      return _source;
   }
   public void set_source(PcuConfiguration _source) {
      this._source = _source;
   }   
   
}
