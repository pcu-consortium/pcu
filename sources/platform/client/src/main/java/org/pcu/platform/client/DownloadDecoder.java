package org.pcu.platform.client;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;

public class DownloadDecoder implements Decoder {
	private final Decoder delegate;

	DownloadDecoder(Decoder decoder) {
		super();
		delegate = decoder;
	}

	@Override
	public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
		if (InputStream.class.equals(type)) {
			return response.body().asInputStream();
		}
		return delegate.decode(response, type);
	}
}