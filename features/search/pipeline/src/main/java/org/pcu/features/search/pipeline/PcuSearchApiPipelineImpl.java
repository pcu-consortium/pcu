package org.pcu.features.search.pipeline;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.pcu.platform.rest.server.PcuJaxrsServerBase;
import org.pcu.providers.search.api.PcuDocument;
import org.pcu.providers.search.api.PcuIndexResult;
import org.pcu.providers.search.api.PcuSearchApi;
import org.springframework.stereotype.Service;

import io.swagger.annotations.Api;


/**
 * Implements PCU indexing on top of PCU pipeline (Spark Streaming filled by Kafka).
 * (CRUD & search operations are only dummy impls)
 * Can be used through REST on its own path, or configured to be used by the default PCU search impl.
 * 
 * @author mardut
 *
 */
@Path("/pipeline") // TODO Q or /searchcpt, /data, /nosql ?? can be extended on client side, and overloaded by impl (whose value should ONLY be "/" else blocks UI servlet)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Api("search index pipeline") // name of the api, merely a tag ; else not in swagger
@Service // for what, or only @Component ?
public class PcuSearchApiPipelineImpl extends PcuJaxrsServerBase implements PcuSearchIndexPipelineApi, PcuSearchApi {

   @Override
   public PcuIndexResult index(String index, PcuDocument pcuDoc) {
      // TODO send to Kafka
      System.err.println("PcuSearchApiPipelineImpl not implemented yet");
      PcuIndexResult res = new PcuIndexResult();
      return res ;
   }
   
   @PostConstruct
   protected void init() {
      // TODO setup Spark Streaming job
   }

   @Override
   public PcuDocument get(String index, String docId) {
      // NOO meaningless
      return null;
   }

}
