package org.pcu.search.elasticsearch.api.query.clause;

import org.pcu.search.elasticsearch.api.query.ESQuery;

public class FunctionScoreFilter extends FunctionScoreFunctions {

   private ESQuery filter;

   public ESQuery getFilter() {
      return filter;
   }
   public void setFilter(ESQuery filter) {
      this.filter = filter;
   }
   
}
