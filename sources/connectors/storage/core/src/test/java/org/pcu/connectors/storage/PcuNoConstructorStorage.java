package org.pcu.connectors.storage;

/*-
 * #%L
 * PCU Storage Core
 * %%
 * Copyright (C) 2017 - 2019 PCU Consortium
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

import com.fasterxml.jackson.databind.JsonNode;

public class PcuNoConstructorStorage implements PcuStorage {

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public JsonNode getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean createContainer(String containerName) throws PcuStorageException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteContainer(String containerName) throws PcuStorageException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean upload(InputStream content, String containerName, String id)
			throws PcuStorageContainerNotFoundException, PcuStorageException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public InputStream download(String containerName, String id)
			throws PcuStorageContainerNotFoundException, PcuStorageFileNotFoundException, PcuStorageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delete(String containerName, String id)
			throws PcuStorageContainerNotFoundException, PcuStorageException {
		// TODO Auto-generated method stub
		return false;
	}

}
