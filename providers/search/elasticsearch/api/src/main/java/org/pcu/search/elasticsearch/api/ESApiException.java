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

   public ESApiException(String message) {
      super(message);
   }
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
   }

   public int getStatus() {
      return status;
   }

   public void setStatus(int status) {
      this.status = status;
   }

   public static long getSerialversionuid() {
      return serialVersionUID;
   }
   
}
