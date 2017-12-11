package org.pcu.providers.file.spi.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.pcu.providers.file.api.PcuFileApi;
import org.pcu.providers.file.api.PcuFileResult;

//@Path("/") // else "not found" because overrides // else not in swagger ; but NOT "/pcu/search/es" else blocks UI servlet
@Consumes({MediaType.APPLICATION_JSON + "; charset=utf-8"})
@Produces({MediaType.APPLICATION_JSON + "; charset=utf-8"}) // else default tomcat conf produces ISO-8859-1 BUT STILL 500 KO
public class PcuFileApiMockImpl implements PcuFileApi {
   
   public static final String TEST_PATH = "test_path";
   
   public static String storedContent;

   @Override
   public PcuFileResult storeContent(String store, InputStream streamedContent) {
      try {
         this.storedContent = IOUtils.toString(streamedContent, (Charset) null);
      } catch (IOException ioex) {
         throw new RuntimeException(ioex);
      }
      PcuFileResult res = new PcuFileResult();
      res.setPath(TEST_PATH);
      return res ;
   }

   @Override
   public PcuFileResult putContent(String store, String path, InputStream streamedContent) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public PcuFileResult appendContent(String store, String path, Long position, InputStream streamedContent) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public InputStream getContent(String store, String pathOrHash) {
      return new ByteArrayInputStream(storedContent.getBytes());
   }

   @Override
   public PcuFileResult deleteContent(String store, String pathOrHash) {
      // TODO Auto-generated method stub
      return null;
   }


}
