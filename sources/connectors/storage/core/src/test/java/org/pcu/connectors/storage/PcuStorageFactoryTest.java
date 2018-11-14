package org.pcu.connectors.storage;

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
		PcuStorageConfiguration pcuStorageConfiguration = new PcuStorageConfiguration("VFS2", configuration);
		assertThatCode(() -> {
			PcuStorageFactory.createStorage(pcuStorageConfiguration);
		}).doesNotThrowAnyException();
	}

	@Test
	public void givenConfigurationNoPathExpectException() {
		ObjectNode configuration = new ObjectMapper().createObjectNode();
		PcuStorageConfiguration pcuStorageConfiguration = new PcuStorageConfiguration("VFS2", configuration);
		assertThatThrownBy(() -> {
			PcuStorageFactory.createStorage(pcuStorageConfiguration);
		}).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("configuration invalid");
	}

	@Test
	public void givenConfigurationInvalidFilePathExpectException() {
		URL classUrl = PcuStorageFactoryTest.class.getResource("PcuStorageFactoryTest.class");
		ObjectNode configuration = new ObjectMapper().createObjectNode();
		configuration.put("path", classUrl.getPath());
		PcuStorageConfiguration pcuStorageConfiguration = new PcuStorageConfiguration("VFS2", configuration);
		assertThatThrownBy(() -> {
			PcuStorageFactory.createStorage(pcuStorageConfiguration);
		}).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("is not a directory");
	}

	@Test
	public void givenConfigurationInvalidDoesNotExistsPathExpectException() {
		ObjectNode configuration = new ObjectMapper().createObjectNode();
		configuration.put("path", System.getProperty("java.io.tmpdir") + "fake");
		PcuStorageConfiguration pcuStorageConfiguration = new PcuStorageConfiguration("VFS2", configuration);
		assertThatThrownBy(() -> {
			PcuStorageFactory.createStorage(pcuStorageConfiguration);
		}).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("does not exists");
	}

	@Test
	public void givenConfigurationInvalidNotWritablePathExpectException() {
		ObjectNode configuration = new ObjectMapper().createObjectNode();
		configuration.put("path", "/");
		PcuStorageConfiguration pcuStorageConfiguration = new PcuStorageConfiguration("VFS2", configuration);
		assertThatThrownBy(() -> {
			PcuStorageFactory.createStorage(pcuStorageConfiguration);
		}).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("missing write permission access");
	}

	@Test
	public void givenConfigurationInvalidTypeExpectException() {
		ObjectNode configuration = new ObjectMapper().createObjectNode();
		configuration.put("path", System.getProperty("java.io.tmpdir"));
		PcuStorageConfiguration pcuStorageConfiguration = new PcuStorageConfiguration("FAKE", configuration);
		assertThatThrownBy(() -> {
			PcuStorageFactory.createStorage(pcuStorageConfiguration);
		}).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("type invalid");
	}

	@Test
	public void givenConfigurationNoConfigurationExpectException() {
		PcuStorageConfiguration pcuStorageConfiguration = new PcuStorageConfiguration("VFS2", null);
		assertThatThrownBy(() -> {
			PcuStorageFactory.createStorage(pcuStorageConfiguration);
		}).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("configuration invalid");
	}
}
