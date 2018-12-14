package org.pcu.connectors.index;

/*-
 * #%L
 * PCU Index Core
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

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Used in PcuIndexFactoryTest
 *
 */
public class PcuNoConstructorIndex implements PcuIndex {

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
	public boolean createIndex(String index) throws PcuIndexException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteIndex(String index) throws PcuIndexException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean createDocument(JsonNode document, String index, String type, String id) throws PcuIndexException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteDocument(String index, String type, String id) throws PcuIndexException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JsonNode getDocument(String index, String type, String id) throws PcuIndexException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonNode getDocuments(JsonNode searchQuery) throws PcuIndexException {
		// TODO Auto-generated method stub
		return null;
	}

}
