package org.pcu.connectors.storage;

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
				String path = configuration.getConfigutation().get("path").asText();
				return new PcuVfs2Storage.Builder(path).build();
			} catch (FileSystemException e) {
				throw new IllegalArgumentException("storage instanciation error");
			}
		default:
			throw new IllegalArgumentException("storage type invalid");
		}

	}
}
