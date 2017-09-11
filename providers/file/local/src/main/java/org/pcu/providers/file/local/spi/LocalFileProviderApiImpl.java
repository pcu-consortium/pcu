package org.pcu.providers.file.local.spi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.pcu.providers.file.api.PcuFileApi;
import org.pcu.providers.file.api.PcuFileResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * TODO move hash store as another store impl ON TOP of trivial impl (else can append to hashed file !!)
 * @author mardut
 *
 */
@Component // (@Service rather at application level)
public class LocalFileProviderApiImpl /*extends PcuJaxrsServerBase */implements PcuFileApi {
   
   private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileProviderApiImpl.class);

   private String storeRootPath = "/tmp/pcu_store";
   private File storeRootDir = new File(storeRootPath);
   
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
      // NB. set its customTempDir to the same fs as the final file
      // (so its bytes won't be copied, even with glusterfs http://blog.vorona.ca/the-way-gluster-fs-handles-renaming-the-file.html )
      // or accept temp place (make it big enough) and bandwidth costs of moving it
      File customTempDir = null; // TODO on same FS ex. gluster
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
         tmpFile.renameTo(contentFile);
      }
      
      PcuFileResult res = new PcuFileResult();
      res.setPath(digestHexString);
      return res;
   }

   @Override
   public PcuFileResult putContent(String store, String path, InputStream streamedContent) {
      saveContent(store, path, streamedContent, false);

      PcuFileResult res = new PcuFileResult();
      res.setPath(path);
      return res;
   }

   @Override
   public PcuFileResult appendContent(String store, String path, InputStream streamedContent) {
      saveContent(store, path, streamedContent, true);

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
   
   private void saveContent(String store, String path, InputStream streamedContent, boolean append) {
      File contentFile = getContentFile(store, path);
      
      try (FileOutputStream contentFileOs = new FileOutputStream(contentFile, append)) { // auto creates and append
         IOUtils.copy(streamedContent, contentFileOs); // TODO apache commons (same), buffer size
      } catch (IOException ioex) {
         throw new RuntimeException("Can't store content, IO error streaming HTTP to temp file", ioex); // TODO better
      } finally { // (try with resource would not allow silent log)
         try { streamedContent.close(); } catch (IOException e) {
            LOGGER.info("error closing streamedContent AFTER reading it", e);
         }
      }
   }
   
   private File getContentFile(String store, String pathOrHash) {
      return new File(storeRootDir, pathOrHash);
   }
   private Path getContentPath(String store, String pathOrHash) {
      return Paths.get(storeRootPath, pathOrHash);
   }
   
}
