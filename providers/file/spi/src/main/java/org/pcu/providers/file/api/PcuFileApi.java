package org.pcu.providers.file.api;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.annotations.Api;

/**
 * TODO LATER REST : return mimetype & name in header (requires PcuFileDownloadApi/Impl on top of this and
 * PcuSearchApi which knows it, or BETTER to store it along the content in order not to impact ElasticSearch)
 * 
 * TODO move hash store as another store impl ON TOP of trivial impl (else can append to hashed file !!)
 * TODO Q also type param ?!
 * @author mardut
 *
 */
@Path("/file/api") // extend to override it for alt impl ; TODO Q or /filecpt, /filestore, /blob ?? can be extended on client side, and overloaded by impl (whose value should ONLY be "/" else blocks UI servlet)
@Consumes({MediaType.APPLICATION_OCTET_STREAM})
@Produces({MediaType.APPLICATION_JSON}) // TODO LATER return mimetype
@Api(value = "file api") // name of the api, merely a tag ; not required (only required on impl) 
public interface PcuFileApi {
   
   // TODO Q hash is the logical / business id of content, compute it on client side and use it to link with doc ??
   // TODO Q rather use HDFS API than File in order to support Amazon S3 ???
   /**
    * Unmodifiable content store with hash-based auto dedup, not for append
    * TODO OPT alt with client-side computed hash ??
    * TODO update content entity if any (front API) and (using) event (for further processing ex. tika if not client side)
    * TODO remove if not referred to anymore from content entity ??
    * @param store TODO Q ??
    * @param streamedContent
    * @return file name = hash
    */
   @Path("/content/{store}")
   @POST
   PcuFileResult storeContent(@PathParam("store") String store, InputStream streamedContent);
   /**
    * Modifiable content store - create or replace
    * @param store
    * @param streamedContent
    * @return file name : (original) hash or path, may contain slashes
    */
   @Path("/content/{store}/{path:.+}")
   @PUT
   PcuFileResult putContent(@PathParam("store") String store, @PathParam("path") String path, InputStream streamedContent);
   /**
    * Modifiable content store - append at end or at client-known position using random access (suffices on its own),
    * which allows to build file tailing clients that remember the size already read and themselves
    * read tailed files using random access
    * TODO update content entity if any (front API) ? and / or rather (using) event (for further processing ex. trigger job run if it is time)
    * @param store
    * @param position where to append if any, else at end
    * @param streamedContent
    * @return file name : (original) hash or path, may contain slashes
    */
   @Path("/content/{store}/{path:.+}")
   @POST
   PcuFileResult appendContent(@PathParam("store") String store, @PathParam("path") String path, @QueryParam("path") Long position, InputStream streamedContent);
   
   @GET
   @Path("/content/{store}/{pathOrHash:.+}")
   @Produces({MediaType.APPLICATION_OCTET_STREAM})
   InputStream getContent(@PathParam("store") String store, @PathParam("pathOrHash") String pathOrHash); // TODO not found BUT NOT NotFoundException

   @DELETE
   @Path("/content/{store}/{pathOrHash:.+}")
   PcuFileResult deleteContent(@PathParam("store") String store, @PathParam("pathOrHash") String pathOrHash); // TODO not found BUT NOT NotFoundException
   
   // TODO list / browse SPI ?!
   
}
