package org.pcu.connectors.indexer.elasticsearch;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

public class EmbeddedElasticsearchExtension implements BeforeEachCallback, AfterEachCallback {

	private Integer port;
	private EmbeddedElastic embeddedElastic;

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		this.port = findPort();
		embeddedElastic = EmbeddedElastic.builder().withElasticVersion("6.0.1")
				.withSetting(PopularProperties.TRANSPORT_TCP_PORT, port)
				.withSetting(PopularProperties.CLUSTER_NAME, "myCluster").withEsJavaOpts("-Xms128m -Xmx512m")
				.withStartTimeout(1, TimeUnit.MINUTES).build().start();

	}

	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		this.port = null;
		embeddedElastic.stop();

	}

	public Integer getPort() {
		return port;
	}

	private final int findPort() {
		try (ServerSocket socket = new ServerSocket(0)) {
			socket.setReuseAddress(true);
			return socket.getLocalPort();
		} catch (final IOException e) {
			throw new IllegalStateException("COULD_NOT_FIND_A_FREE_TCP_IP_PORT", e);
		}
	}
}
