package org.pcu.connectors.collectors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import org.pcu.connectors.collectors.api.PcuCollector;

public class PcuAgent {

	private static ServiceLoader<PcuCollector> pcuCollectorLoader = ServiceLoader.load(PcuCollector.class);
	private static Map<String, PcuCollector> pcuCollectors = new HashMap<String, PcuCollector>();

	public static void main(String[] args) {
		initPcuCollectors();
	}

	private static void initPcuCollectors() {
		// Discover and register the available commands
		pcuCollectors.clear();
		pcuCollectorLoader.reload();
		Iterator<PcuCollector> pcuCollectorsIterator = pcuCollectorLoader.iterator();
		while (pcuCollectorsIterator.hasNext()) {
			PcuCollector collector = pcuCollectorsIterator.next();
			System.out.println(collector.getId());
			pcuCollectors.put(collector.getId(), collector);
		}
	}

}
