package org.pcu.features.search.simple;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.pcu.platform.rest.server.PcuJaxrsServerBase;
import org.pcu.providers.file.api.PcuFileApi;
import org.pcu.providers.search.api.PcuDocument;
import org.pcu.providers.search.api.PcuIndexResult;
import org.pcu.providers.search.api.PcuSearchApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import io.swagger.annotations.Api;

// NOO meaningless, replaced by bean alias defaultSearchProviderApiImpl
@Path("/search/api") // TODO Q or /searchcpt, /data, /nosql ?? can be extended on client side, and overloaded by impl (whose value should ONLY be "/" else blocks UI servlet)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Api("search api") // name of the api, merely a tag ; else not in swagger
@Service // for what, or only @Component ?
public class PcuSearchApiSimpleImpl extends PcuJaxrsServerBase implements PcuSearchApi {

   @Autowired @Qualifier("defaultSearchProviderApiImpl")
   private PcuSearchApi esSearchProviderApi;

   @Override
   public PcuIndexResult index(String index, PcuDocument doc) {
      return this.esSearchProviderApi.index(index, doc);
   }

   @Override
   public PcuDocument get(String index, String docId) {
      return this.esSearchProviderApi.get(index, docId);
   }

}
