package org.pcu.connectors.collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = PcuCollectorApplication.class)
public class PcuCollectorApplicationTest {

    @Test
    public void dumbIntegrationTest() {
    	PcuCollectorApplication.main(new String[] {});
    	System.out.println("tested 1");
    }


    @Test
    public void dumberIntegrationTest() {
    	PcuCollectorApplication.main(new String[] {});
    	System.out.println("tested 2");
    }
}
