package org.pcu.connectors.index;

/*-
 * #%L
 * PCU Index API
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

public class PcuIndexConfiguration {

	private String className;
	private JsonNode configuration;

	public PcuIndexConfiguration(String className, JsonNode configuration) {
		super();
		this.configuration = configuration;
		this.className = className;
	}

	public JsonNode getConfigutation() {
		return configuration;
	}

	public String getClassName() {
		return className;
	}

}
