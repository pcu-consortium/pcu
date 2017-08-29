package org.pcu.providers.search.elasticsearch.spi;

import javax.annotation.Resource;

import org.pcu.providers.search.spi.SpiDocument;
import org.pcu.providers.search.spi.PcuSearchProviderApi;
import org.pcu.search.elasticsearch.api.ESApiException;
import org.pcu.search.elasticsearch.api.ElasticSearchApi;
import org.pcu.search.elasticsearch.api.mapping.IndexResult;
import org.springframework.stereotype.Component;

@Component // (@Service rather at application level)
public class ESSearchProviderApiImpl implements PcuSearchProviderApi {
   
   @Resource(name="elasticSearchRestClient") // NOT @Autowired else NoSuchBean because proxy has not the same class https://stackoverflow.com/questions/15614786/could-not-autowire-jaxrs-client
   private ElasticSearchApi esApi;
   
   public ESSearchProviderApiImpl() {
      
   }
   @Override
   public void index(String index, /*String type, */SpiDocument doc) {
      org.pcu.search.elasticsearch.api.Document esDoc = new org.pcu.search.elasticsearch.api.Document();
      esDoc.setProperties(doc.getProperties());
      try {
         if (doc.getId() != null) {
            IndexResult res = esApi.indexDocument(index, doc.getType(), doc.getId(), esDoc, null, null, null, null, null, null, null);
         } else { // generate id (TODO Q support ?)
            IndexResult res = esApi.indexDocument(index, doc.getType(), esDoc, null, null, null, null, null, null, null);
         }
      } catch (ESApiException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
}
