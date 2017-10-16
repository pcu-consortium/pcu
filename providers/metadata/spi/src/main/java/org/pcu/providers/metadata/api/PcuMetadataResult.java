package org.pcu.providers.metadata.api;

import java.util.List;
import java.util.Map;

/**
 * @author ekeller
 */
public interface PcuMetadataResult {
	
	Map<String, Object> getMetas();

	List<Map<String, Object>> getContent();

}
