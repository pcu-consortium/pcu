package org.pcu.search.elasticsearch.client;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.client.ResponseExceptionMapper;
import org.pcu.search.elasticsearch.api.ESApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Provider
public class ESApiResponseExceptionMapper implements ResponseExceptionMapper<ESApiException> {
   
   @Autowired @Qualifier("elasticSearchMapper")
   private ObjectMapper mapper;

   @Override
   public ESApiException fromResponse(Response r) {
      try {
         String errMsg = IOUtils.toString((InputStream) r.getEntity());
         try {
            ESApiException ex = mapper.readValue(errMsg, ESApiException.class);
            ex.setAsJson(errMsg); // to help debugging
            return ex;
         } catch (IOException ioex) {
            throw new RuntimeException("Can't parse REST error JSON from " + errMsg, ioex);
         }
      } catch (IOException ioex) {
         throw new RuntimeException("Can't read REST error response stream", ioex);
      }
   }

}
