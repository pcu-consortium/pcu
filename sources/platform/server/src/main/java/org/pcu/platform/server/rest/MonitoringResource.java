package org.pcu.platform.server.rest;

import java.util.HashMap;

/*-
 * #%L
 * PCU Platform Server
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

import java.util.Map;

import org.pcu.platform.server.service.MonitoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MonitoringResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringResource.class);

	@Autowired
	private MonitoringService monitoringService;

	@RequestMapping(path = "/status", method = RequestMethod.HEAD)
	public ResponseEntity<Void> status() {
		LOGGER.debug("status");
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(path = "/status", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> statusDetail() {
		LOGGER.debug("status detail");
		return new ResponseEntity<>(monitoringService.getStatus(), HttpStatus.OK);
	}

}
