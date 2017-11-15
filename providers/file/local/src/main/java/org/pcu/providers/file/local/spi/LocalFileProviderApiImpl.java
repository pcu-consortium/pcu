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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.pcu.platform.rest.server.PcuJaxrsServerBase;
import org.pcu.providers.file.api.PcuFileApi;
import org.pcu.providers.file.api.PcuFileResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Consumes({MediaType.APPLICATION_OCTET_STREAM})
@Produces({MediaType.APPLICATION_OCTET_STREAM}) // TODO LATER return mimetype & name
@Api(value = "file api") // name of the api, merely a tag ; not required (only required on impl) 
@Service // @Component // (@Service rather at application level)
public class LocalFileProviderApiImpl extends PcuJaxrsServerBase implements PcuFileApi {
   
   private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileProviderApiImpl.class);

   private String storeRootPath = "/tmp/pcu_store";
   private File storeRootDir = new File(storeRootPath);
   // temp file dir. NB. set it to the same fs as the final file
   // (so its bytes won't be copied, even with glusterfs http://blog.vorona.ca/the-way-gluster-fs-handles-renaming-the-file.html )
   // or accept temp place (make it big enough) and bandwidth costs of moving it
   private File customTempDir = null; // TODO on same FS ex. gluster
   
   public LocalFileProviderApiImpl() {
      
   }

   
   @PostConstruct
   void init() {
      // TODO check :
      if (!storeRootDir.exists()) {
         storeRootDir.mkdirs();
      }
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
         throw new RuntimeException("Can't find content " + pathOrHash); // TODO better ; NOT JAXRS NotFoundException else ClassNotFoundException: org.glassfish.jersey.internal.RuntimeDelegateImpl
      }
   }

   @Override
   public PcuFileResult deleteContent(String store, String pathOrHash) {
      try {
         Files.deleteIfExists(getContentPath(store, pathOrHash)); // NB. failing if not exist would not be REST
         PcuFileResult res = new PcuFileResult();
         res.setPath(pathOrHash);
         return res ;
      } catch (IOException fnfex) {
         throw new RuntimeException("Can't delete content " + pathOrHash); // TODO better ; NOT JAXRS NotFoundException else ClassNotFoundException: org.glassfish.jersey.internal.RuntimeDelegateImpl
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
   
}
