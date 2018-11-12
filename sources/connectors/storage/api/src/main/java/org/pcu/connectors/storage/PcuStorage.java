package org.pcu.connectors.storage;

import java.io.InputStream;

/**
 * PCU storage : connector containing all services to managed storage.
 * 
 * @author gafou
 *
 */
public interface PcuStorage extends AutoCloseable {

	boolean createContainer(String containerName) throws PcuStorageException;

	boolean deleteContainer(String containerName) throws PcuStorageException;

	boolean upload(InputStream content, String containerName, String id)
			throws PcuStorageContainerNotFoundException, PcuStorageException;

	InputStream download(String containerName, String id)
			throws PcuStorageContainerNotFoundException, PcuStorageFileNotFoundException, PcuStorageException;

	boolean delete(String containerName, String id) throws PcuStorageContainerNotFoundException, PcuStorageException;

}
