package org.pcu.search.elasticsearch.api;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-delete.html
 * @author mdutoo
 *
 */
public class EmptyDocumentResult {
   
   private String _index;
   private String _type;
   private String _id;
   private Long _version; // only if set ? in _search only if asked
   
   public String get_index() {
      return _index;
   }
   public void set_index(String _index) {
      this._index = _index;
   }
   public String get_type() {
      return _type;
   }
   public void set_type(String _type) {
      this._type = _type;
   }
   public String get_id() {
      return _id;
   }
   public void set_id(String _id) {
      this._id = _id;
   }
   public Long get_version() {
      return _version;
   }
   public void set_version(Long _version) {
      this._version = _version;
   }

}
