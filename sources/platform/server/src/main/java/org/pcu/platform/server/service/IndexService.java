package org.pcu.platform.server.service;

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
