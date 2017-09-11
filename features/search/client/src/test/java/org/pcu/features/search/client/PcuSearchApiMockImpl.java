package org.pcu.features.search.client;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.pcu.providers.search.api.PcuDocument;
import org.pcu.providers.search.api.PcuIndexResult;
import org.pcu.providers.search.api.PcuSearchApi;

//@Path("/") // else "not found" because overrides // else not in swagger ; but NOT "/pcu/search/es" else blocks UI servlet
@Consumes({MediaType.APPLICATION_JSON + "; charset=utf-8"})
@Produces({MediaType.APPLICATION_JSON + "; charset=utf-8"}) // else default tomcat conf produces ISO-8859-1 BUT STILL 500 KO
public class PcuSearchApiMockImpl implements PcuSearchApi {
   
   public static final String TEST_ = "test_username";

   //public final LinkedHashMap<String,IndexMapping> indexMappings = new LinkedHashMap<String,IndexMapping>();
   /** DocumentResult rather than Document in order to know about index & type */
   public final HashMap<String,PcuDocument> docs = new HashMap<String,PcuDocument>();
   
   @Override
   public PcuIndexResult index(String index, PcuDocument doc) {
      PcuIndexResult res = new PcuIndexResult();
      return res ;
   }

   @Override
   public PcuDocument get(String index, String docId) {
      // TODO Auto-generated method stub
      return null;
   }

}
