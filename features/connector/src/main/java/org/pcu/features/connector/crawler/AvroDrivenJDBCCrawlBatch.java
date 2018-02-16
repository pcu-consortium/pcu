package org.pcu.features.connector.crawler;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.pcu.providers.search.api.PcuDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A JDBCCrawlBatch that uses an Avro schema to specify which fields (and their types) should be copied
 * from the SQL query results into the converted PCU document.
 * Avro schema can be written manually, or generated from SQL (using ex. a Spark job).
 * Avro schema name is used as PCU document type.
 * TODO parse schema (see test), put in schema API (modelService), put mapping in ElasticSearch after converted from avro schema.
 * @author mardut
 *
 */
public class AvroDrivenJDBCCrawlBatch extends JDBCCrawlBatchBase {

   protected static final Logger log = LoggerFactory.getLogger(AvroDrivenJDBCCrawlBatch.class);
   
   /** used to ser & deser avroSchema, is its ref in the modelService
    * (which is FOR now readonly & filled from classpath) */
   private String avroSchemaName;
   /** only cache */
   @JsonIgnore
   private Schema avroSchema = null;

   /** for REST ser only */
   public AvroDrivenJDBCCrawlBatch() {
      super(null);
   }

   public AvroDrivenJDBCCrawlBatch(Crawler2 crawler) {
      super(crawler);
   }

   protected PcuDocument mapToPcuDocument(ResultSet rs, long rowNum) throws SQLException {
      PcuDocument pcuDoc = new PcuDocument();
      // TODO LATER also which index
      pcuDoc.setType(getAvroSchema().getName());
      Object id = null;
      
      for (Field field : getAvroSchema().getFields()) {
         String fieldName = field.name();
         if (fieldName.equals("type")) {
            // for now doc type has to be defined in the avro schema
            continue;
         }
         if (fieldName.equals(this.getIdFieldName())) {
            id = pcuDoc.getProperties().get(this.getIdFieldName());
            continue;
         }
         Object value = null;
         switch (field.schema().getName()) {
         case "long" :
            value = rs.getLong(fieldName);
            // TODO date
            break;
         case "string" :
            value = rs.getString(fieldName);
            break;
         // TODO others : date (long with annotation dateMillis)...
         default :
            log.error("mapping not implemented yet for field type " + field.schema().getName());
         }
         if (value != null) {
            pcuDoc.setProperty(fieldName, value);
         }
      }

      // setting id : (allows not to create new entries on next polling job)
      if (id != null) {
         pcuDoc.setId(id + "");
      }
      
      // TODO OPT content if any (ex. ECM case) : download and upload, parse / extract and enrich pcuDoc with meta (including content store path)
      
      return pcuDoc;
   }

   /** if no cachedAvroSchema yet, lazy inits it from avroSchemaName by lookup in modelService */
   public Schema getAvroSchema() {
      if (this.avroSchema == null) {
         this.avroSchema = this.crawler.getConnector().getModelService().getTypeSchemaMap().get(this.avroSchemaName);
         if (this.avroSchema == null) {
            String msg = "Can't find avro schema " + this.avroSchemaName
                  + " in modelService. Maybe the name is wrong, or its file is not in bootstrap/avro in the classpath";
            log.error(msg);
            throw new RuntimeException(msg); // otherwise NullPointerException in mapToPcuDocument() 
         }
      }
      return this.avroSchema;
   }
   /** only use for tests, otherwise rater setAvroSchemaName */
   public void setCachedAvroSchema(Schema avroSchema) {
      this.avroSchema = avroSchema;
   }

   public String getAvroSchemaName() {
      return avroSchemaName;
   }
   public void setAvroSchemaName(String avroSchemaName) {
      this.avroSchemaName = avroSchemaName;
   }
   
}
