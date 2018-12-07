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

import feign.Response;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;

public class PcuPlatformErrorDecoder implements ErrorDecoder {

	final Decoder decoder;
	final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();

	PcuPlatformErrorDecoder(Decoder decoder) {
		this.decoder = decoder;
	}

	@Override
	public Exception decode(String methodKey, Response response) {
		try {
			Object result = decoder.decode(response, PcuPlatformClientException.class);
			if (result == null) {
				if (response.status() < 500) {
					return new PcuPlatformClientException("Client error " + response.status());
				} else {
					return new PcuPlatformClientException("Server error " + response.status());
				}
			} else {
				return (Exception) result;
			}
		} catch (IOException fallbackToDefault) {
			return defaultDecoder.decode(methodKey, response);
		}
	}
}
