package org.pcu.features.search.pipeline;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.pcu.features.search.pipeline.kafka.KafkaProducerFactory;
import org.pcu.features.search.pipeline.kafka.ProducerKafka;
import org.pcu.platform.model.ModelServiceImpl;
import org.pcu.platform.rest.server.PcuJaxrsServerBase;
import org.pcu.providers.search.api.PcuDocument;
import org.pcu.providers.search.api.PcuIndexResult;
import org.pcu.providers.search.api.PcuSearchApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.swagger.annotations.Api;


/**
 * Implements PCU indexing on top of PCU pipeline (Spark Streaming filled by Kafka).
 * (CRUD & search operations are only dummy impls)
 * Can be used through REST on its own path, or configured to be used by the default PCU search impl.
 * 
 * @author mardut
 *
 */
@Path("/pipeline") // TODO Q or /searchcpt, /data, /nosql ?? can be extended on client side, and overloaded by impl (whose value should ONLY be "/" else blocks UI servlet)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Api("search index pipeline") // name of the api, merely a tag ; else not in swagger
@Service // for what, or only @Component ?
public class PcuSearchApiPipelineImpl extends PcuJaxrsServerBase implements PcuSearchIndexPipelineApi, PcuSearchApi {

   @Autowired
   private ModelServiceImpl modelService;
   @Autowired
   private ProducerKafka producerKafka;
   private Producer<String, String> kafkaProducer;
   

   @Override
   public PcuIndexResult index(String index, PcuDocument pcuDoc) {
      // check model schema :
      modelService.validatePcuEntityAgainstAvroSchema(pcuDoc);
      
      // send to Kafka :
      try {
         String topic = pcuDoc.getType(); // and not index, because (without more conf) inAndOut gets schema from topic
         RecordMetadata kafkaDocRes = producerKafka.runProducer(kafkaProducer, topic, pcuDoc, pcuDoc.getId());
         // TODO robustness
         PcuIndexResult res = new PcuIndexResult();
         // TODO res doc info :
         // res.setId(pcuDoc.getId()); // & index (topic), type, version ?!
         // TODO res kafka info :
         //res.setPipelineTimestamp(kafkaDocRes.timestamp()); // & key, offset, partition ?
         return res ;
      } catch (Exception e) {
         throw new RuntimeException("Error sending indexation to kafka of pcuDoc " + pcuDoc.getId(), e);
      }
   }
   
   @PostConstruct
   protected void init() {
      kafkaProducer = KafkaProducerFactory.createProducer();
      // TOOD create topics according to (enabled) ES indexes, more...
      // TODO load avro models
      // TODO setup Spark Streaming job
   }

   @Override
   public PcuDocument get(String index, String docId) {
      // NOO meaningless
      return null;
   }

}
