package org.pcu.search.elasticsearch.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.pcu.search.elasticsearch.api.BulkAction;
import org.pcu.search.elasticsearch.api.BulkMessage;
import org.pcu.search.elasticsearch.api.BulkResult;
import org.pcu.search.elasticsearch.api.DeleteByQueryResult;
import org.pcu.search.elasticsearch.api.Document;
import org.pcu.search.elasticsearch.api.DocumentResult;
import org.pcu.search.elasticsearch.api.ESApiException;
import org.pcu.search.elasticsearch.api.ElasticSearchApi;
import org.pcu.search.elasticsearch.api.GetResult;
import org.pcu.search.elasticsearch.api.IndexAction;
import org.pcu.search.elasticsearch.api.UpdateRequest;
import org.pcu.search.elasticsearch.api.mapping.DeleteMappingResult;
import org.pcu.search.elasticsearch.api.mapping.IndexMapping;
import org.pcu.search.elasticsearch.api.mapping.IndexResult;
import org.pcu.search.elasticsearch.api.mapping.IndexSettings;
import org.pcu.search.elasticsearch.api.mapping.PutMappingResult;
import org.pcu.search.elasticsearch.api.mapping.TypeMapping;
import org.pcu.search.elasticsearch.api.query.ESQueryMessage;
import org.pcu.search.elasticsearch.api.query.Hit;
import org.pcu.search.elasticsearch.api.query.Hits;
import org.pcu.search.elasticsearch.api.query.SearchResult;

@Path("/") // else not in swagger ; but NOT "/pcu/search/es" else blocks UI servlet
@Consumes({MediaType.APPLICATION_JSON + "; charset=utf-8"})
@Produces({MediaType.APPLICATION_JSON + "; charset=utf-8"}) // else default tomcat conf produces ISO-8859-1 BUT STILL 500 KO
public class ElasticSearchApiMockImpl implements ElasticSearchApi {
   
   public static final String TEST_ = "test_username";
   public static final int took_search = 1000;
   public static final int took_searchInType = 2000;

   public final LinkedHashMap<String,IndexMapping> indexMappings = new LinkedHashMap<String,IndexMapping>();
   /** DocumentResult rather than Document in order to know about index & type */
   public final HashMap<String,DocumentResult> docs = new HashMap<String,DocumentResult>();

   @Override
   public PutMappingResult putMapping(String indexPattern, IndexMapping indexMapping) {
      this.indexMappings.put(indexPattern, indexMapping);
      PutMappingResult res = new PutMappingResult();
      res.setAcknowledged(true);
      res.setShards_acknowledged(true);
      return res;
   }

   @Override
   public void putTypeMapping(String indexPattern, String type, TypeMapping typeMapping, Boolean update_all_types) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public LinkedHashMap<String, IndexMapping> getMapping(String index, String type) {
      return this.indexMappings;
   }

   @Override
   public LinkedHashMap<String, IndexMapping> getMapping(String index) {
      return this.indexMappings;
   }

   @Override
   public LinkedHashMap<String, IndexSettings> getSettings(String index) {
      return null;//this.indexMappings.get(index).getSettings().
   }

   @Override
   public DeleteMappingResult deleteMapping(String indexPattern) {
      DeleteMappingResult res = new DeleteMappingResult();
      res.setAcknowledged(true);
      return res ;
   }

   @Override
   public IndexResult indexDocument(String index, String type, String id, Document docSource, String routing,
         String timeout, Long version, String version_type, String op_type, String wait_for_active_shards,
         String refresh) {
      DocumentResult docRes = new DocumentResult();
      docRes.set_index(index);
      docRes.set_type(type);
      docRes.set_id(id);
      docRes.set_source(docSource);
      docs.put(id, docRes);
      IndexResult res = new IndexResult();
      res.set_index(index);
      res.set_type(type);
      res.set_id(id);
      res.setCreated(true);
      res.setResult("created");
      return res;
   }

   @Override
   public IndexResult indexDocument(String index, String type, Document doc, String routing,
         String timeout, Long version, String version_type, String op_type, String wait_for_active_shards,
         String refresh) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public IndexResult updateDocument(String index, String type, String id, Document doc, String routing, String timeout,
         Long version, String version_type, Integer retry_on_conflict, String wait_for_active_shards, String refresh,
         boolean _source, String parent) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public IndexResult updateDocument(String index, String type, String id, UpdateRequest updateRequest) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void updateDocumentByQuery(ESQueryMessage query, Document doc) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public IndexResult deleteDocument(String index, String type, String id, String routing, String timeout, Long version,
         String wait_for_active_shards, String refresh) throws ESApiException {
      IndexResult res = new IndexResult();
      res.set_index(index);
      res.set_type(type);
      res.set_id(id);
      res.setFound(true);
      res.setResult("deleted");
      return res;
   }

   @Override
   public DeleteByQueryResult deleteDocumentByQuery(String index, String typePattern, ESQueryMessage query,
         String routing, String timeout, String refresh, String wait_for_active_shards, Boolean wait_for_completion)
         throws ESApiException {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public GetResult getDocument(String index, String type, String id, String routing, String refresh, Long version,
         Boolean realtime, Boolean _source, String preference, String _source_include, String _source_exclude)
         throws ESApiException {
      GetResult res = new GetResult();
      res.set_index(index);
      res.set_type(type);
      res.set_id(id);
      res.set_routing(routing);
      res.set_version(version);
      res.set_source(docs.get(id).get_source());
      return res;
   }

   @Override
   public GetResult getDocumentHead(String index, String type, String id, String routing, String refresh, Long version,
         Boolean realtime, String preference, String _source_include, String _source_exclude) throws ESApiException {
      GetResult res = new GetResult();
      res.set_index(index);
      return res;
   }

   @Override
   public Document getDocumentSource(String index, String type, String id, String routing, String refresh, Long version,
         Boolean realtime, String preference, String _source_include, String _source_exclude) throws ESApiException {
      return docs.get(id).get_source();
   }

   @Override
   public BulkResult bulk(BulkMessage bulkMessage, String wait_for_active_shards, String refresh) throws ESApiException {
      BulkResult res = new BulkResult();
      List<LinkedHashMap<String, IndexResult>> items = new ArrayList<LinkedHashMap<String, IndexResult>>();
      res.setItems(items);
      for (BulkAction bulkAction : bulkMessage.getActions()) {
         IndexAction indexAction = bulkAction.getKindToAction().get("index");
         LinkedHashMap<String, IndexResult> item = new LinkedHashMap<String, IndexResult>();
         IndexResult indexResult = new IndexResult();
         indexResult.set_index(indexAction.get_index());
         indexResult.set_type(indexAction.get_type());
         indexResult.set_id(indexAction.get_id());
         item.put("index", indexResult );
         items.add(item);
      }
      return res;
   }

   @Override
   public SearchResult search(ESQueryMessage queryMessage, String search_type, Boolean request_cache,
         String filter_path) throws ESApiException {
      SearchResult res = new SearchResult();
      Hits hits = new Hits();
      if (!docs.isEmpty()) {
         hits.setTotal(1);
         hits.setMax_score(0.5f);
         DocumentResult docRes = docs.values().iterator().next();
         Hit hit = new Hit();
         hit.set_source(docRes.get_source());
         hit.set_index(docRes.get_index());
         hit.set_type(docRes.get_type());
         hit.set_id(docRes.get_id());
         hit.set_score(0.5f);
         hits.setHits(Arrays.asList(hit));
      } else {
         hits.setTotal(0);
      }
      res.setHits(hits);
      res.setTook(took_search);
      return res ;
   }

   @Override
   public SearchResult searchInType(String index, String type, ESQueryMessage queryMessage, String search_type,
         Boolean request_cache, String filter_path) throws ESApiException {
      SearchResult res = new SearchResult();
      res.setTook(took_searchInType);
      return res;
   }

}
