package org.pcu.features.search.server;

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
///@Service // for what, or only @Component ? NOO NAME CONFLICTS WITH PCUSEARCHAPI
public class PcuSearchApiServerImpl extends PcuJaxrsServerBase implements PcuSearchApi {

   @Autowired @Qualifier("defaultSearchProviderApi") // pcuSearchApiPipelineImpl
   private PcuSearchApi delegateSearchIndexApi;
   //@Autowired @Qualifier("defaultSearchProviderApiImpl") // pcuSearchApiSimpleImpl
   private PcuSearchApi delegateSearchCrudApi;
   //@Autowired @Qualifier("pcuSearchApiEngineImpl")
   private PcuSearchApi delegateSearchEngineApi;
   // delegateCrudApi
   // delegateSearchApi

   @Override
   public PcuIndexResult index(String index, PcuDocument pcuDoc) {
      return delegateSearchIndexApi.index(index, pcuDoc) ;
   }

   @Override
   public PcuDocument get(String index, String docId) {
      return delegateSearchIndexApi.get(index, docId);
   }

}
