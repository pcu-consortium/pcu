package org.pcu.search.elasticsearch.api;

/**
 * TODO use it also as IndexMessage instead of @QueryParams ??
 * @author mardut
 *
 */
public class IndexAction {
   private String _index;
   private String _type;
   private String _id;
   private Long _version;
   private String _routing;
   private String _parent;
   private String _retry_on_conflict;
   
   // TODO update : doc (partial document), upsert, doc_as_upsert, script and _source
   
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
   public void set_version(Long long1) {
      this._version = long1;
   }
   public String get_routing() {
      return _routing;
   }
   public void set_routing(String _routing) {
      this._routing = _routing;
   }
   public String get_parent() {
      return _parent;
   }
   public void set_parent(String _parent) {
      this._parent = _parent;
   }
   public String get_retry_on_conflict() {
      return _retry_on_conflict;
   }
   public void set_retry_on_conflict(String _retry_on_conflict) {
      this._retry_on_conflict = _retry_on_conflict;
   }

}
