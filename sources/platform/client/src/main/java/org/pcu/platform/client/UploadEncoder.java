package org.pcu.platform.client;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import java.lang.reflect.Type;

public class UploadEncoder implements Encoder {
	private final Encoder delegate;

	public UploadEncoder(Encoder encoder) {
		super();
		delegate = encoder;
	}

	@Override
	public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
		if (InputStream.class.equals(bodyType)) {
			template.header("Content-type", "application/octet-stream");
			InputStream inputStream = InputStream.class.cast(object);
			try (BufferedInputStream bin = new BufferedInputStream(inputStream);
					ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = bin.read(buffer)) > 0) {
					bos.write(buffer, 0, bytesRead);
				}
				bos.flush();
				template.body(bos.toByteArray(), StandardCharsets.UTF_8);
			} catch (IOException e) {
				throw new EncodeException("Cannot upload file", e);
			}
		} else {
			delegate.encode(object, bodyType, template);
		}
	}
}