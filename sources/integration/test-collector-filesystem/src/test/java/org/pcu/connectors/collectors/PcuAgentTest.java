package org.pcu.connectors.collectors;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.pcu.connectors.collectors.api.PcuCollectorConfig;
import org.pcu.connectors.collectors.filesystem.internal.PcuFilesystemNorconexCollector;
import org.pcu.integration.TemporaryFolderExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

@ExtendWith(TemporaryFolderExtension.class)
public class PcuAgentTest {

	@RegisterExtension
	public TemporaryFolderExtension temporaryFolder = new TemporaryFolderExtension();

	@Test
	public void norconexCollectorOnSampleOk() throws IOException {

		String confFileName = UUID.randomUUID().toString();
		File confFile = temporaryFolder.getRoot().createTempFile(confFileName, ".json");
		String workdirFolderName = UUID.randomUUID().toString();
		File workdir = temporaryFolder.newFolder(workdirFolderName);
		String variablesFilePath = createTempVariables(workdir.getAbsolutePath(), "./sample");
		String xmlFilePath = createTempXml();
		
		ObjectMapper mapper = new ObjectMapper();
		PcuCollectorConfig config = new PcuCollectorConfig();
		config.setCollectorId("collectorId");
		config.setDatasourceId("datasourceId");
		// TODO branch on PCU server
		config.setPcuPlatformUrl("http://localhost:8080");
		config.set(PcuFilesystemNorconexCollector.EXTERNAL_CONFIG_XML_KEY, xmlFilePath);
		config.set(PcuFilesystemNorconexCollector.EXTERNAL_CONFIG_VARIABLES_KEY, variablesFilePath);
		mapper.writeValue(confFile, config);

		// TODO add pcuserver
		
		String[] args = new String[] { confFile.getAbsolutePath() };
		PcuAgent.main(args);
	
		List<String> workdirFileNames  = Arrays.asList(workdir.listFiles()).stream().map(File::getAbsolutePath).collect(Collectors.toList());
		
		assertThat(workdirFileNames).contains(workdir.getAbsolutePath()+"/crawlstore");
		assertThat(workdirFileNames).contains(workdir.getAbsolutePath()+"/logs");
		assertThat(workdirFileNames).contains(workdir.getAbsolutePath()+"/progress");
		
	}

	private String createTempVariables(String workdir, String targetPath) throws IOException {
		String filename = UUID.randomUUID().toString();
		File tmpFile = File.createTempFile(filename, ".variables");
		InputStream is = PcuAgentTest.class.getClassLoader().getResourceAsStream("norconex-filesystem-config.variables");
		String content = IOUtils.toString(is, Charsets.UTF_8);
		content = content.replace("${workdir}", workdir).replace("${targetPath}", targetPath);
		FileUtils.writeStringToFile(tmpFile, content, Charsets.UTF_8);
		return tmpFile.getAbsolutePath();
	}

	private String createTempXml() throws IOException {
		String filename = UUID.randomUUID().toString();
		File tmpFile = File.createTempFile(filename, ".xml");
		InputStream is = PcuAgentTest.class.getClassLoader().getResourceAsStream("norconex-filesystem-config.xml");
		FileUtils.copyInputStreamToFile(is, tmpFile);
		return tmpFile.getAbsolutePath();
	}

}
