package org.pcu.features.search.client;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;

public class CustomObjectWithDateInMapDeserializer extends UntypedObjectDeserializer {
   private static final long serialVersionUID = 7220695760801326144L;

   private DateTimeFormatter format;

   public CustomObjectWithDateInMapDeserializer(DateTimeFormatter format) {
      super(null, null);
      this.format = format;
   }

   @Override
   public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      if (p.getCurrentTokenId() == JsonTokenId.ID_STRING) {
         // TODO also check metamodel (avro schema)
         try {
            String value = p.getText();
            return ZonedDateTime.from(format.parse(value)); // with ZonedDateTime else JsonMappingException: No serializer found for class java.time.format.Parsed and no properties discovered to create BeanSerializer (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS) (through reference chain: org.pcu.search.elasticsearch.api.Document["[anySetter]"]->java.util.LinkedHashMap["file"]->java.util.LinkedHashMap["last_modified"])
         } catch (Exception e) {
            return super.deserialize(p, ctxt);
         }
      } else {
         return super.deserialize(p, ctxt);
      }
   }

}