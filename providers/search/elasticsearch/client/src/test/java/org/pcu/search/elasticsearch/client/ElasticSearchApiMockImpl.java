package org.pcu.search.elasticsearch.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.pcu.search.elasticsearch.api.DeleteByQueryResult;
import org.pcu.search.elasticsearch.api.DeleteResult;
import org.pcu.search.elasticsearch.api.Document;
import org.pcu.search.elasticsearch.api.DocumentResult;
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
import org.pcu.search.elasticsearch.api.query.Hit;
import org.pcu.search.elasticsearch.api.query.Hits;
import org.pcu.search.elasticsearch.api.query.SearchResult;

@Path("/") // else not in swagger ; but NOT "/pcu/search/es" else blocks UI servlet
@Consumes({MediaType.APPLICATION_JSON + "; charset=utf-8"})
@Produces({MediaType.APPLICATION_JSON + "; charset=utf-8"}) // else default tomcat conf produces ISO-8859-1 BUT STILL 500 KO
public class ElasticSearchApiMockImpl implements ElasticSearchApi {
   
   public static final String TEST_ = "test_username";

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
      DocumentResult doc = new DocumentResult();
      doc.set_index(index);
      doc.set_type(type);
      doc.set_id(id);
      doc.set_source(docSource);
      docs.put(id, doc);
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
   public DeleteResult deleteDocument(String index, String type, String id, String routing, String timeout,
         Long version, String parent, String wait_for_active_shards, String refresh) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public DeleteByQueryResult deleteDocumentByQuery(String index, String typePattern, ESQueryMessage query,
         Boolean pretty, String timeout, String refresh, String wait_for_active_shards, Boolean wait_for_completion) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public GetResult getDocument(String index, String type, String id, String routing, Boolean refresh, Long version,
         Boolean realtime, Boolean _source, String preference, String _source_include, String _source_exclude) {
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
   public GetResult getDocumentHead(String index, String type, String id, String routing, Boolean refresh, Long version,
         Boolean realtime, String preference, String _source_include, String _source_exclude) {
      GetResult res = new GetResult();
      res.set_index(index);
      return res;
   }

   @Override
   public Document getDocumentSource(String index, String type, String id, String routing, Boolean refresh,
         Long version, Boolean realtime, String preference, String _source_include, String _source_exclude) {
      return docs.get(id).get_source();
   }

   @Override
   public SearchResult search(ESQueryMessage queryMessage, String search_type, Boolean request_cache) {
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
      return res ;
   }

   @Override
   public SearchResult search(ESQueryMessage queryMessage, String search_type, Boolean request_cache,
         String filter_path) throws ESApiException {
      return null;
   }

}
