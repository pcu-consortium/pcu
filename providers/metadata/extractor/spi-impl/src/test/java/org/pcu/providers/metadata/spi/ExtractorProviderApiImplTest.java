package org.pcu.providers.metadata.spi;

import com.qwazr.utils.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.pcu.providers.metadata.api.PcuMetadataApi;
import org.pcu.providers.metadata.api.PcuMetadataResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class ExtractorProviderApiImplTest {

	@Test
	public void test() throws IOException {
		final File testFile = Files.createTempFile("extractor", ".txt").toFile();
		IOUtils.writeStringAsFile("test", testFile);

		final PcuMetadataApi api = new ExtractorProviderApiImpl();
		Assert.assertNotNull(api);

		final PcuMetadataResult result = api.extract(testFile.toURI().toString());
		Assert.assertNotNull(result);
		Assert.assertNotNull(result.getContent());
		Assert.assertEquals(Arrays.asList("test"), result.getContent().get(0).get("content"));
	}
}