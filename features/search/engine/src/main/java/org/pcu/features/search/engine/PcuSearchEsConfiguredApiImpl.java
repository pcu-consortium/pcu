package org.pcu.features.search.engine;

import java.util.LinkedHashMap;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.pcu.platform.rest.server.PcuJaxrsServerBase;
import org.pcu.providers.search.api.PcuSearchEsApi;
import org.pcu.search.elasticsearch.api.BulkMessage;
import org.pcu.search.elasticsearch.api.BulkResult;
import org.pcu.search.elasticsearch.api.DeleteByQueryResult;
import org.pcu.search.elasticsearch.api.Document;
import org.pcu.search.elasticsearch.api.ESApiException;
import org.pcu.search.elasticsearch.api.ElasticSearchApi;
import org.pcu.search.elasticsearch.api.GetResult;
import org.pcu.search.elasticsearch.api.UpdateRequest;
import org.pcu.search.elasticsearch.api.mapping.DeleteMappingResult;
import org.pcu.search.elasticsearch.api.mapping.IndexMapping;
import org.pcu.search.elasticsearch.api.mapping.IndexResult;
import org.pcu.search.elasticsearch.api.mapping.IndexSettings;
import org.pcu.search.elasticsearch.api.mapping.PutMappingResult;
import org.pcu.search.elasticsearch.api.mapping.TypeMapping;
import org.pcu.search.elasticsearch.api.query.ESQueryMessage;
import org.pcu.search.elasticsearch.api.query.SearchResult;
import org.springframework.stereotype.Service;

import io.swagger.annotations.Api;

/**
 * Configuration-driven implementation of PCU ElasticSearch-like API Impl on ElasticSearch.
 * Allows to enable/disable and configure intelligent search features on top of ElasticSearch.
 * Draft, see commented code in search().
 * @author mardut
 *
 */
@Path("/search/esconfiguredapi") // TODO Q or /searchcpt, /data, /nosql ?? can be extended on client side, and overloaded by impl (whose value should ONLY be "/" else blocks UI servlet)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Api("PCU search ES configured API") // name of the api, merely a tag ; else not in swagger
@Service("pcuSearchEsConfiguredApi") // for what, or only @Component ? HOW TO INJECT : SECURITY MAPPING CHECK & CRITERIA, SCHEMA CHECK ?? EVEN IN MODELSERVICE ??
public class PcuSearchEsConfiguredApiImpl extends PcuJaxrsServerBase implements PcuSearchEsApi {

   //@Autowired @Qualifier("elasticSearchRestClient")
   @Resource(name="elasticSearchRestClient") // NOT @Autowired else NoSuchBean because proxy has not the same class https://stackoverflow.com/questions/15614786/could-not-autowire-jaxrs-client
   private ElasticSearchApi elasticSearchRestClient;
   
   public PcuSearchEsConfiguredApiImpl() {
      
   }

   public PutMappingResult putMapping(String indexPattern, IndexMapping indexMapping) throws ESApiException {
      return elasticSearchRestClient.putMapping(indexPattern, indexMapping);
   }

   public void putTypeMapping(String indexPattern, String type, TypeMapping typeMapping, Boolean update_all_types)
         throws ESApiException {
      elasticSearchRestClient.putTypeMapping(indexPattern, type, typeMapping, update_all_types);
   }

   public LinkedHashMap<String, IndexMapping> getMapping(String index, String type) throws ESApiException {
      return elasticSearchRestClient.getMapping(index, type);
   }

   public LinkedHashMap<String, IndexMapping> getMapping(String index) throws ESApiException {
      return elasticSearchRestClient.getMapping(index);
   }

   public LinkedHashMap<String, IndexSettings> getSettings(String index) throws ESApiException {
      return elasticSearchRestClient.getSettings(index);
   }

   public DeleteMappingResult deleteMapping(String indexPattern) throws ESApiException {
      return elasticSearchRestClient.deleteMapping(indexPattern);
   }

   public IndexResult indexDocument(String index, String type, String id, Document doc, String routing, String timeout,
         Long version, String version_type, String op_type, String wait_for_active_shards, String refresh)
         throws ESApiException {
      return elasticSearchRestClient.indexDocument(index, type, id, doc, routing, timeout, version, version_type,
            op_type, wait_for_active_shards, refresh);
   }

   public IndexResult indexDocument(String index, String type, Document doc, String routing, String timeout,
         Long version, String version_type, String op_type, String wait_for_active_shards, String refresh)
         throws ESApiException {
      return elasticSearchRestClient.indexDocument(index, type, doc, routing, timeout, version, version_type, op_type,
            wait_for_active_shards, refresh);
   }

   public IndexResult updateDocument(String index, String type, String id, Document doc, String routing, String timeout,
         Long version, String version_type, Integer retry_on_conflict, String wait_for_active_shards, String refresh,
         boolean _source, String parent) throws ESApiException {
      return elasticSearchRestClient.updateDocument(index, type, id, doc, routing, timeout, version, version_type,
            retry_on_conflict, wait_for_active_shards, refresh, _source, parent);
   }

   public IndexResult updateDocument(String index, String type, String id, UpdateRequest updateRequest)
         throws ESApiException {
      return elasticSearchRestClient.updateDocument(index, type, id, updateRequest);
   }

   public void updateDocumentByQuery(ESQueryMessage query, Document doc) {
      elasticSearchRestClient.updateDocumentByQuery(query, doc);
   }

   public IndexResult deleteDocument(String index, String type, String id, String routing, String timeout, Long version,
         String wait_for_active_shards, String refresh) throws ESApiException {
      return elasticSearchRestClient.deleteDocument(index, type, id, routing, timeout, version, wait_for_active_shards, refresh);
   }

   public DeleteByQueryResult deleteDocumentByQuery(String index, String typePattern, ESQueryMessage query,
         String routing, String timeout, String refresh, String wait_for_active_shards, Boolean wait_for_completion)
         throws ESApiException {
      return elasticSearchRestClient.deleteDocumentByQuery(index, typePattern, query, routing, timeout, refresh, wait_for_active_shards, wait_for_completion);
   }

   public GetResult getDocument(String index, String type, String id, String routing, String refresh, Long version,
         Boolean realtime, Boolean _source, String preference, String _source_include, String _source_exclude)
         throws ESApiException {
      return elasticSearchRestClient.getDocument(index, type, id, routing, refresh, version, realtime, _source, preference, _source_include, _source_exclude);
   }

   public GetResult getDocumentHead(String index, String type, String id, String routing, String refresh, Long version,
         Boolean realtime, String preference, String _source_include, String _source_exclude) throws ESApiException {
      return elasticSearchRestClient.getDocumentHead(index, type, id, routing, refresh, version, realtime, preference, _source_include, _source_exclude);
   }

   public Document getDocumentSource(String index, String type, String id, String routing, String refresh,
         Long version, Boolean realtime, String preference, String _source_include, String _source_exclude)
         throws ESApiException {
      return elasticSearchRestClient.getDocumentSource(index, type, id, routing, refresh, version, realtime, preference, _source_include, _source_exclude);
   }

   public BulkResult bulk(BulkMessage doc, String wait_for_active_shards, String refresh) throws ESApiException {
      return elasticSearchRestClient.bulk(doc, wait_for_active_shards, refresh);
   }

   public SearchResult search(ESQueryMessage queryMessage, String search_type, Boolean request_cache,
         String filter_path) throws ESApiException {
      /*
      String smartEngineConfId = cxfJaxrsApiProvider.getHttpHeaders().get("X-smartEngineConfId");
      
      String smartEngineImplConf = confApi.get(smartEngineConfId); // impl'd as pcuEsApi.get("configuration", smartEngineConfId)
      PcuSmartEngineBase smartEngineImpl = confObjectMapper.readValue(smartEngineImplConf, PcuSmartEngineBase.class);
      // which requires impl class to be stored in JSON conf and ObjectMapper to be conf'd for it.
      // OR without that (but hardcoded) :
      // PcuSmartEngineIntentDetectionImpl = confApi.getClassForId(smartEngineConfId)
      // PcuSmartEngineIntentDetectionImpl smartEngineImpl = pcuSmartEngineIntentDetectionApi.get(smartEngineConfId);
      
      smartEngineImpl.setElasticSearchApi(elasticSearchRestClient);
      return smartEngineImpl.search(queryMessage, search_type, request_cache, filter_path);
      
      // OR with a query enricher pattern :
      queryMessage = smartEngineImpl.enrichQuery(queryMessage);
      return elasticSearchRestClient.search(queryMessage, search_type, request_cache, filter_path);
      
      // OR smartEngineImpl could actually be a full-fledged configurable request pipeline, made of
      // request pipeline components (like EIP or logstash or Solr's or Lucidworks')
      */
      return elasticSearchRestClient.search(queryMessage, search_type, request_cache, filter_path);
   }

   @Override
   public SearchResult searchInType(String index, String type, ESQueryMessage queryMessage, String search_type,
         Boolean request_cache, String filter_path) throws ESApiException {
      return elasticSearchRestClient.searchInType(index, type, queryMessage, search_type, request_cache, filter_path);
   }
   
}
