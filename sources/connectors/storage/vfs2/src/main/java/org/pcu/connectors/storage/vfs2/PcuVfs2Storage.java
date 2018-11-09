package org.pcu.connectors.storage.vfs2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.pcu.connectors.storage.PcuStorage;
import org.pcu.connectors.storage.PcuStorageException;

public class PcuVfs2Storage implements PcuStorage {

	private StandardFileSystemManager manager;
	private String baseConnection;

	private PcuVfs2Storage(Builder builder) throws FileSystemException {
		manager = new StandardFileSystemManager();
		manager.init();
		baseConnection = builder.getPath();
		manager.setBaseFile(manager.resolveFile(baseConnection));

	}

	@Override
	public void close() throws Exception {

	}

	public static class Builder {

		private String path;

		public Builder(String path) {
			this.path = path;
		}

		public String getPath() {
			return path;
		}

		public PcuStorage build() throws FileSystemException {
			PcuStorage result = new PcuVfs2Storage(this);
			return result;
		}
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
	public boolean upload(InputStream content, String containerName, String id) throws PcuStorageException {
		try {
			FileObject container = manager.resolveFile(containerName);
			if (!container.exists()) {
				throw new PcuStorageException("Could not upload file in container : container does not exists");
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
	public InputStream download(String containerName, String id) throws PcuStorageException {
		try {
			// TODO Auto-generated method stub
			FileObject container = manager.resolveFile(containerName);
			if (!container.exists()) {
				throw new PcuStorageException("Could not download file from container : container does not exists");
			}
			FileObject file = container.resolveFile(id);
			if (!file.exists()) {
				throw new PcuStorageException("Could not download file from container : file does not exists");
			}
			FileContent content = file.getContent();
			return content.getInputStream();
		} catch (FileSystemException e) {
			throw new PcuStorageException("Could not download file", e);
		}
	}

	@Override
	public boolean delete(String containerName, String id) throws PcuStorageException {
		try {
			FileObject container = manager.resolveFile(containerName);
			if (!container.exists()) {
				throw new PcuStorageException("Could not delete file from container : container does not exists");
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

}
