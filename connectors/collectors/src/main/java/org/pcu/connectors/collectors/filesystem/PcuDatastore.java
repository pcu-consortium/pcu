package org.pcu.connectors.collectors.filesystem;

import java.util.Iterator;

import com.norconex.collector.core.data.ICrawlData;
import com.norconex.collector.core.data.store.ICrawlDataStore;

public class PcuDatastore implements ICrawlDataStore {

	@Override
	public void queue(ICrawlData crawlData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isQueueEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getQueueSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isQueued(String reference) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ICrawlData nextQueued() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isActive(String reference) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getActiveCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ICrawlData getCached(String cacheReference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCacheEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void processed(ICrawlData crawlData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isProcessed(String reference) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getProcessedCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Iterator<ICrawlData> getCacheIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
