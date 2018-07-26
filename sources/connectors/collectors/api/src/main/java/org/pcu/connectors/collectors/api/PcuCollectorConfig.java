package org.pcu.connectors.collectors.api;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PcuCollectorConfig {

	@JsonProperty(value = "collectorId", required = true)
	private String collectorId;
	@JsonProperty(value = "datasourceId", required = true)
	private String datasourceId;
	@JsonProperty(value = "pcuPlatformUrl", required = true)
	private String pcuPlatformUrl;

	private Map<String, String> other = new HashMap<String, String>();

	@JsonAnyGetter
	public Map<String, String> any() {
		return other;
	}

	@JsonAnySetter
	public void set(String name, String value) {
		other.put(name, value);
	}

	public String getCollectorId() {
		return collectorId;
	}

	public void setCollectorId(String collectorId) {
		this.collectorId = collectorId;
	}

	public String getDatasourceId() {
		return datasourceId;
	}

	public void setDatasourceId(String datasourceId) {
		this.datasourceId = datasourceId;
	}

	public String getPcuPlatformUrl() {
		return pcuPlatformUrl;
	}

	public void setPcuPlatformUrl(String pcuPlatformUrl) {
		this.pcuPlatformUrl = pcuPlatformUrl;
	}
}
