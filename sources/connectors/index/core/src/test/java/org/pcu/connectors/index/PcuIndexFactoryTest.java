package org.pcu.connectors.index;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RunWith(JUnitPlatform.class)
public class PcuIndexFactoryTest {

	@Test
	public void givenConfigurationValidES5ExpectPcuVfs2Storage() {

		ObjectNode configuration = new ObjectMapper().createObjectNode();
		configuration.put("uri", "http://localhost:9200");
		PcuIndexConfiguration pcuIndexConfiguration = new PcuIndexConfiguration("ES5", configuration);
		assertThatCode(() -> {
			PcuIndexFactory.createIndex(pcuIndexConfiguration);
		}).doesNotThrowAnyException();
	}

	@Test
	public void givenConfigurationValidES6ExpectPcuVfs2Storage() {

		ObjectNode configuration = new ObjectMapper().createObjectNode();
		configuration.put("uri", "http://localhost:9200");
		PcuIndexConfiguration pcuIndexConfiguration = new PcuIndexConfiguration("ES6", configuration);
		assertThatCode(() -> {
			PcuIndexFactory.createIndex(pcuIndexConfiguration);
		}).doesNotThrowAnyException();
	}

	@Test
	public void givenConfigurationNoUriExpectException() {
		ObjectNode configuration = new ObjectMapper().createObjectNode();
		PcuIndexConfiguration pcuIndexConfiguration = new PcuIndexConfiguration("ES6", configuration);
		assertThatThrownBy(() -> {
			PcuIndexFactory.createIndex(pcuIndexConfiguration);
		}).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("configuration invalid");
	}

	@Test
	public void givenConfigurationInvalidTypeExpectException() {
		ObjectNode configuration = new ObjectMapper().createObjectNode();
		configuration.put("uri", "http://localhost:9200");
		PcuIndexConfiguration pcuIndexConfiguration = new PcuIndexConfiguration("FAKE", configuration);
		assertThatThrownBy(() -> {
			PcuIndexFactory.createIndex(pcuIndexConfiguration);
		}).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("type invalid");
	}

	@Test
	public void givenConfigurationNoConfigurationExpectException() {
		PcuIndexConfiguration pcuIndexConfiguration = new PcuIndexConfiguration("ES6", null);
		assertThatThrownBy(() -> {
			PcuIndexFactory.createIndex(pcuIndexConfiguration);
		}).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("configuration invalid");
	}

}
