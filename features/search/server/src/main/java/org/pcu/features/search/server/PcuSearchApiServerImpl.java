package org.pcu.features.search.server;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.pcu.features.search.api.PcuDocument;
import org.pcu.features.search.api.PcuIndexResult;
import org.pcu.features.search.api.PcuSearchApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import io.swagger.annotations.Api;

@Path("/search/api") // TODO Q or /searchcpt, /data, /nosql ?? can be extended on client side, and overloaded by impl (whose value should ONLY be "/" else blocks UI servlet)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Api("search api") // name of the api, merely a tag ; else not in swagger
@Service // for what, or only @Component ?
public class PcuSearchApiServerImpl implements PcuSearchApi {

   @Autowired @Qualifier("pcuSearchApiPipelineImpl")
   private PcuSearchApi delegateSearchIndexApi;
   @Autowired @Qualifier("pcuSearchApiSimpleImpl")
   private PcuSearchApi delegateSearchCrudApi;
   @Autowired @Qualifier("pcuSearchApiEngineImpl")
   private PcuSearchApi delegateSearchEngineApi;
   // delegateCrudApi
   // delegateSearchApi

   @Override
   public PcuIndexResult index(String index, PcuDocument pcuDoc) {
      return delegateSearchIndexApi.index(index, pcuDoc) ;
   }

}
