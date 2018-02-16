package org.pcu.search.elasticsearch.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * NB. doesn't extend WebApplicationException, because would be RuntimeException
 * and doesn't bring anything beyond storing Response.
 * @author mdutoo
 *
 */
public class ESApiException extends WebApplicationException { /**/
   private static final long serialVersionUID = 4946903083601043828L;
   
   private ESApiError error;
   private int status;
   private String asJsonCache = null;

   /** for deser */
   public ESApiException() {
      super();
   }
   /** TODO build ESApiError from message */
   public ESApiException(String message) {
      super(message);
   }
   /** TODO build ESApiError from message */
   public ESApiException(String message, Throwable cause) {
      super(message, cause);
   }

   /*
   private Response response;
   public ESApiException(String message, Throwable cause, Response response) {
      super(message, cause);
      this.response = response;
   }
   public Response getResponse() {
      return response;
   }
   */
   

   public ESApiException(int status) {
      super(status);
      this.status = status;
   }
   public ESApiException(Response response) {
      super(response);
      this.status = response.getStatus();
   }
   public ESApiException(Status status) {
      super(status);
      this.status = status.getStatusCode();
   }
   public ESApiException(String message, int status) {
      super(message, status);
      this.status = status;
   }
   public ESApiException(String message, Response response) {
      super(message, response);
      this.status = response.getStatus();
   }
   public ESApiException(String message, Status status) {
      super(message, status);
      this.status = status.getStatusCode();
   }
   public ESApiException(String message, Throwable cause, int status) {
      super(message, cause, status);
      this.status = status;
   }
   public ESApiException(String message, Throwable cause, Response response) {
      super(message, cause, response);
      this.status = response.getStatus();
   }
   public ESApiException(String message, Throwable cause, Status status) throws IllegalArgumentException {
      super(message, cause, status);
      this.status = status.getStatusCode();
   }
   public ESApiException(Throwable cause, int status) {
      super(cause, status);
      this.status = status;
   }
   public ESApiException(Throwable cause, Response response) {
      super(cause, response);
      this.status = response.getStatus();
   }
   public ESApiException(Throwable cause, Status status) throws IllegalArgumentException {
      super(cause, status);
      this.status = status.getStatusCode();
   }
   public ESApiException(Throwable cause) {
      super(cause);
   }
   
   
   public ESApiError getError() {
      return error;
   }
   public void setError(ESApiError error) {
      this.error = error;
      this.asJsonCache = null;
   }
   public int getStatus() {
      return status;
   }
   public void setStatus(int status) {
      this.status = status;
      this.asJsonCache = null;
   }

   @Override
   public String getMessage() {
      String msg = super.getMessage();
      if (msg != null) {
         return msg;
      }
      return getAsJson();
   }
   public String getAsJson() {
      return this.asJsonCache;
   }
   public void setAsJson(String errAsJson) {
      this.asJsonCache = errAsJson;
      // TODO generate from error using Jackson ObjectMapper
   }
}
