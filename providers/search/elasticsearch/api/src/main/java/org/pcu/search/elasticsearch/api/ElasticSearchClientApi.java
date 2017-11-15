package org.pcu.search.elasticsearch.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.pcu.search.elasticsearch.api.query.ESQueryMessage;
import org.pcu.search.elasticsearch.api.query.SearchResult;

import io.swagger.annotations.ApiParam;

/**
 * Helps clients by adding shorter, easier methods to inherited API ;
 * only use it to create "proxy" clients, not to implement servers !
 * (JAXRS differentiates operations only by path and not by ex. query params)
 * 
 * @author mdutoo
 *
 */
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public interface ElasticSearchClientApi extends ElasticSearchApi {
   
   // Query API. Difficulty is being composite.
   // https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-body.html
   // without common options
   @Path("/_search")
   @POST
   @GET
   SearchResult search(ESQueryMessage queryMessage,
         @ApiParam(value = "or dfs_query_then_fetch", defaultValue = "query_then_fetch") @QueryParam("search_type") String search_type,
         @ApiParam(value = "overrides index conf whose default is true") @QueryParam("request_cache") Boolean request_cache) throws ESApiException;
   // Query API. Difficulty is being composite.
   // https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-body.html
   // without common options
   @Path("/{index}/{type}/_search")
   @POST
   @GET
   SearchResult searchInType(@ApiParam(value = "index", required = true) @PathParam("index") String index,
         @ApiParam(value = "type", defaultValue = "_all") @PathParam("type") String type,
         ESQueryMessage queryMessage,
         @ApiParam(value = "or dfs_query_then_fetch", defaultValue = "query_then_fetch") @QueryParam("search_type") String search_type,
         @ApiParam(value = "overrides index conf whose default is true") @QueryParam("request_cache") Boolean request_cache) throws ESApiException;

}
