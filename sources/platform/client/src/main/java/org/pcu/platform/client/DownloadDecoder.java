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
