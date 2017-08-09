package org.pcu.search.elasticsearch.api;

import java.util.LinkedHashMap;

public class BulkAction {

   private LinkedHashMap<String,IndexAction> kindToAction; // TODO or several wrapped classes ?
   private Document doc;
   
   public LinkedHashMap<String, IndexAction> getKindToAction() {
      return kindToAction;
   }
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
