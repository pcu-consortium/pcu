package org.pcu.search.elasticsearch.binding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.pcu.search.elasticsearch.api.BulkAction;
import org.pcu.search.elasticsearch.api.BulkMessage;
import org.pcu.search.elasticsearch.api.Document;
import org.pcu.search.elasticsearch.api.IndexAction;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class BulkMessageDeserializer extends StdDeserializer<BulkMessage> {
   private static final long serialVersionUID = -3805551809428655328L;
   
   private static class KindToActionTypeReference extends TypeReference<LinkedHashMap<String, IndexAction>> {

   };
   private static final TypeReference<?> KIND_TO_ACTION_TYPE = new KindToActionTypeReference();
   
   public BulkMessageDeserializer() {
      super(BulkMessage.class);
   }

   @Override
   public BulkMessage deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      BulkMessage bulkMessage = new BulkMessage();
      List<BulkAction> bulkActions = new ArrayList<BulkAction>();
      bulkMessage.setActions(bulkActions);
      for (;;) {
         BulkAction bulkAction = new BulkAction();
         try {
            bulkAction.setKindToAction(p.readValueAs(KIND_TO_ACTION_TYPE));
         } catch (JsonMappingException jmex) {
            if (p.isClosed()) { // end of content has been reached, regular return case
               return bulkMessage;
            }
            throw jmex;
         }
         bulkAction.setDoc(p.readValueAs(Document.class));
         bulkActions.add(bulkAction);
      }
   }
   
}
