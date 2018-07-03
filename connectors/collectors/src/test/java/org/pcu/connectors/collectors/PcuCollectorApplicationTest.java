package org.pcu.connectors.collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pcu.connectors.collectors.filesystem.PcuFilesystemCollector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = PcuCollectorApplication.class)
public class PcuCollectorApplicationTest {

//	@Value(value = "classpath:norconex-filesystem-config.xml")
//	private Resource norconexFilesystemConfigXml;
    
	@Test
    public void dumbIntegrationTest() {
    	//ReflectionTestUtils.setField(PcuFilesystemCollector.class, "norconexFilesystemConfigXml", "norconex-filesystem-config.xml");
    	PcuCollectorApplication.main(new String[] {});
    	System.out.println("tested 1");
    }


    //@Test
    public void dumberIntegrationTest() {
    	PcuCollectorApplication.main(new String[] {});
    	System.out.println("tested 2");
    }
}
