package org.pcu.connectors.collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.noContent;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.base.Charsets;

import ru.lanwen.wiremock.ext.WiremockResolver;
import ru.lanwen.wiremock.ext.WiremockResolver.Wiremock;
import ru.lanwen.wiremock.ext.WiremockUriResolver;
import ru.lanwen.wiremock.ext.WiremockUriResolver.WiremockUri;

@ExtendWith({ TemporaryFolderExtension.class, WiremockResolver.class, WiremockUriResolver.class })
public class PcuAgentTest {

	@RegisterExtension
	public TemporaryFolderExtension temporaryFolder = new TemporaryFolderExtension();

	@Test
	public void norconexCollectorOnSampleOk(@Wiremock WireMockServer server, @WiremockUri String uri)
			throws IOException, URISyntaxException {

		assertThat(server.isRunning()).isTrue();
		configureMock(server);

		String confFileName = UUID.randomUUID().toString();
		File confFile = temporaryFolder.newFile(confFileName+ ".json");
		String workdirFolderName = UUID.randomUUID().toString();
		File workdir = temporaryFolder.newFolder(workdirFolderName);
		File sampleFolder = createTemporaryFolderWithSample();
		String variablesFilePath = createTempVariables(workdir.getAbsolutePath(), sampleFolder.getAbsolutePath());
		String xmlFilePath = createTempXml();

		ObjectMapper mapper = new ObjectMapper();
		PcuCollectorConfig config = new PcuCollectorConfig();
		config.setCollectorId("collectorId");
		config.setDatasourceId("datasourceId");

		config.setPcuPlatformUrl(uri);
		config.set(PcuFilesystemNorconexCollector.EXTERNAL_CONFIG_XML_KEY, xmlFilePath);
		config.set(PcuFilesystemNorconexCollector.EXTERNAL_CONFIG_VARIABLES_KEY, variablesFilePath);
		mapper.writeValue(confFile, config);

		String[] args = new String[] { confFile.getAbsolutePath() };
		PcuAgent.main(args);

		List<String> workdirFileNames = Arrays.asList(workdir.listFiles()).stream().map(File::getAbsolutePath)
				.collect(Collectors.toList());

		assertThat(workdirFileNames).contains(workdir.getAbsolutePath() + "/crawlstore");
		assertThat(workdirFileNames).contains(workdir.getAbsolutePath() + "/logs");
		assertThat(workdirFileNames).contains(workdir.getAbsolutePath() + "/progress");

		Files.delete(Paths.get(sampleFolder.getAbsolutePath() + "/20171206 POSS/PCU@POSS_20171206.pdf"));
		
		PcuAgent.main(args);
		
		// TODO assert on not found
	}

	private void configureMock(WireMockServer server) {
		server.stubFor(post("/ingest").willReturn(ok()));
		// TODO find valid matching regex
		server.stubFor(delete("/indexes/").willReturn(noContent()));

	}

	private String createTempVariables(String workdir, String targetPath) throws IOException {
		String filename = UUID.randomUUID().toString();
		File tmpFile = File.createTempFile(filename, ".variables");
		InputStream is = PcuAgentTest.class.getClassLoader()
				.getResourceAsStream("norconex-filesystem-config.variables");
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

	private File createTemporaryFolderWithSample() throws IOException, URISyntaxException {
		File sampleFolder = temporaryFolder.newFolder("sample");
		File existingSample = new File(PcuAgentTest.class.getClassLoader().getResource("sample").toURI());
		FileUtils.copyDirectory(existingSample, sampleFolder);
		return sampleFolder;
	}

}
