package org.pcu.connectors.collectors.api;

/*-
 * #%L
 * PCU Collector API
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
