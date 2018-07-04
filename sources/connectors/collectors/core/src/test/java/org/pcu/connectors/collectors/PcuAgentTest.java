package org.pcu.connectors.collectors;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PcuAgentTest {

	@Mock
	private PcuCollector pcuCollector;
	
	@InjectMocks
	@Resource
	private PcuAgent collector;
	
	@Before
	public void setUp() throws Exception {
	    // Initialize mocks created above
	    MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void dumbTest() {
		try {
			collector.exectue();
		} catch (PcuCollectorException e) {
			e.printStackTrace();
		}
	}
}
