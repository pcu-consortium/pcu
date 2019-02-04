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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.pcu.connectors.index.elasticsearch.PcuESIndex;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RunWith(JUnitPlatform.class)
public class PcuIndexFactoryTest {

	@Test
	public void givenConfigurationValidES5ExpectPcuEsIndex() {

		ObjectNode configuration = new ObjectMapper().createObjectNode();
		configuration.put("uri", "http://localhost:9200");
		PcuIndexConfiguration pcuIndexConfiguration = new PcuIndexConfiguration(
				"org.pcu.connectors.index.elasticsearch.PcuESIndex", configuration);
		assertThatCode(() -> {
			PcuIndex pcuIndex = PcuIndexFactory.createIndex(pcuIndexConfiguration);
			assertThat(pcuIndex).isInstanceOf(PcuESIndex.class);
		}).doesNotThrowAnyException();
	}

	@Test
	public void givenNonExistantClassExpectIllegalArgumentException() {

		ObjectNode configuration = new ObjectMapper().createObjectNode();
		configuration.put("uri", "http://localhost:9200");
		PcuIndexConfiguration pcuIndexConfiguration = new PcuIndexConfiguration(
				"org.pcu.connectors.index.elasticsearch.PcuFakeIndex", configuration);
		assertThatCode(() -> {
			PcuIndexFactory.createIndex(pcuIndexConfiguration);
		}).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("index class invalid");
	}

	@Test
	public void givenNonExistantConstructorConfigurationParameterExpectIllegalArgumentException() {

		ObjectNode configuration = new ObjectMapper().createObjectNode();
		configuration.put("uri", "http://localhost:9200");
		PcuIndexConfiguration pcuIndexConfiguration = new PcuIndexConfiguration(
				"org.pcu.connectors.index.PcuNoConstructorIndex", configuration);
		assertThatCode(() -> {
			PcuIndexFactory.createIndex(pcuIndexConfiguration);
		}).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("could not find valid constructor");
	}

	@Test
	public void givenConfigurationNoUriExpectException() {
		ObjectNode configuration = new ObjectMapper().createObjectNode();
		PcuIndexConfiguration pcuIndexConfiguration = new PcuIndexConfiguration(
				"org.pcu.connectors.index.elasticsearch.PcuESIndex", configuration);
		assertThatThrownBy(() -> {
			PcuIndexFactory.createIndex(pcuIndexConfiguration);
		}).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("instanciation of class thrown an exception")
				.hasCause(new PcuIndexConfigurationException("configuration invalid : expected 'uri' parameter"));
	}

	@Test
	public void givenConfigurationNoConfigurationExpectException() {
		PcuIndexConfiguration pcuIndexConfiguration = new PcuIndexConfiguration(
				"org.pcu.connectors.index.elasticsearch.PcuESIndex", null);
		assertThatThrownBy(() -> {
			PcuIndexFactory.createIndex(pcuIndexConfiguration);
		}).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("configuration invalid");
	}

}
