package org.pcu.providers.file.local.spi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.pcu.platform.rest.server.PcuJaxrsServerBase;
import org.pcu.providers.file.api.PcuFileApi;
import org.pcu.providers.file.api.PcuFileResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.swagger.annotations.Api;

/**
 * PCU File content API.
 * 
 * TODO REST : return mimetype & name (...)
 * 
 * TODO move hash store as another store impl ON TOP of trivial impl (else can append to hashed file !!)
 * TODO manage stores
 * @author mardut
 *
 */
@javax.ws.rs.Path("/file/api") // extend to override it for alt impl ; TODO Q or /filecpt, /filestore, /blob ?? can be extended on client side, and overloaded by impl (whose value should ONLY be "/" else blocks UI servlet)
@Api(value = "file api") // name of the api, merely a tag ; not required (only required on impl) 
@Service("defaultFileProviderApi") // @Component // (@Service rather at application level) HOW TO INJECT : SECURITY MAPPING CHECK & CRITERIA, SCHEMA CHECK ?? EVEN IN MODELSERVICE ??
public class LocalFileProviderApiImpl extends PcuJaxrsServerBase implements PcuFileApi {
   
   private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileProviderApiImpl.class);

   @Autowired @Value("${pcu.file.local.storeRootPath:/tmp/pcu_store}")
   private String storeRootPath = "/tmp/pcu_store";
   private File storeRootDir;
   // temp file dir. NB. set it to the same fs as the final file
   // (so its bytes won't be copied, even with glusterfs http://blog.vorona.ca/the-way-gluster-fs-handles-renaming-the-file.html )
   // or accept temp place (make it big enough) and bandwidth costs of moving it
   private File customTempDir; // TODO on same FS ex. gluster
   
   public LocalFileProviderApiImpl() {
      
   }

   
   @PostConstruct
   void init() {
      // TODO check :
      storeRootDir = new File(storeRootPath);
      if (!storeRootDir.exists()) {
         storeRootDir.mkdirs();
      }
      customTempDir = new File(storeRootDir, "tmp"); // in SAME partition else rename (move) fails silently
      if (!customTempDir.exists()) {
         customTempDir.mkdirs();
      }
      LOGGER.info("storeRoot is " + storeRootDir.getAbsolutePath());
   }

   @Override
   public PcuFileResult storeContent(String store, InputStream streamedContent) {
      /*
      FileStore fileStore = getFileStore(store);
      fileStore.save(streamedContent);
      */

      // create temp file : (since name = business id i.e. hash not yet known)
      File tmpFile;
      try {
         tmpFile = File.createTempFile("pcu_store_tmp_", ".bin", customTempDir);
      } catch (IOException ioex) {
         throw new RuntimeException("Can't store content, error creating temp file", ioex); // TODO
      }
      // compute digest while storing :
      // NB. don't compute other props, because are handled in dedicated metadata providers (ex. file, which provides ex. size without additional cost)
      MessageDigest contentMessageDigest;
      try {
         contentMessageDigest = MessageDigest.getInstance("MD5");
      } catch (NoSuchAlgorithmException nsaex) {
         throw new RuntimeException("Can't store content, error initing hash / digest", nsaex); // TODO
      }
      try (FileOutputStream tmpFileOs = new FileOutputStream(tmpFile)) { // NB. buffering done on our own and by CXF BinaryDataProvider (IOUtils)
         byte[] buf = new byte[16*1024];
         int readNb;
         while ((readNb = streamedContent.read(buf))> 0) {
            contentMessageDigest.update(buf, 0, readNb);
            tmpFileOs.write(buf, 0, readNb);
         }
      } catch (IOException ioex) {
         throw new RuntimeException("Can't store content, IO error streaming HTTP to temp file", ioex); // TODO
      } finally { // (try with resource would not allow silent log)
         try { streamedContent.close(); } catch (IOException e) {
            e.printStackTrace(); // TODO log
         }
      }
      BigInteger digestBigInt = new BigInteger(1, contentMessageDigest.digest());
      String digestHexString = digestBigInt.toString(16);
      
      // rename to business id i.e. hash / digest :
      File contentFile = getContentFile(store, digestHexString);
      if (!contentFile.exists()) {
         contentFile.getParentFile().mkdirs(); // else renameTo() fails silently (returns false)
         tmpFile.renameTo(contentFile);
      } else {
         tmpFile.delete();
      }
      
      PcuFileResult res = new PcuFileResult();
      res.setPath(digestHexString);
      return res;
   }

   @Override
   public PcuFileResult putContent(String store, String path, InputStream streamedContent) {
      saveContent(store, path, streamedContent, 0l);

      PcuFileResult res = new PcuFileResult();
      res.setPath(path);
      return res;
   }

   @Override
   public PcuFileResult appendContent(String store, String path, Long position, InputStream streamedContent) {
      if (position == null) {
         saveContent(store, path, streamedContent, null);
         
      } else {
         try (ByteArrayOutputStream contentBos = new ByteArrayOutputStream()) { // auto creates and append
            IOUtils.copy(streamedContent, contentBos); // TODO buffer size
            RandomAccessFile raf = new RandomAccessFile(getContentFile(store, path), "rw");
            raf.seek(position);
            raf.write(contentBos.toByteArray());
            raf.close();
         } catch (IOException ioex) {
            throw new RuntimeException("Can't store content, IO error streaming HTTP to file", ioex); // TODO better
         } finally { // (try with resource would not allow silent log)
            try { streamedContent.close(); } catch (IOException e) {
               LOGGER.info("error closing streamedContent AFTER reading it", e);
            }
         }
      }

      PcuFileResult res = new PcuFileResult();
      res.setPath(path);
      return res;
   }

   @Override
   public InputStream getContent(String store, String pathOrHash) {
      File contentFile = getContentFile(store, pathOrHash);
      try {
         return new FileInputStream(contentFile);
      } catch (FileNotFoundException fnfex) {
         //throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
         //      .entity("Can't find store/content " + store + "/" + pathOrHash)
         //      .type(cxfJaxrsApiProvider.getNegotiatedResponseMediaType()).build()); // NOT JAXRS NotFoundException else ClassNotFoundException: org.glassfish.jersey.internal.RuntimeDelegateImpl
         throw new RuntimeException("Can't find store/content " + store + "/" + pathOrHash); // TODO better ; 
      }
   }

   @Override
   public PcuFileResult deleteContent(String store, String pathOrHash) {
      if (pathOrHash.startsWith("/")) {
         throw new RuntimeException("pathOrHash must not start by /");
      }
      try {
         Files.deleteIfExists(getContentPath(store, pathOrHash)); // NB. failing if not exist would not be REST
         PcuFileResult res = new PcuFileResult();
         res.setPath(pathOrHash);
         return res ;
      } catch (IOException fnfex) {
         throw new RuntimeException("Can't delete store/content " + store + "/" + pathOrHash); // TODO better ; NOT JAXRS NotFoundException else ClassNotFoundException: org.glassfish.jersey.internal.RuntimeDelegateImpl
      }
   }
   
   private void saveContent(String store, String path, InputStream streamedContent, Long position) {
      File contentFile = getContentFile(store, path);
      contentFile.getParentFile().mkdirs(); // else FileNotFoundExeption

      if (position != null && position > 0) {
         // append using random access :
         try (ByteArrayOutputStream contentBos = new ByteArrayOutputStream()) {
            IOUtils.copy(streamedContent, contentBos); // TODO buffer size
            RandomAccessFile raf = new RandomAccessFile(getContentFile(store, path), "rw"); // creates file
            raf.seek(position);
            raf.write(contentBos.toByteArray());
            raf.close();
         } catch (IOException ioex) {
            throw new RuntimeException("Can't store content, IO error streaming HTTP to file", ioex); // TODO better
         } finally { // (try with resource would not allow silent log)
            try { streamedContent.close(); } catch (IOException e) {
               LOGGER.info("error closing streamedContent AFTER reading it", e);
            }
         }
         
      } else {
         // create or append at the end :
         boolean append = position == null; // else 0
         try (FileOutputStream contentFileOs = new FileOutputStream(contentFile, append)) { // auto creates file and append
            IOUtils.copy(streamedContent, contentFileOs); // TODO buffer size
         } catch (IOException ioex) {
            throw new RuntimeException("Can't store content, IO error streaming HTTP to file", ioex); // TODO better
         } finally { // (try with resource would not allow silent log)
            try { streamedContent.close(); } catch (IOException e) {
               LOGGER.info("error closing streamedContent AFTER reading it", e);
            }
         }
      }
   }
   
   
   //////////////////////////////////////////
   // ADMIN ONLY

   /**
    * admin only (or check rights)
    * allows ex. to prune unreferenced content after using their metadata in the entity store
    * @param store
    * @return
    * @throws IOException 
    */
   public List<String> listContentPathes(String store) throws IOException {
      Path storePath = Paths.get(getStorePath(store));
      return Files.walk(storePath).filter(Files::isRegularFile).map(Path::toString).collect(Collectors.toList());
   }

   /**
    * admin only
    * only for cleanup, ex. for tests or to prune unreferenced content after having identified
    * them using listContentPathes and their metadata in the entity store
    * @param store
    * @throws IOException 
    */
   public void deleteStore(String store) throws IOException {
      Path storePath = Paths.get(getStorePath(store));
      try {
         Files.walkFileTree(storePath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
         });
      } catch (NoSuchFileException e) {
         // silent
      }
   }
   
   /** public for tests */
   public File getContentFile(String store, String pathOrHash) {
      return new File(getStorePath(store), pathOrHash);
   }
   private Path getContentPath(String store, String pathOrHash) {
      return Paths.get(getStorePath(store), pathOrHash);
   }
   private String getStorePath(String store) {
      return storeRootPath + File.separatorChar + store;
   }


   /**
    * TODO LATER update doc meta on content change :
    * async notify or sync index, called in store/put/appendContent
    * NOO rather client side !
    * @param path
    * @param index
    * @param docId
    * @param digestHexString
    */
   private void handleContentChanged(String path, String index, String docId, String digestHexString) {
      /*
      // notify :
      kafkaEventSystem.sendEvent("store.content.changed", path, digestHexString, index, docId);
      
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
      
      esSearchProviderApi.index(index, pcuDoc); // TODO sync ; ideally patch, require that exists ??
      */
   }
   
}
