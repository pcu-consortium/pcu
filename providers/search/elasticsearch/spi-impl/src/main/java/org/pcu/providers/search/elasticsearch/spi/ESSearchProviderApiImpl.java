package org.pcu.providers.search.elasticsearch.spi;

import java.util.LinkedHashMap;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.pcu.platform.rest.server.PcuJaxrsServerBase;
import org.pcu.providers.search.api.PcuDocument;
import org.pcu.providers.search.api.PcuIndexResult;
import org.pcu.providers.search.api.PcuSearchApi;
import org.pcu.search.elasticsearch.api.ESApiException;
import org.pcu.search.elasticsearch.api.ElasticSearchApi;
import org.pcu.search.elasticsearch.api.GetResult;
import org.pcu.search.elasticsearch.api.mapping.IndexResult;
import org.springframework.stereotype.Service;

import io.swagger.annotations.Api;

@Path("/search/api") // TODO Q or /searchcpt, /data, /nosql ?? can be extended on client side, and overloaded by impl (whose value should ONLY be "/" else blocks UI servlet)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Api("search api") // name of the api, merely a tag ; else not in swagger
@Service("defaultSearchProviderApi") // for what, or only @Component ? HOW TO INJECT SECURITY MAPPING CHECK & CRITERIA ?? model check ?
//@Component // (@Service rather at application level)
public class ESSearchProviderApiImpl extends PcuJaxrsServerBase implements PcuSearchApi {
   
   @Resource(name="elasticSearchRestClient") // NOT @Autowired else NoSuchBean because proxy has not the same class https://stackoverflow.com/questions/15614786/could-not-autowire-jaxrs-client
   private ElasticSearchApi esApi;
   
   public ESSearchProviderApiImpl() {
      
   }
   
   @Override
   public PcuIndexResult index(String index, /*String type, */PcuDocument doc) {
      org.pcu.search.elasticsearch.api.Document esDoc = new org.pcu.search.elasticsearch.api.Document();
      esDoc.setProperties(doc.getProperties());
      try {
         IndexResult res;
         // TODO rather unify using @BeanParam :
         if (doc.getId() != null) {
            res = esApi.indexDocument(index, doc.getType(), doc.getId(), esDoc, null, null, null, null, null, null, null);
         } else { // generate id (TODO Q support ?)
            res = esApi.indexDocument(index, doc.getType(), esDoc, null, null, null, null, null, null, null);
         }
         PcuIndexResult pcuRes = new PcuIndexResult();
         pcuRes.setCreated(res.isCreated());
         pcuRes.setVersion(res.get_version());
         return pcuRes;
      } catch (ESApiException e) {
         throw new RuntimeException("Error calling ElasticSearch", e); // TODO better (convert in ExceptionWrapper)
      }
   }

   @Override
   public PcuDocument get(String index, String docId) {
      Long version = null; // TODO optimistic locking, from ETag ; other ES features ?
      try {
         GetResult res = esApi.getDocument(index, "_all", // TODO if used through REST would be auto default
               docId, null, null, version, null, null, null, null, null);
         PcuDocument pcuDoc = new PcuDocument();
         pcuDoc.setId(docId);
         pcuDoc.setVersion(res.get_version());
         pcuDoc.setType(res.get_type());
         // NB. no raw
         pcuDoc.setProperties(new LinkedHashMap<>(res.get_source().getProperties())); // copy
         // TOOD TODO set other (extracted) metas / prop groups ACCORDING TO METADATA
         //pcuDoc.setMetadataGroups(metadataGroups);
         return pcuDoc ;
      } catch (ESApiException e) {
         throw new RuntimeException("Error calling ElasticSearch", e); // TODO better (convert in ExceptionWrapper)
      }
   }
   
}
