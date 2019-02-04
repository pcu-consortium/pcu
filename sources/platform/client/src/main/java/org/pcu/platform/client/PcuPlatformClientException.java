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





public class PcuPlatformClientException extends RuntimeException {

	public PcuPlatformClientException(String message) {
		super(message);
	}

	public PcuPlatformClientException(String message, Throwable cause) {
		super(message, cause);

	}

    public PcuPlatformClientException(Throwable cause) {
        super(cause);
    }

}
