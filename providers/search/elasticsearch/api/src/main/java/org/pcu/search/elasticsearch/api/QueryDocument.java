package org.pcu.search.elasticsearch.api;

/**
 * Document wrapper provided in query (more_like_this).
 * NB. ElasticSearch does not support version.
 * @author mdutoo
 *
 */
public class QueryDocument extends EmptyDocumentResult {

   private Document doc;

   public Document getDoc() {
      return doc;
   }
   public void setDoc(Document doc) {
      this.doc = doc;
   }
   
}
