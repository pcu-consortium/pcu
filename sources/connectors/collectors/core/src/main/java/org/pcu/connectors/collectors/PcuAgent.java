package org.pcu.connectors.collectors;

public class PcuAgent {

	public static void main(String[] args) {
		if (args == null || args.length < 1) {
			throw new IllegalArgumentException("Missing argument : config file path");
		}
		String collectorConfig = args[0];
		PcuCollectorService.getInstance().init(collectorConfig);
		PcuCollectorService.getInstance().execute();
	}
}
