package org.pcu.search.elasticsearch.api;

import java.util.List;

import org.pcu.search.elasticsearch.binding.BulkMessageDeserializer;
import org.pcu.search.elasticsearch.binding.BulkMessageSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using=BulkMessageSerializer.class)
@JsonDeserialize(using=BulkMessageDeserializer.class)
public class BulkMessage {
   
   private List<BulkAction> actions;

   public List<BulkAction> getActions() {
      return actions;
   }
   public void setActions(List<BulkAction> actions) {
      this.actions = actions;
   }

}
