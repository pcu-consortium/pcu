package org.pcu.connectors.collectors;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = PcuCollectorApplication.class)
public class PcuCollectorApplicationIT {


	// FIXME when something actually works YOLO
	@Test
	@Ignore
	public void dumbIntegrationTest() throws PcuCollectorException {
		// Mockito.when(pcuIndexer.index(ArgumentMatchers.any(), ArgumentMatchers.any(),
		// ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(true);
		PcuCollectorApplication.main(new String[] {});
		System.out.println("tested 1");
	}
}
