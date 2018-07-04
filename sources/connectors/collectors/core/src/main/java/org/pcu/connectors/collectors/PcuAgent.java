package org.pcu.connectors.collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PcuAgent {

	@Autowired
	private PcuCollector collector;

	public PcuAgent() {
		System.out.println("init");
	}

	public void exectue() throws PcuCollectorException {
		collector.execute();
	}

}
