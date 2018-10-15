package org.pcu.platform.server.service;

import org.pcu.connectors.index.PcuIndex;
import org.pcu.connectors.index.PcuIndexException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IndexService {

	@Autowired
	private PcuIndex pcuIndex;

	public void createIndex(String indexId) throws PcuIndexException {
		boolean created = pcuIndex.createIndex(indexId);
		if (!created) {
			throw new PcuIndexException("could not create index");
		}
	}

	public void deleteIndex(String indexId) throws PcuIndexException {
		boolean deleted = pcuIndex.deleteIndex(indexId);
		if (!deleted) {
			throw new PcuIndexException("could not delete index");
		}
	}

}
