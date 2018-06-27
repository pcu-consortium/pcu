package org.pcu.platform.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.annotation.PostConstruct;

import org.apache.avro.AvroTypeException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.pcu.providers.search.api.PcuDocument;
import org.pcu.providers.search.api.PcuSearchEsApi;
import org.pcu.search.elasticsearch.api.ESApiException;
import org.pcu.search.elasticsearch.api.mapping.IndexMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


/**
 * Requires ElasticSearch, TODO push ES-specific parts to ES provider
 * TODO refactor to AvroSchemaManager & (ES)PluggableConfBootstrapper ?
 * 
 * @author mardut
 *
 */
@Component
public class ModelServiceImpl {

   protected static final Logger log = LoggerFactory.getLogger(ModelServiceImpl.class);
   
   @Autowired @Qualifier("pcuApiAvroMapper")
   private ObjectMapper pcuApiAvroMapper;
   
   @Autowired @Qualifier("defaultSearchProviderEsApi") //pcuSearchEsApiImpl //pcuSearchEsApiRestClient pcuSearchEsApiImpl
   private PcuSearchEsApi searchEsApi; // PcuSearchEsClientApi
   @Autowired @Qualifier("elasticSearchMapper")
   private ObjectMapper elasticSearchMapper;
   //@Autowired
   //private ResourceLoader resourceLoader;
   private PathMatchingResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver(); // TODO @Bean
   
   private HashMap<String,Schema> fileSchemaMap = new HashMap<String,Schema>();

   /** TODO also fill it from previously uploaded ones, from ES or Kafka */
   private HashMap<String,Schema> typeSchemaMap = new HashMap<String,Schema>();

   private String defaultPcuDocJson;
   private boolean forceBootstrap = true;
   
   @PostConstruct
   public void init() throws JsonParseException, JsonMappingException, IOException {
      // init ES :
      // TODO in spi-impl/ESBootstrapper ?!
      for (Resource esIndexMappingResource : resourceLoader.getResources("classpath*:bootstrap/es/index/mapping/*.json")) {
         IndexMapping indexMapping = elasticSearchMapper.readValue(esIndexMappingResource.getInputStream(), IndexMapping.class);
         String index = esIndexMappingResource.getFilename();
         index = index.substring(0, index.lastIndexOf('.'));

         try {
            LinkedHashMap<String, IndexMapping> existing = searchEsApi.getMapping(index);
            // BEWARE ES can't update mapping, only add new types or fields
            // TODO check if backward compatible, and :
            // - if it is (and only new types or fields), update, save if identical
            // - else in production mode fail, OR auto create another index _[datetime] (up to :
            // fill new index also with existing AND new incoming changes, schedule migration of
            // search API on this new index, and deletion of old index)
            
            if (forceBootstrap) { // delete if already there
            	searchEsApi.deleteMapping(index);
            } else {
               continue;
            }
            
         } catch (ESApiException esex) {
            //if (esex.getAsJson() != null && !esex.getAsJson().contains("index_not_found_exception")) {
            if (esex.getResponse().getStatus() != 404) {
               throw new RuntimeException("Unkown error reiniting mapping of index " + index, esex);
            }
         }
         
         // upload if not yet there or forceBootstrap :
         try {
            searchEsApi.putMapping(index, indexMapping);
         } catch (ESApiException esex) {
            log.error("Failed to update conf of index " + index, esex);
         }
      }
      
      // init meta :
      // TODO check backward compatible, else replace or in production mode fail
      for (Resource avroSchemaResource : resourceLoader.getResources("classpath*:bootstrap/avro/*.avsc")) {
         try (InputStream avroSchemaResourceIs = avroSchemaResource.getInputStream()) {
            Schema schema = new Schema.Parser().parse(avroSchemaResourceIs);
            fileSchemaMap.put(schema.getFullName(), schema); // NOO fullName = "union" !?!!
            for (Schema typeSchema : schema.getTypes()) {
               typeSchemaMap.put(typeSchema.getName(), typeSchema); // TODO fullName
            }
         } catch (IOException ioex) {
            log.error("Failed to load avro schema " + avroSchemaResource.getFilename(), ioex);
         }
      }
      
      // check metamodel vs ES conf consistency :
      // TODO
      
      // setup workaround for avro defaults :
      this.defaultPcuDocJson = this.pcuApiAvroMapper.writeValueAsString(buildDefaultPcuDocument());
   }
   
   /**
    * For now writes pcuDoc to JSON and reads this as avro.
    * LATER don't write to JSON, using custom Jackson Deserializer ?
    * @param pcuDoc
    * @return
    * @throws AvroTypeException
    */
   public GenericRecord validatePcuEntityAgainstAvroSchema(PcuDocument pcuDoc) throws AvroTypeException {
      Schema schema = typeSchemaMap.get(pcuDoc.getType());
      if (schema == null) {
         throw new RuntimeException("Unknown schema type " + pcuDoc.getType());
      }
      try {
         // convert instance to JSON : (TODO skip this step...)
         PcuDocument pcuDocWithDefaults = cloneWithDefaults(pcuDoc);
         pcuApiAvroMapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS); // else double ex. 1512423589.000000000 https://stackoverflow.com/questions/27951124/jackson-java-8-datetime-serialisation
         pcuApiAvroMapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
         String pcuDocumentAvroJson = pcuApiAvroMapper.writeValueAsString(pcuDocWithDefaults); // Jackson should not fail since comes from REST API
         // validate instance :
         DatumReader<GenericRecord> genericDatumReader = new GenericDatumReader<GenericRecord>(schema); // TODO cache
         Decoder decoder = DecoderFactory.get().jsonDecoder(schema, new ByteArrayInputStream(pcuDocumentAvroJson.getBytes()));
         GenericRecord pcuDocRec = genericDatumReader.read(null, decoder); // AvroTypeException
         return pcuDocRec;
         
      } catch (JsonProcessingException ioex) {
         // shouldn't happen since all json to be read has been written from jackson beans
         throw new RuntimeException("JSON error validating schema type " + schema.getType()
               + " of PCU document " + pcuDoc.getId(), ioex);
      } catch (IOException ioex) {
         // shouldn't happen since everything is written in memory
         throw new RuntimeException("IO error validating schema type " + schema.getType()
               + " of PCU document " + pcuDoc.getId(), ioex);
      }
   }

   
   /**
    * NOT USED
    * optional fields workaround :
    * init default pcuDoc with all default values
    * TODO LATER rather from avro schema, and / or using better avro json validation such as https://github.com/allegro/json-avro-converter
    * or jackson to avro https://github.com/FasterXML/jackson-dataformats-binary
    * @return
    */
   private PcuDocument buildDefaultPcuDocument() {
      PcuDocument defaultPcuDoc = new PcuDocument();
      defaultPcuDoc.setByPath("http.url", "");
      defaultPcuDoc.setByPath("http.mimetype", "");
      defaultPcuDoc.setByPath("meta.author", "");
      defaultPcuDoc.setByPath("meta.title", "");
      defaultPcuDoc.setByPath("meta.created", 0l);
      defaultPcuDoc.setByPath("meta.modified", 0l);
      defaultPcuDoc.setByPath("meta.keywords", new ArrayList<String>(0));
      defaultPcuDoc.setByPath("meta.language", "");
      defaultPcuDoc.setByPath("fulltext", "");
      return defaultPcuDoc;
   }
   private PcuDocument cloneWithDefaults(PcuDocument pcuDoc) throws IOException {
      // merge using Jackson : NOO meta.created is removed because not in overriding pcuDoc
      /*
      PcuDocument clonedDefaultPcuDoc = pcuApiAvroMapper.readValue(defaultPcuDocJson, PcuDocument.class); // NOT SerializationUtils.clone(pcuDoc) because doesn't copy version !?
      ObjectReader updater = pcuApiAvroMapper.readerForUpdating(clonedDefaultPcuDoc);
      PcuDocument mergedPcuDoc = updater.readValue(pcuApiAvroMapper.writeValueAsString(pcuDoc));
      */
      PcuDocument mergedPcuDoc = pcuApiAvroMapper.readValue(pcuApiAvroMapper.writeValueAsString(pcuDoc), PcuDocument.class);
      setIfNull(mergedPcuDoc, "http.url", "");
      setIfNull(mergedPcuDoc, "http.mimetype", "");
      setIfNull(mergedPcuDoc, "meta.author", "");
      setIfNull(mergedPcuDoc, "meta.title", "");
      setIfNull(mergedPcuDoc, "meta.created", 0l);
      setIfNull(mergedPcuDoc, "meta.modified", 0l);
      setIfNull(mergedPcuDoc, "meta.keywords", new ArrayList<String>(0));
      setIfNull(mergedPcuDoc, "meta.language", "");
      setIfNull(mergedPcuDoc, "fulltext", "");
      return mergedPcuDoc;
   }
   
   
   private void setIfNull(PcuDocument pcuDoc, String path, Object value) {
      if (pcuDoc.getByPath(path) == null) {
         pcuDoc.setByPath(path, value);
      }
   }
   

   public HashMap<String, Schema> getFileSchemaMap() {
      return fileSchemaMap;
   }

   public HashMap<String, Schema> getTypeSchemaMap() {
      return typeSchemaMap;
   }
   
}
