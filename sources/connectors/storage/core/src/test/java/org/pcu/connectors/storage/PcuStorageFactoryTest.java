package org.pcu.connectors.storage;

/*-
 * #%L
 * PCU Storage Core
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

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URL;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RunWith(JUnitPlatform.class)
public class PcuStorageFactoryTest {

	@Test
	public void givenConfigurationValidExpectPcuVfs2Storage() {

		ObjectNode configuration = new ObjectMapper().createObjectNode();
		configuration.put("path", System.getProperty("java.io.tmpdir"));
		PcuStorageConfiguration pcuStorageConfiguration = new PcuStorageConfiguration(
				"org.pcu.connectors.storage.vfs2.PcuVfs2Storage", configuration);
		assertThatCode(() -> {
			PcuStorageFactory.createStorage(pcuStorageConfiguration);
		}).doesNotThrowAnyException();
	}

	@Test
	public void givenConfigurationNoPathExpectException() {
		ObjectNode configuration = new ObjectMapper().createObjectNode();
		PcuStorageConfiguration pcuStorageConfiguration = new PcuStorageConfiguration(
				"org.pcu.connectors.storage.vfs2.PcuVfs2Storage", configuration);
		assertThatThrownBy(() -> {
			PcuStorageFactory.createStorage(pcuStorageConfiguration);
		}).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("instanciation of class thrown an exception")
				.hasCause(new PcuStorageConfigurationException("configuration invalid : expected 'path' parameter"));
	}

	@Test
	public void givenConfigurationInvalidFilePathExpectException() {
		URL classUrl = PcuStorageFactoryTest.class.getResource("PcuStorageFactoryTest.class");
		ObjectNode configuration = new ObjectMapper().createObjectNode();
		configuration.put("path", classUrl.getPath());
		PcuStorageConfiguration pcuStorageConfiguration = new PcuStorageConfiguration(
				"org.pcu.connectors.storage.vfs2.PcuVfs2Storage", configuration);
		assertThatThrownBy(() -> {
			PcuStorageFactory.createStorage(pcuStorageConfiguration);
		}).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("is not a directory");
	}

	@Test
	public void givenConfigurationInvalidDoesNotExistsPathExpectException() {
		ObjectNode configuration = new ObjectMapper().createObjectNode();
		configuration.put("path", System.getProperty("java.io.tmpdir") + "fake");
		PcuStorageConfiguration pcuStorageConfiguration = new PcuStorageConfiguration(
				"org.pcu.connectors.storage.vfs2.PcuVfs2Storage", configuration);
		assertThatThrownBy(() -> {
			PcuStorageFactory.createStorage(pcuStorageConfiguration);
		}).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("does not exists");
	}

	@Test
	public void givenConfigurationInvalidNotWritablePathExpectException() {
		ObjectNode configuration = new ObjectMapper().createObjectNode();
		configuration.put("path", "/");
		PcuStorageConfiguration pcuStorageConfiguration = new PcuStorageConfiguration(
				"org.pcu.connectors.storage.vfs2.PcuVfs2Storage", configuration);
		assertThatThrownBy(() -> {
			PcuStorageFactory.createStorage(pcuStorageConfiguration);
		}).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("missing write permission access");
	}

	@Test
	public void givenConfigurationInvalidClassExpectException() {
		ObjectNode configuration = new ObjectMapper().createObjectNode();
		configuration.put("path", System.getProperty("java.io.tmpdir"));
		PcuStorageConfiguration pcuStorageConfiguration = new PcuStorageConfiguration("FAKE", configuration);
		assertThatThrownBy(() -> {
			PcuStorageFactory.createStorage(pcuStorageConfiguration);
		}).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("storage class invalid");
	}

	@Test
	public void givenConfigurationNoConfigurationExpectException() {
		PcuStorageConfiguration pcuStorageConfiguration = new PcuStorageConfiguration(
				"org.pcu.connectors.storage.vfs2.PcuVfs2Storage", null);
		assertThatThrownBy(() -> {
			PcuStorageFactory.createStorage(pcuStorageConfiguration);
		}).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("configuration invalid");
	}

	@Test
	public void givenNonExistantConstructorConfigurationParameterExpectIllegalArgumentException() {

		ObjectNode configuration = new ObjectMapper().createObjectNode();
		configuration.put("path", System.getProperty("java.io.tmpdir"));
		PcuStorageConfiguration pcuStorageConfiguration = new PcuStorageConfiguration(
				"org.pcu.connectors.storage.PcuNoConstructorStorage", configuration);
		assertThatCode(() -> {
			PcuStorageFactory.createStorage(pcuStorageConfiguration);
		}).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("could not find valid constructor");
	}
}
