package org.pcu.connectors.storage.vfs2;

import java.io.File;

/*-
 * #%L
 * PCU Storage VFS2
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.pcu.connectors.storage.PcuStorage;
import org.pcu.connectors.storage.PcuStorageConfiguration;
import org.pcu.connectors.storage.PcuStorageConfigurationException;
import org.pcu.connectors.storage.PcuStorageContainerNotFoundException;
import org.pcu.connectors.storage.PcuStorageException;
import org.pcu.connectors.storage.PcuStorageFileNotFoundException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PcuVfs2Storage implements PcuStorage {

	private StandardFileSystemManager manager;
	private String baseConnection;

	public PcuVfs2Storage(PcuStorageConfiguration configuration) {
		if (configuration.getConfigutation() == null || !configuration.getConfigutation().has("path")) {
			throw new PcuStorageConfigurationException("configuration invalid : expected 'path' parameter");
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

			manager = new StandardFileSystemManager();
			manager.init();
			baseConnection = validPath;
			manager.setBaseFile(manager.resolveFile(baseConnection));
		} catch (FileSystemException fse) {
			throw new IllegalArgumentException("storage instanciation error", fse);
		}
	}

	@Override
	public void close() throws Exception {

	}

	@Override
	public JsonNode getStatus() {
		ObjectNode status = new ObjectMapper().createObjectNode();
		status.put("path", baseConnection);
		try {
			status.put("exists", manager.getBaseFile().exists());
			status.put("hidden", manager.getBaseFile().isHidden());
			status.put("writeable", manager.getBaseFile().isWriteable());
			status.put("readable", manager.getBaseFile().isReadable());
			status.put("executable", manager.getBaseFile().isExecutable());
		} catch (FileSystemException e) {
			status.put("error", e.getCode() + ":" + e.getInfo());
		}
		return status;
	}

	@Override
	public boolean createContainer(String containerName) throws PcuStorageException {
		String containerPath = containerName;
		try {
			FileObject container = manager.resolveFile(containerPath);
			if (container.exists()) {
				return false;
			} else {
				container.createFolder();
				return true;
			}
		} catch (FileSystemException e) {
			throw new PcuStorageException("Could not create container", e);
		}
	}

	@Override
	public boolean upload(InputStream content, String containerName, String id)
			throws PcuStorageContainerNotFoundException, PcuStorageException {
		try {
			FileObject container = manager.resolveFile(containerName);
			if (!container.exists()) {
				throw new PcuStorageContainerNotFoundException(
						"Could not upload file in container : container does not exists");
			}
			FileObject file = container.resolveFile(id);
			if (file.exists()) {
				return false;
			}
			file.createFile();
			try (FileContent fileContent = file.getContent(); OutputStream out = fileContent.getOutputStream()) {
				content.transferTo(out);
			}
			return true;
		} catch (IOException e) {
			throw new PcuStorageException("Could not upload file in container", e);
		}
	}

	@Override
	public boolean deleteContainer(String containerName) throws PcuStorageException {
		String containerPath = containerName;
		try {
			FileObject container = manager.resolveFile(containerPath);
			int result = container.deleteAll();
			if (result == 0) {
				return false;
			} else {
				return true;
			}
		} catch (FileSystemException e) {
			throw new PcuStorageException("Could not delete container", e);
		}
	}

	@Override
	public InputStream download(String containerName, String id)
			throws PcuStorageContainerNotFoundException, PcuStorageFileNotFoundException, PcuStorageException {
		try {
			FileObject container = manager.resolveFile(containerName);
			if (!container.exists()) {
				throw new PcuStorageContainerNotFoundException(
						"Could not download file from container : container does not exists");
			}
			FileObject file = container.resolveFile(id);
			if (!file.exists()) {
				throw new PcuStorageFileNotFoundException(
						"Could not download file from container : file does not exists");
			}
			FileContent content = file.getContent();
			return content.getInputStream();
		} catch (FileSystemException e) {
			throw new PcuStorageException("Could not download file", e);
		}
	}

	@Override
	public boolean delete(String containerName, String id)
			throws PcuStorageContainerNotFoundException, PcuStorageException {
		try {
			FileObject container = manager.resolveFile(containerName);
			if (!container.exists()) {
				throw new PcuStorageContainerNotFoundException(
						"Could not delete file from container : container does not exists");
			}
			FileObject file = container.resolveFile(id);
			if (!file.exists()) {
				return false;
			}
			return file.delete();
		} catch (FileSystemException e) {
			throw new PcuStorageException("Could not delete file", e);
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
