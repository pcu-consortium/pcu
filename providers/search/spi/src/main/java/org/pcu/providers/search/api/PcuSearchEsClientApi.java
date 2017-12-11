package org.pcu.providers.search.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.pcu.search.elasticsearch.api.ElasticSearchClientApi;

/**
 * Helps clients by adding shorter, easier methods to inherited API ;
 * only use it to create "proxy" clients, not to implement servers !
 * (JAXRS differentiates operations only by path and not by ex. query params)
 * 
 * @author mardut
 *
 */
@Path("/search/esapi") // extend to override it for alt impl ; TODO Q or /searchcpt, /data, /nosql ?? can be extended on client side, and overloaded by impl (whose value should ONLY be "/" else blocks UI servlet)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public interface PcuSearchEsClientApi extends PcuSearchEsApi {
   
}
