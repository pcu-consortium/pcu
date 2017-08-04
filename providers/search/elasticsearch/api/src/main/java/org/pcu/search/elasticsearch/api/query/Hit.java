package org.pcu.search.elasticsearch.api.query;

import java.util.LinkedHashMap;
import java.util.List;

import org.pcu.search.elasticsearch.api.DocumentResult;

public class Hit extends DocumentResult {

   private float _score;
   
   private String _shard; // when explain=true ; or in (Empty)DocumentResult ??
   private String _node; // when explain=true ; or in (Empty)DocumentResult ??
   /** when explain=true https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-explain.html */
   private Object _explanation; // {"value":2.6986625,"description":"weight(spanOr([spanNear([content.content:i, content.content:phone], 0, true), content.content:i phone]) in 0) [PerFieldSimilarity], result of:","details":[{"value":2.6986625,"description":"score(doc=0,freq=2.0 = phraseFreq=2.0\n), product of:","details":[{"value":2.0,"description":"boost","details":[]},{"value":0.8630463,"description":"idf(), sum of:","details":[{"value":0.2876821,"description":"idf, computed as log(1 + (docCount - docFreq + 0.5) / (docFreq + 0.5)) from:","details":[{"value":1.0,"description":"docFreq","details":[]},{"value":1.0,"description":"docCount","details":[]}]},{"value":0.2876821,"description":"idf, computed as log(1 + (docCount - docFreq + 0.5) / (docFreq + 0.5)) from:","details":[{"value":1.0,"description":"docFreq","details":[]},{"value":1.0,"description":"docCount","details":[]}]},{"value":0.2876821,"description":"idf, computed as log(1 + (docCount - docFreq + 0.5) / (docFreq + 0.5)) from:","details":[{"value":1.0,"description":"docFreq","details":[]},{"value":1.0,"description":"docCount","details":[]}]}]},{"value":1.5634518,"description":"tfNorm, computed as (freq * (k1 + 1)) / (freq + k1 * (1 - b + b * fieldLength / avgFieldLength)) from:","details":[{"value":2.0,"description":"phraseFreq=2.0","details":[]},{"value":1.2,"description":"parameter k1","details":[]},{"value":0.75,"description":"parameter b","details":[]},{"value":28.0,"description":"avgFieldLength","details":[]},{"value":16.0,"description":"fieldLength","details":[]}]}]}]}}]}}
   /** when highlight */
   private LinkedHashMap<String,List<String>> highlight;
   /** when script_fields */
   private LinkedHashMap<String,Object> fields;

   public float get_score() {
      return _score;
   }
   public void set_score(float _score) {
      this._score = _score;
   }
   public String get_shard() {
      return _shard;
   }
   public void set_shard(String _shard) {
      this._shard = _shard;
   }
   public String get_node() {
      return _node;
   }
   public void set_node(String _node) {
      this._node = _node;
   }
   public Object get_explanation() {
      return _explanation;
   }
   public void set_explanation(Object _explanation) {
      this._explanation = _explanation;
   }
   public LinkedHashMap<String,List<String>> getHighlight() {
      return highlight;
   }
   public void setHighlight(LinkedHashMap<String,List<String>> highlight) {
      this.highlight = highlight;
   }
   public LinkedHashMap<String,Object> getFields() {
      return fields;
   }
   public void setFields(LinkedHashMap<String,Object> fields) {
      this.fields = fields;
   }
   
}
