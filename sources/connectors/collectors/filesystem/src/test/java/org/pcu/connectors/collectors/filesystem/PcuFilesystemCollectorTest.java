package org.pcu.connectors.collectors.filesystem;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pcu.connectors.collectors.PcuCollectorException;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//@SpringBootTest(properties = {"norconex.filesystem.config.xml.path=toto"})


@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan("org.pcu.connectors.collectors.filesystem")
@ContextConfiguration
@TestPropertySource(properties = {
	    "norconex.filesystem.config.xml.path=toto",
	})
public class PcuFilesystemCollectorTest {
	
	@Test
	public void dumbTest() throws PcuCollectorException {
		PcuFilesystemCollector collector = new PcuFilesystemCollector();
		collector.execute();
	}

}
