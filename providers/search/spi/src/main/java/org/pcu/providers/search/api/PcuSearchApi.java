package org.pcu.providers.search.api;

import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.annotations.Api;

/**
 * TODO Q also type param ?!
 * @author mardut
 *
 */
@Path("/search/api") // extend to override it for alt impl ; TODO Q or /searchcpt, /data, /nosql ?? can be extended on client side, and overloaded by impl (whose value should ONLY be "/" else blocks UI servlet)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Api(value = "search api") // name of the api, merely a tag ; not required (only required on impl) 
public interface PcuSearchApi {

   @Path("/index/{index}")
   @PUT
   PcuIndexResult index(@PathParam("index") String index, PcuDocument doc);
   
   // CRUD :
   @Path("/index/{docId}")
   @GET
   PcuDocument get(@PathParam("index") String index, @PathParam("docId") String docId);
   // TODO more
   
   // search :
   // TODO

   // store :
   // REMOVED

   //PcuResult search(String query);
   
}
