package org.pcu.features.search.engine;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.pcu.platform.rest.server.PcuJaxrsServerBase;
import org.pcu.providers.search.api.PcuDocument;
import org.pcu.providers.search.api.PcuIndexResult;
import org.pcu.providers.search.api.PcuSearchApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import io.swagger.annotations.Api;

@Path("/search/api") // TODO Q or /searchcpt, /data, /nosql ?? can be extended on client side, and overloaded by impl (whose value should ONLY be "/" else blocks UI servlet)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Api("search api") // name of the api, merely a tag ; else not in swagger
@Service // for what, or only @Component ?
public class PcuSearchApiEngineImpl extends PcuJaxrsServerBase implements PcuSearchApi {

   /** or PcuSearchProviderApi ?? */
   //@Autowired @Qualifier("defaultSearchProviderApiImpl")
   //private PcuSearchApi pcuSearchApi;

   /** dummy impl */
   @Override
   public PcuIndexResult index(String index, PcuDocument pcuDoc) {
      PcuIndexResult res = new PcuIndexResult();
      return res ;
   }

   @Override
   public PcuDocument get(String index, String docId) {
      // TODO Auto-generated method stub
      return null;
   }
   
   // TODO impl search operations, by calling one or more time pcuSearchApi.search()
   // System.err.println("PcuSearchApiEngineImpl not implemented yet");
   public PcuIndexResult search(String indexOrEngine, String query) {
      PcuSearchApi searchEngineInstance = getSearchEngine(indexOrEngine);
      /*PcuSearchResult res = searchEngineInstance.search(query);
      return res;*/
      return null;
   }

   private PcuSearchApi getSearchEngine(String indexOrEngine) {
      // TODO Auto-generated method stub
      return null;
   }

}
