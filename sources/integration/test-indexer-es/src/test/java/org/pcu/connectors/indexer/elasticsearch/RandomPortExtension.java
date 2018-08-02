package org.pcu.connectors.indexer.elasticsearch;

import java.io.IOException;
import java.net.ServerSocket;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class RandomPortExtension implements BeforeEachCallback, AfterEachCallback {

	private Integer port;

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		this.port = findPort();

	}

	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		this.port = null;

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
