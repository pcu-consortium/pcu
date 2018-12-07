package org.pcu.connectors.storage;

/*-
 * #%L
 * PCU Storage API
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
