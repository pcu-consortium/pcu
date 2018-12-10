package org.pcu.connectors.storage;

/*-
 * #%L
 * PCU Storage Core
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.vfs2.FileSystemException;
import org.pcu.connectors.storage.vfs2.PcuVfs2Storage;

public class PcuStorageFactory {

	public static PcuStorage createStorage(PcuStorageConfiguration configuration) {
		switch (configuration.getType()) {
		case "VFS2":
			if (configuration.getConfigutation() == null || !configuration.getConfigutation().has("path")) {
				throw new IllegalArgumentException("configuration invalid");
			}

			try {
				String validPath = null;
				try {
					String path = configuration.getConfigutation().get("path").asText();
					File file = new File(path);
					validPath = file.getCanonicalPath();
					checkValidPath(validPath);
				} catch (IOException ioe) {
					throw new IllegalArgumentException("storage path invalid", ioe);
				}
				return new PcuVfs2Storage.Builder(validPath).build();
			} catch (FileSystemException fse) {
				throw new IllegalArgumentException("storage instanciation error", fse);
			}
		default:
			throw new IllegalArgumentException("storage type invalid");
		}

	}

	private static void checkValidPath(String path) throws IllegalArgumentException {
		Path filePath = Paths.get(path);
		if (!Files.exists(filePath)) {
			throw new IllegalArgumentException("storage path does not exists");
		}
		if (!Files.isDirectory(filePath)) {
			throw new IllegalArgumentException("storage path is not a directory");
		}
		if (!Files.isWritable(filePath)) {
			throw new IllegalArgumentException("storage path missing write permission access");
		}
	}
}
