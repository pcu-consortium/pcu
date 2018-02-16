package org.pcu.search.elasticsearch.api;

import java.util.LinkedHashMap;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/5.5/docs-bulk.html
 * @author mardut
 *
 */
public class BulkAction {

   /** only 1 entry, of key index/create/update/delete */
   private LinkedHashMap<String,IndexAction> kindToAction = new LinkedHashMap<String,IndexAction>(1); // TODO or several wrapped classes ?
   private Document doc;
   
   public LinkedHashMap<String, IndexAction> getKindToAction() {
      return kindToAction;
   }
   /** for deser only */
   public void setKindToAction(LinkedHashMap<String, IndexAction> kindToAction) {
      this.kindToAction = kindToAction;
   }
   public Document getDoc() {
      return doc;
   }
   public void setDoc(Document doc) {
      this.doc = doc;
   }
   
}
