package org.pcu.search.elasticsearch.client;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.pcu.search.elasticsearch.api.ESApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Provider
public class ESApiExceptionMapper implements ExceptionMapper<ESApiException> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ESApiExceptionMapper.class);

   @Autowired @Qualifier("elasticSearchMapper")
   private ObjectMapper mapper;

   @Override
   public Response toResponse(ESApiException e) {
      if (e.getResponse() != null) { // comes from ES client side within server
         return e.getResponse();
      }
      // non-proxied response (thrown in PCU-specific code) :
      String errJson;
      try {
         errJson = mapper.writeValueAsString(e);
         return Response.status(e.getStatus()).entity(errJson).build();
      } catch (JsonProcessingException jpex) {
         LOGGER.error("Jackson error caught writing error JSON", jpex);
         return Response.serverError().entity("Error writing error JSON").build();
      }
   }

}
