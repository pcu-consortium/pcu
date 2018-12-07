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





import org.pcu.connectors.index.elasticsearch.PcuESIndex;

public class PcuIndexFactory {

	public static PcuIndex createIndex(PcuIndexConfiguration configuration) {
		switch (configuration.getType()) {
		case "ES5":
		case "ES6":
			if (configuration.getConfigutation() == null || !configuration.getConfigutation().has("uri")) {
				throw new IllegalArgumentException("configuration invalid");
			}
			return new PcuESIndex.Builder(configuration.getConfigutation().get("uri").asText()).build();
		default:
			throw new IllegalArgumentException("index type invalid");
		}

	}
}
