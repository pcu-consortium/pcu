package org.pcu.features.search.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.annotations.Api;

@Path("/search/api") // extend to override it for alt impl ; TODO Q or /searchcpt, /data, /nosql ?? can be extended on client side, and overloaded by impl (whose value should ONLY be "/" else blocks UI servlet)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Api(value = "search api") // name of the api, merely a tag ; not required (only required on impl) 
public interface PcuSearchApi {

   @Path("/{index}")
   @PUT
   PcuIndexResult index(@PathParam("index") String index, PcuDocument doc);
   
}
