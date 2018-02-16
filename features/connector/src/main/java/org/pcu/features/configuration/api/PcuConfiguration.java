package org.pcu.features.configuration.api;

import java.util.Arrays;
import java.util.List;

import org.pcu.search.elasticsearch.api.Document;

/**
 * Inner intermediary abstract field is required by Jackson to achieve polymorphism.
 * @author mardut
 *
 */
public class PcuConfiguration extends Document {
   
   private List<PcuComponent> components;
   
   public PcuConfiguration() {
      
   }
   public PcuConfiguration(PcuComponent... components) {
      this.setComponents(Arrays.asList(components));
   }
   public List<PcuComponent> getComponents() {
      return components;
   }
   public void setComponents(List<PcuComponent> components) {
      this.components = components;
   }
   
}
