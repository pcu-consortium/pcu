package org.pcu.providers.search.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.pcu.search.elasticsearch.api.ElasticSearchApi;

import io.swagger.annotations.Api;

/**
 * PCU ElasticSearch-like API
 * @author mardut
 *
 */
@Path("/search/esapi") // extend to override it for alt impl ; TODO Q or /searchcpt, /data, /nosql ?? can be extended on client side, and overloaded by impl (whose value should ONLY be "/" else blocks UI servlet)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Api(value = "search ES api") // name of the api, merely a tag ; not required (only required on impl) 
public interface PcuSearchEsApi extends ElasticSearchApi {
   
}
