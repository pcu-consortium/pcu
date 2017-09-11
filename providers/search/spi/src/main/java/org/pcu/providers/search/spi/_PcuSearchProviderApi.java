package org.pcu.providers.search.spi;

public interface _PcuSearchProviderApi {
   
   /**
    * TODO result
    * @param index
    * @param doc TODO or DataResource/Object ?
    */
   public void index(String index, _SpiDocument doc);
   
}
