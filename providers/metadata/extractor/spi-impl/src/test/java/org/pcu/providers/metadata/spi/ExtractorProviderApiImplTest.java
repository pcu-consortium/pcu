package org.pcu.providers.metadata.spi;

import com.qwazr.utils.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.pcu.providers.metadata.api.PcuMetadataApi;
import org.pcu.providers.metadata.api.PcuMetadataResult;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ExtractorProviderApiImplTest {

	@Test
	public void testWithTextFile() throws IOException {

		// We create a text file as example
		final Path testFile = Files.createTempFile("extractor", ".txt");
		IOUtils.writeStringToPath("test", StandardCharsets.UTF_8, testFile);

		// We build an URL as locator
		final String testFileUrl = testFile.toUri().toString();

		// Let's create an instance
		final PcuMetadataApi api = new ExtractorProviderApiImpl();

		// Do the extraction
		final PcuMetadataResult result = api.extract(testFileUrl);
		Assert.assertNotNull(result);
		Assert.assertNotNull(result.getContent());
		Assert.assertEquals(Arrays.asList("test"), result.getContent().get(0).get("content"));
	}

	@Test
	public void testWithPdfFile() {

		// Get the URL to our test PDF
		final URL filePdfURL = ExtractorProviderApiImplTest.class.getResource("file.pdf");

		// Let's create an instance
		final PcuMetadataApi api = new ExtractorProviderApiImpl();

		final PcuMetadataResult result = api.extract(filePdfURL.toString());
		Assert.assertNotNull(result);
		Assert.assertNotNull(result.getContent());

		// Extract our meta data (title, author, creation date etc...)
		Map<String, Object> metas = result.getMetas();
		Assert.assertNotNull(metas);

		// Extract the  content. A list is provided for multipage documents.
		final List<Map<String, Object>> pages = result.getContent();

		for (Map<String, Object> pageContent : pages) {

			Assert.assertNotNull(pageContent);
			Assert.assertNotNull(pageContent.get("content"));
		}
	}
}