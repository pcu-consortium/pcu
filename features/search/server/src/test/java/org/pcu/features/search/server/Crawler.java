package org.pcu.features.search.server;

public class Crawler {
   private String contentStore;
   private String index;
   //private String type;
   private String connectorComputerName;
   private String connectorComputerId;
   public String getContentStore() {
      return contentStore;
   }
   public void setContentStore(String contentStore) {
      this.contentStore = contentStore;
   }
   public String getIndex() {
      return index;
   }
   public void setIndex(String index) {
      this.index = index;
   }
   public String getConnectorComputerName() {
      return connectorComputerName;
   }
   public void setConnectorComputerName(String connectorComputerName) {
      this.connectorComputerName = connectorComputerName;
   }
   public String getConnectorComputerId() {
      return connectorComputerId;
   }
   public void setConnectorComputerId(String connectorComputerId) {
      this.connectorComputerId = connectorComputerId;
   }
}