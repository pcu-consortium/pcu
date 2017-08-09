package org.pcu.search.elasticsearch.api;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/breaking_50_document_api_changes.html
 * @author mdutoo
 *
 */
public class RetriesResult {

   private int bulk;
   private int search;
   
   public int getBulk() {
      return bulk;
   }
   public void setBulk(int bulk) {
      this.bulk = bulk;
   }
   public int getSearch() {
      return search;
   }
   public void setSearch(int search) {
      this.search = search;
   }
   
}
