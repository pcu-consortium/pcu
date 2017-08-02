package org.pcu.search.elasticsearch.api.query;

import org.pcu.search.elasticsearch.api.DocumentResult;

public class Hit extends DocumentResult {

   private float _score;

   public float get_score() {
      return _score;
   }
   public void set_score(float _score) {
      this._score = _score;
   }
   
}
