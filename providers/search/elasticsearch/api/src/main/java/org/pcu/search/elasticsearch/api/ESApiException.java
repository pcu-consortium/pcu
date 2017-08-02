package org.pcu.search.elasticsearch.api;

/**
 * NB. doesn't extend WebApplicationException, because would be RuntimeException
 * and doesn't bring anything beyond storing Response.
 * @author mdutoo
 *
 */
public class ESApiException extends /*WebApplication*/Exception {
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
