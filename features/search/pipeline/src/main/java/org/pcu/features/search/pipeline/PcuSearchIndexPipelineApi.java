package org.pcu.features.search.pipeline;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.pcu.providers.search.api.PcuSearchApi;

import io.swagger.annotations.Api;

@Path("/pipeline") // TODO Q or /searchcpt, /data, /nosql ?? can be extended on client side, and overloaded by impl (whose value should ONLY be "/" else blocks UI servlet)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Api("search index pipeline") // name of the api, merely a tag ; else not in swagger
public interface PcuSearchIndexPipelineApi extends PcuSearchApi {

}
