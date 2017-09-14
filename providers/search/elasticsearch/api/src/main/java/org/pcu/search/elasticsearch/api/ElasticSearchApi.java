package org.pcu.search.elasticsearch.api;

import java.util.LinkedHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.pcu.search.elasticsearch.api.mapping.DeleteMappingResult;
import org.pcu.search.elasticsearch.api.mapping.IndexMapping;
import org.pcu.search.elasticsearch.api.mapping.IndexResult;
import org.pcu.search.elasticsearch.api.mapping.IndexSettings;
import org.pcu.search.elasticsearch.api.mapping.PutMappingResult;
import org.pcu.search.elasticsearch.api.mapping.TypeMapping;
import org.pcu.search.elasticsearch.api.query.ESQueryMessage;
import org.pcu.search.elasticsearch.api.query.SearchResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * JAXRS Definition for PCU of ElasticSearch REST API and of its calls :
 * - mappings configuration (complex)
 * - document indexing (meta)
 * - document query (composite)
 * 
 * unsupported :
 * - auth, because belongs to ES commercial offering : X-Pack Security (formerly Shield) https://www.elastic.co/products/x-pack/security https://www.elastic.co/guide/en/shield/current/getting-started.html
 * - _cat (plain text output)
 * - common params : pretty, human, error_trace (TODO !), source, filter_path outside _search (TODO ?!) https://github.com/elastic/elasticsearch/blob/master/rest-api-spec/src/main/resources/rest-api-spec/api/_common.json https://www.elastic.co/guide/en/elasticsearch/reference/current/common-options.html
 * - TODO bulk
 * - ? mget/search/...
 * - TODO requirements : MES (function_score, index mgmt ex. reindex ?), ekeller's list...
 * 
 * gotchas :
 * - doc id : slash not supported by ES (though JAXRS could : {id:.+}), but file path is not a good Lucene id anyway.
 * So rather use UUID v1 (the best Lucene id : http://blog.mikemccandless.com/2014/05/choosing-fast-unique-identifier-uuid.html ),
 * or hash id fields (like fscrawler does with file path https://github.com/shadiakiki1986/docker-fscrawler )
 * which provides auto dedup.
 * 
 * ElasticSearch REST API reference source :
 * https://github.com/elastic/elasticsearch/blob/master/rest-api-spec/src/main/resources/rest-api-spec/api/
 * 
 * TODO swagger defaultValue to jaxrs @DefaultValue (?)
 * 
 * @author mdutoo
 *
 */
@Path("/") // can be extended on client side, and overloaded by impl (whose value should ONLY be "/" else blocks UI servlet)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Api(value = "elasticsearch") // name of the api, merely a tag ; not required (only required on impl) 
public interface ElasticSearchApi {
   // {__unencoded__id}") //  to accept even /

   // mapping conf API. Difficulty is being complex.
   @ApiOperation(value = "Adding new types (to the index) or new fields (to types).",
         notes = "https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-put-mapping.html\n" + 
               "NB. can't update a type mapping in an index, must delete whole index"/*,
               response = PutMappingResult.class*/)
   @Path("/{indexPattern}")
   @PUT
   PutMappingResult putMapping(@PathParam("indexPattern") String indexPattern, IndexMapping indexMapping) throws ESApiException;
   @Path("/{indexPattern}/_mapping/{type}")
   @PUT
   void putTypeMapping(@PathParam("indexPattern") String indexPattern, @PathParam("type") String type, TypeMapping typeMapping,
         @ApiParam(value = "else fails if field in several types") @DefaultValue("false") @QueryParam("update_all_types") Boolean update_all_types) throws ESApiException; // TODO , defaultValue = "false"
   // https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-get-mapping.html
   @Path("{index}/_mapping/{type}") // NB. .+ works but not {type:(/type/[^/]+?)?} http://www.nakov.com/blog/2009/07/15/jax-rs-path-pathparam-and-optional-parameters/
   // still TODO alts : @Path("/_mapping/{type}") @Path("/_mapping")
   @GET
   LinkedHashMap<String,IndexMapping> getMapping(@ApiParam(value = "index", defaultValue = "_all") @PathParam("index") String index,
         @ApiParam(value = "type") @DefaultValue("")  @PathParam("type") String type) throws ESApiException;
   @Path("{index}/_mapping")
   @GET
   LinkedHashMap<String,IndexMapping> getMapping(@ApiParam(value = "index", defaultValue = "_all") @PathParam("index") String index) throws ESApiException;
   // https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-get-settings.html
   @Path("{indexPattern}/_settings")
   @GET
   LinkedHashMap<String,IndexSettings> getSettings(@ApiParam(value = "index", defaultValue = "_all") @PathParam("index") String index) throws ESApiException;
   @ApiOperation(value = "Deletes an index.",
         notes = "https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-delete-index.html\n" + 
               "NB. can't delete a type mapping in an index, must delete whole index https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-delete-mapping.html"/*,
               response = DeleteMappingResult.class*/)
   @Path("/{indexPattern}")
   @DELETE
   DeleteMappingResult deleteMapping(@PathParam("indexPattern") String indexPattern) throws ESApiException;
   // LATER getFieldMapping https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-get-field-mapping.html
   // LATER GET my_index/_analyze
   // TODO dynamic mapping https://www.elastic.co/guide/en/elasticsearch/reference/current/dynamic-mapping.html
   
   // index API. Difficulty is being meta.
   // https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-index_.html
   @Path("/{index}/{type}/{id}") // NOT id:.+ else No handler found for uri, URL encode it instead
   @PUT
   IndexResult indexDocument(@ApiParam(value = "index", required = true) @PathParam("index") String index,
         @ApiParam(value = "type") @PathParam("type") String type, @ApiParam(value = "id") @PathParam("id") String id,
         @ApiParam(value = "document", required = true) Document doc,
         @ApiParam(value = "routing, or mapped _routing field") @QueryParam("routing") String routing,
         @ApiParam(value = "ex. 5m") @QueryParam("timeout") String timeout,
         @QueryParam("version") Long version, @QueryParam("version_type") String version_type, // internal (> 0 !), external & external_gte do not support versioning, use index API instead (force is deprecated) https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-delete-by-query.html
         @ApiParam(value = "create, or _create path param", required = false) @QueryParam("op_type") String op_type,
         @ApiParam(value = "int or all, default is primaries only i.e. 1") @QueryParam("wait_for_active_shards") String wait_for_active_shards,
         @ApiParam(value = "true or empty, wait_for", defaultValue = "false") @QueryParam("refresh") String refresh) throws ESApiException; // enum https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-refresh.html
   @ApiOperation("same as PUT /{index}/{type}/id with id generation")
   @Path("/{index}/{type}")
   @PUT
   IndexResult indexDocument(@ApiParam(value = "index", required = true) @PathParam("index") String index,
         @ApiParam(value = "type") @PathParam("type") String type,
         @ApiParam(value = "document", required = true) Document doc,
         @ApiParam(value = "routing, or mapped _routing field") @QueryParam("routing") String routing,
         @ApiParam(value = "ex. 5m") @QueryParam("timeout") String timeout,
         @QueryParam("version") Long version, @QueryParam("version_type") String version_type, // internal (> 0 !), external & external_gte do not support versioning, use index API instead (force is deprecated) https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-delete-by-query.html
         @ApiParam(value = "create, or _create path param", required = false) @QueryParam("op_type") String op_type,
         @ApiParam(value = "int or all, default is primaries only i.e. 1") @QueryParam("wait_for_active_shards") String wait_for_active_shards,
         @ApiParam(value = "true or empty, wait_for", defaultValue = "false") @QueryParam("refresh") String refresh) throws ESApiException; // enum https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-refresh.html
   // https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-update.html
   @Path("/{index}/{type}/{id}")
   @POST
   IndexResult updateDocument(@PathParam("index") String index, @PathParam("type") String type,
         @PathParam("id") String id, Document doc,
         @ApiParam(value = "routing") String routing, @ApiParam(value = "timeout") String timeout,
         Long version, String version_type, // internal (> 0 !), no versioning with external & external_gte, use index API instead (force is deprecated) https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-delete-by-query.html
         Integer retry_on_conflict, @ApiParam(value = "int or all, default is primaries only i.e. 1") String wait_for_active_shards,
         @ApiParam(value = "true or empty, wait_for", defaultValue = "false") String refresh, // 
         boolean _source, String parent) throws ESApiException;
   @Path("/{index}/{type}/{id}/_update")
   @POST
   IndexResult updateDocument(@ApiParam(value = "index", required = true) @PathParam("index") String index,
         @ApiParam(value = "type", required = true) @PathParam("type") String type,
         @ApiParam(value = "id", required = true) @PathParam("id") String id, UpdateRequest updateRequest) throws ESApiException; // TODO version ??
   void updateDocumentByQuery(ESQueryMessage query, Document doc);
   // https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-delete.html
   @Path("/{index}/{type}/{id}")
   @DELETE
   IndexResult deleteDocument(@ApiParam(value = "index", required = true) @PathParam("index") String index,
         @ApiParam(value = "type", required = true) @PathParam("type") String type,
         @ApiParam(value = "id", required = true) @PathParam("id") String id,
         @ApiParam(value = "routing") String routing, @ApiParam(value = "ex. 5m, 1000") String timeout,
         Long version, String parent,
         @ApiParam(value = "int or all, default is primaries only i.e. 1") String wait_for_active_shards,
         @ApiParam(value = "true or empty, wait_for", defaultValue = "false") String refresh) throws ESApiException;
   // https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-delete-by-query.html
   @Path("/{indexPattern}/{typePattern}/_delete_by_query") // NB. .+ works but not {typePattern:(/[^/]+?)?} http://www.nakov.com/blog/2009/07/15/jax-rs-path-pathparam-and-optional-parameters/
   @POST
   DeleteByQueryResult deleteDocumentByQuery(@PathParam("indexPattern") String index,
         @PathParam("typePattern") String typePattern, ESQueryMessage query,
         @ApiParam(defaultValue = "false") Boolean pretty, // TODO everywhere
         @ApiParam(value = "ex. 5m, 1000") String timeout,
         @ApiParam(value = "true or empty, wait_for", defaultValue = "false") String refresh,
         @ApiParam(value = "int or all, default is primaries only i.e. 1") String wait_for_active_shards,
         @ApiParam(value = "", defaultValue = "false") Boolean wait_for_completion) throws ESApiException;
   // LATER tasks, rethrottle, slice
   // https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-get.html
   @Path("/{index}/{type}/{id}") // NOT id:.+ else No handler found for uri, URL encode it instead
   @GET
   // https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-get.html#get-source-filtering
   GetResult getDocument(@ApiParam(value = "index", required = true) @PathParam("index") String index,
         @ApiParam(value = "type", defaultValue = "_all") @PathParam("type") String type,
         @ApiParam(value = "id", required = true) @PathParam("id") String id,
         @ApiParam(value = "routing") @QueryParam("routing") String routing,
         @ApiParam(value = "refresh", defaultValue = "false") @QueryParam("refresh") Boolean refresh,
         @ApiParam(value = "only if equal") @QueryParam("version") Long version,
         @ApiParam(value = "realtime", defaultValue = "false") @QueryParam("realtime") Boolean realtime,
         @ApiParam(value = "_source", defaultValue = "true") @QueryParam("_source") Boolean _source,
         @ApiParam(value = "preference") @QueryParam("preference") String preference, // or _primary, _local, or custom (session id, user name...)
         @ApiParam(value = "comma separated fields or wildcards (or _source but not supporting)") @QueryParam("_source_include") String _source_include,
         @ApiParam(value = "comma separated fields or wildcards") @QueryParam("_source_exclude") String _source_exclude) throws ESApiException;
   @ApiOperation("same as GET")
   @Path("/{index}/{type}/{id}")
   @HEAD
   // https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-get.html#get-source-filtering
   GetResult getDocumentHead(@ApiParam(value = "index", required = true) @PathParam("index") String index,
         @ApiParam(value = "type", defaultValue = "_all") @PathParam("type") String type,
         @ApiParam(value = "id", required = true) @PathParam("id") String id,
         @ApiParam(value = "routing") @QueryParam("routing") String routing,
         @ApiParam(value = "refresh", defaultValue = "false") @QueryParam("refresh") Boolean refresh,
         @ApiParam(value = "only if equal") @QueryParam("version") Long version,
         @ApiParam(value = "realtime", defaultValue = "false") @QueryParam("realtime") Boolean realtime,
         @ApiParam(value = "preference") @QueryParam("preference") String preference, // or _primary, _local, or custom (session id, user name...)
         @ApiParam(value = "comma separated fields or wildcards (or _source but not supporting)") @QueryParam("_source_include") String _source_include,
         @ApiParam(value = "comma separated fields or wildcards") @QueryParam("_source_exclude") String _source_exclude) throws ESApiException;
   // https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-get.html#get-source-filtering
   @Path("/{index}/{type}/{id}/_source")
   @GET
   Document getDocumentSource(@ApiParam(value = "index", required = true) @PathParam("index") String index,
         @ApiParam(value = "type", defaultValue = "_all") @PathParam("type") String type,
         @ApiParam(value = "id", required = true) @PathParam("id") String id,
         @ApiParam(value = "routing") @QueryParam("routing") String routing,
         @ApiParam(value = "refresh", defaultValue = "false") @QueryParam("refresh") Boolean refresh,
         @ApiParam(value = "only if equal") @QueryParam("version") Long version,
         @ApiParam(value = "realtime", defaultValue = "false") @QueryParam("realtime") Boolean realtime,
         @ApiParam(value = "preference") @QueryParam("preference") String preference, // or _primary, _local, or custom (session id, user name...)
         @ApiParam(value = "! comma separated fields or wildcards (or _source but not supporting)") @QueryParam("_source_include") String _source_include,
         @ApiParam(value = "! comma separated fields or wildcards") @QueryParam("_source_exclude") String _source_exclude) throws ESApiException;

   // bulk API. Difficulty is "flat" top-level serialization.
   // TODO TODO in open HTTP connection, which ES probably does like Solr https://cwiki.apache.org/confluence/display/solr/Using+SolrJ
   // TODO also /{index}/{type}/_bulk ?
   // https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-bulk.html
   @Path("/_bulk")
   @POST
   BulkResult bulk(@ApiParam(value = "document", required = true) BulkMessage doc,
         @ApiParam(value = "int or all, default is primaries only i.e. 1") @QueryParam("wait_for_active_shards") String wait_for_active_shards,
         @ApiParam(value = "true or empty, wait_for", defaultValue = "false") @QueryParam("refresh") String refresh) throws ESApiException;
   
   // Query API. Difficulty is being composite.
   // https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-body.html
   @Path("/_search")
   @POST
   @GET
   SearchResult search(ESQueryMessage queryMessage,
         @ApiParam(value = "or dfs_query_then_fetch", defaultValue = "query_then_fetch") @QueryParam("search_type") String search_type,
         @ApiParam(value = "overrides index conf whose default is true") @QueryParam("request_cache") Boolean request_cache) throws ESApiException;
   // with common options :
   // filter_path pattern https://www.elastic.co/guide/en/elasticsearch/reference/current/common-options.html
   // TODO transversal for all operations
   @Path("/_search")
   @POST
   @GET
   SearchResult search(ESQueryMessage queryMessage,
         @ApiParam(value = "or dfs_query_then_fetch", defaultValue = "query_then_fetch") @QueryParam("search_type") String search_type,
         @ApiParam(value = "overrides index conf whose default is true") @QueryParam("request_cache") Boolean request_cache,
         @ApiParam(value = "ex. took,hits.hits._id,hits.hits.name*") @QueryParam("filter_path") String filter_path) throws ESApiException;

}
