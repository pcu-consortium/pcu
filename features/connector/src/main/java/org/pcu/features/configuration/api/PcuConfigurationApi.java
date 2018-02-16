package org.pcu.features.configuration.api;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.pcu.providers.file.api.PcuFileResult;
import org.pcu.search.elasticsearch.api.Document;
import org.pcu.search.elasticsearch.api.ESApiException;
import org.pcu.search.elasticsearch.api.GetResult;
import org.pcu.search.elasticsearch.api.mapping.IndexResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;

/**
 * TODO Q or PcuComponentInstance/TemplateApis ??
 * 
 * @author mardut
 *
 */
@Path("/search/esapi") // /connector/api
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Api(value = "configuration api") // name of the api, merely a tag ; not required (only required on impl) 
public interface PcuConfigurationApi {

   // index API. Difficulty is being meta.
   // https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-index_.html
   //@Path("/{index}/{type}/{id}") // NOT id:.+ else No handler found for uri, URL encode it instead
   @Path("/configurations/{type}/{id}") // NOT id:.+ else No handler found for uri, URL encode it instead
   @PUT
   IndexResult put(@ApiParam(value = "type", defaultValue = "_all") @PathParam("type") String type,
         @ApiParam(value = "id") @PathParam("id") String id,
         @ApiParam(value = "document", required = true) PcuConfiguration pcuConf,
         /*@ApiParam(value = "routing, or mapped _routing field") @QueryParam("routing") String routing,
         @ApiParam(value = "ex. 5m") @QueryParam("timeout") String timeout,*/
         @QueryParam("version") Long version, @QueryParam("version_type") String version_type, // internal (> 0 !), external & external_gte do not support versioning, use index API instead (force is deprecated) https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-delete-by-query.html
         /*@ApiParam(value = "create, or _create path param", required = false) @QueryParam("op_type") String op_type,
         @ApiParam(value = "int or all, default is primaries only i.e. 1") @QueryParam("wait_for_active_shards") String wait_for_active_shards,*/
         @ApiParam(value = "true or empty, wait_for (makes indexing synchronous)", defaultValue = "false") @QueryParam("refresh") String refresh) throws ESApiException; // enum https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-refresh.html

   // LATER tasks, rethrottle, slice
   // https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-get.html
   @Path("/configurations/{type}/{id}") // NOT id:.+ else No handler found for uri, URL encode it instead
   @GET
   // https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-get.html#get-source-filtering
   GetConfigurationResult get(@ApiParam(value = "type", defaultValue = "_all") @PathParam("type") String type,
         @ApiParam(value = "id", required = true) @PathParam("id") String id,
         //@ApiParam(value = "routing") @QueryParam("routing") String routing,
         @ApiParam(value = "refresh", defaultValue = "false") @QueryParam("refresh") Boolean refresh/*,
         @ApiParam(value = "only if equal") @QueryParam("version") Long version,
         @ApiParam(value = "realtime", defaultValue = "false") @QueryParam("realtime") Boolean realtime,
         @ApiParam(value = "_source", defaultValue = "true") @QueryParam("_source") Boolean _source,
         @ApiParam(value = "preference") @QueryParam("preference") String preference, // or _primary, _local, or custom (session id, user name...)
         @ApiParam(value = "comma separated fields or wildcards (or _source but not supporting)") @QueryParam("_source_include") String _source_include,
         @ApiParam(value = "comma separated fields or wildcards") @QueryParam("_source_exclude") String _source_exclude*/) throws ESApiException;

   // https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-delete.html
   @Path("/configurations/{type}/{id}")
   @DELETE
   IndexResult delete(@ApiParam(value = "type", required = true) @PathParam("type") String type,
         @ApiParam(value = "id", required = true) @PathParam("id") String id,
         /*@ApiParam(value = "routing") String routing, @ApiParam(value = "ex. 5m, 1000") String timeout,
         Long version, String parent,
         @ApiParam(value = "int or all, default is primaries only i.e. 1") String wait_for_active_shards,*/
         @ApiParam(value = "true or empty, wait_for", defaultValue = "false") String refresh) throws ESApiException;
   
}
