package org.pcu.features.connector.crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileCrawlBatch extends ItemCrawlBatch<File> {

   /** used for (de)ser */
   private String lastCrawledFilePath;
   /** only cache & for tests */
   private File lastCrawled;
   private File previousPageLastCrawled = null;

   /** for REST ser only */
   public FileCrawlBatch() {
      super(null);
   }

   public FileCrawlBatch(File lastCrawled, Crawler2 crawler) {
      super(crawler);
      this.lastCrawled = lastCrawled;
   }

   public List<File> nextPageItems() {
      /*
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
      */
      File fileLastCrawled = getLastCrawled();
      if (previousPageLastCrawled == fileLastCrawled) { // dummy check ensuring a single call to nextPageItems()
         return null;
      }
      this.previousPageLastCrawled = fileLastCrawled;
      if (fileLastCrawled.isFile() || fileLastCrawled.isDirectory()) {
         if (fileLastCrawled.canRead()) {
            File[] children = fileLastCrawled.listFiles();
            if (children != null) {
               return Arrays.asList(children);
            }
         }
      }
      return Collections.emptyList();
   }

   protected PcuDocumentsMapResult buildPcuDocuments(File toBeCrawled) throws FileNotFoundException, IOException {
      return this.buildPcuDocuments(toBeCrawled, null);
   }

   public File getLastCrawled() {
      if (this.lastCrawled == null && this.lastCrawledFilePath != null) {
         this.lastCrawled = new File(this.lastCrawledFilePath);
      }
      return this.lastCrawled;
   }
   public String getLastCrawledFilePath() {
      return this.lastCrawledFilePath;
   }
   /**
    * Allows YAML or JSON deser i.e. if no lastCrawled yet.
    * Can also be used alternatively to new FileCrawlBatch(lastCrawled, crawler).
    * @param jdbcProperties
    */
   public void setLastCrawledFilePath(String lastCrawledFilePath) {
      this.lastCrawledFilePath = lastCrawledFilePath;
   }

}
