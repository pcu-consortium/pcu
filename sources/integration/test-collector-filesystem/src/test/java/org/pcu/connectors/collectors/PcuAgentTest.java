package org.pcu.connectors.collectors;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.pcu.connectors.collectors.api.PcuCollectorConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(TemporaryFolderExtension.class)
public class PcuAgentTest {

	@RegisterExtension
	public TemporaryFolderExtension temporaryFolder = new TemporaryFolderExtension();

	@Test
	public void testMain() throws IOException {
		
		String filename = UUID.randomUUID().toString();
		File confFile = temporaryFolder.getRoot().createTempFile(filename,".json");
		
		ObjectMapper mapper = new ObjectMapper();
		PcuCollectorConfig config = new PcuCollectorConfig();
		config.setCollectorId("collectorId");
		config.setDatasourceId("datasourceId");
		config.setPcuPlatformUrl("http://localhost:8080");
		mapper.writeValue(confFile, config);
		
		String[] args = new String[] { confFile.getAbsolutePath() };
		PcuAgent.main(args);
	}

}
