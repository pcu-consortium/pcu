package org.pcu.features.search.simple;

import java.io.InputStream;
import java.util.LinkedHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.pcu.platform.rest.server.PcuJaxrsServerBase;
import org.pcu.providers.file.api.PcuFileApi;
import org.pcu.providers.file.api.PcuFileResult;
import org.pcu.providers.search.api.PcuDocument;
import org.pcu.providers.search.api.PcuSearchApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.swagger.annotations.Api;

/**
 * TODO NOO rather client side !!!!!!!!!!!!!!!!!!!!!!!!!
 * meaningless, rather directly use LocalFileProviderApiImpl
 * @author mardut
 *
 */
@Path("/file/api") // TODO Q or /searchcpt, /data, /nosql ?? can be extended on client side, and overloaded by impl (whose value should ONLY be "/" else blocks UI servlet)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Api("file api") // name of the api, merely a tag ; else not in swagger
@Service // for what, or only @Component ?
public class PcuFileApiSimpleImpl extends PcuJaxrsServerBase implements PcuFileApi {

   //@Autowired
   private PcuSearchApi esSearchProviderApi;
   @Autowired
   private PcuFileApi localFileProviderApi;
   
   @Override
   public PcuFileResult storeContent(String store, InputStream streamedContent) {
      PcuFileResult res = localFileProviderApi.storeContent(store, streamedContent);

      // TODO update content entity and / using event :
      String index = "files";
      String docId = "myfile.doc";
      ///handleContentChanged(res.getPath(), index, docId, null);
      return res;
   }

   @Override
   public PcuFileResult putContent(String store, String path, InputStream streamedContent) {
      PcuFileResult res = localFileProviderApi.putContent(store, path, streamedContent);

      // TODO update content entity and / using event :
      String index = "files";
      String docId = "myfile.doc";
      ///handleContentChanged(path, index, docId, null);
      return res;
   }

   @Override
   public PcuFileResult appendContent(String store, String path, Long position, InputStream streamedContent) {
      PcuFileResult res = localFileProviderApi.appendContent(store, path, position, streamedContent);

      // TODO update content entity and / using event :
      String index = "files";
      String docId = "myfile.doc";
      ///handleContentChanged(path, index, docId, null);
      return res;
   }

   @Override
   public InputStream getContent(String store, String pathOrHash) {
      return localFileProviderApi.getContent(store, pathOrHash);
   }

   @Override
   public PcuFileResult deleteContent(String store, String pathOrHash) {
      return localFileProviderApi.deleteContent(store, pathOrHash);
   }

   // TODO NOO rather client side !!!!!!!!!!!!!!!!!!!!!!!!!
   private void handleContentChanged(String path, String index, String docId, String digestHexString) {
      // notify :
      ///kafkaEventSystem.sendEvent("store.content.changed", path, digestHexString, index, docId);
      
      // update entity if required : (in event ??)
      // NB. a single entity could have several contents by having more path props
      //if (docId != null) {
      PcuDocument pcuDoc = new PcuDocument();
      pcuDoc.setId(docId);
      // set props :
      // TODO rather using PcuContentProps object model ? or JSON-LD ??
      pcuDoc.setProperties(new LinkedHashMap<>(3));
      //pcuDoc.getProperties().put("content.path", path); // to refer to it
      //if (digestHexString != null) pcuDoc.getProperties().put("content.hash", digestHexString); // if any, same alt way to refer to it
      LinkedHashMap<String, Object> contentProps = new LinkedHashMap<>(3);
      pcuDoc.getProperties().put("content", contentProps);
      contentProps.put("path", path); // to refer to it
      if (digestHexString != null) contentProps.put("hash", digestHexString); // if any, same alt way to refer to it
      // TODO OPT could plug other server-side metadata provider here ex. file, tika ??
      esSearchProviderApi.index(index, pcuDoc); // TODO sync, ideally patch, require exists ??
   }

}
