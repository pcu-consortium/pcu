package org.pcu.platform.client;

/*-
 * #%L
 * PCU Platform Client
 * %%
 * Copyright (C) 2017 - 2018 PCU Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */





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
