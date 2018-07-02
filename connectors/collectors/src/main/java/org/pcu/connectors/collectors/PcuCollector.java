package org.pcu.connectors.collectors;

import org.pcu.connectors.collectors.filesystem.PcuFilesystemCollector;
import org.springframework.stereotype.Component;

@Component
public class PcuCollector {

	public PcuCollector() {
		System.out.println("init");
	}

	public void exectue() {
		// TODO Auto-generated method stub
		PcuFilesystemCollector collector = new PcuFilesystemCollector();
		collector.execute();
	}
	
}
