package org.pcu.integration;

/*-
 * #%L
 * PCU Integration test : common code
 * %%
 * Copyright (C) 2017 - 2018 PCU Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */





import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TemporaryFolderExtension implements BeforeEachCallback, AfterEachCallback {

   private final File parentFolder;
   private File folder;

   public TemporaryFolderExtension() {
       this(null);
   }

   public TemporaryFolderExtension(File parentFolder) {
       this.parentFolder = parentFolder;
   }

   @Override
   public void afterEach(ExtensionContext extensionContext) {
       if (folder != null) {
           recursiveDelete(folder);
       }
   }

   @Override
   public void beforeEach(ExtensionContext extensionContext) throws IOException {
       folder = File.createTempFile("junit", "", parentFolder);
       folder.delete();
       folder.mkdir();
   }

   public File newFile(String fileName) throws IOException {
       File file = new File(getRoot(), fileName);
       if (!file.createNewFile()) {
           throw new IOException("a file with the name \'" + fileName + "\' already exists in the test folder");
       }
       return file;
   }

   public File newFolder(String folderName) {
       File file = getRoot();
       file = new File(file, folderName);
       file.mkdir();
       return file;
   }

   private void recursiveDelete(File file) {
       File[] files = file.listFiles();
       if (files != null) {
           for (File each : files) {
               recursiveDelete(each);
           }
       }
       file.delete();
   }

   public File getRoot() {
       if (folder == null) {
           throw new IllegalStateException("the temporary folder has not yet been created");
       }
       return folder;
   }

}
