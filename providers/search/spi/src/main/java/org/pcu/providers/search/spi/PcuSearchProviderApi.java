package org.pcu.providers.search.spi;

public interface PcuSearchProviderApi {
   
   /**
    * TODO result
    * @param index
    * @param doc TODO or DataResource/Object ?
    */
   public void index(String index, SpiDocument doc);
   
}
