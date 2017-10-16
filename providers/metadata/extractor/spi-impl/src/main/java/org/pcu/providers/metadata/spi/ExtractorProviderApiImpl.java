package org.pcu.providers.metadata.spi;

import com.qwazr.extractor.ExtractorManager;
import com.qwazr.extractor.ExtractorServiceInterface;
import com.qwazr.extractor.ParserResult;
import org.pcu.providers.metadata.api.PcuMetadataApi;
import org.pcu.providers.metadata.api.PcuMetadataResult;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAcceptableException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExtractorProviderApiImpl implements PcuMetadataApi {

	private final ExtractorServiceInterface extractorService;

	public ExtractorProviderApiImpl() {
		final ExtractorManager extractorManager = new ExtractorManager();
		try {
			extractorManager.registerServices();
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		extractorService = extractorManager.getService();
	}

	@Override
	public PcuMetadataResult extract(String locator) {
		try {
			final URL url = new URL(locator);
			final URLConnection urlConnection = url.openConnection();
			final String contentType = urlConnection.getContentType();
			try (InputStream input = urlConnection.getInputStream()) {
				return new Result(extractorService.putMagic(null, url.getFile(), null, contentType, input));
			}
		} catch (MalformedURLException e) {
			throw new NotAcceptableException(e);
		} catch (IOException e) {
			throw new InternalServerErrorException(e);
		}
	}

	class Result implements PcuMetadataResult {

		final private Map<String, Object> metas;
		final private List<Map<String, Object>> documents;

		Result(ParserResult parserResult) {
			metas = parserResult.metas;
			documents = parserResult != null && parserResult.documents != null ?
					new ArrayList<>(parserResult.documents) :
					null;
		}

		@Override
		public Map<String, Object> getMetas() {
			return metas;
		}

		@Override
		public List<Map<String, Object>> getContent() {
			return documents;
		}
	}
}
