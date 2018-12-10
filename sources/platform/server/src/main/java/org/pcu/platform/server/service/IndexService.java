package org.pcu.platform.server.service;

/*-
 * #%L
 * PCU Platform Server
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





import org.pcu.connectors.index.PcuIndex;
import org.pcu.connectors.index.PcuIndexException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IndexService {

	private static final Logger LOGGER = LoggerFactory.getLogger(IndexService.class);

	@Autowired
	private PcuIndex pcuIndex;

	public void createIndex(String indexId) throws PcuIndexException {
		LOGGER.debug("create index");
		boolean created = pcuIndex.createIndex(indexId);
		if (!created) {
			throw new PcuIndexException("could not create index");
		}
	}

	public void deleteIndex(String indexId) throws PcuIndexException {
		LOGGER.debug("delete index");
		boolean deleted = pcuIndex.deleteIndex(indexId);
		if (!deleted) {
			throw new PcuIndexException("could not delete index");
		}
	}

}
