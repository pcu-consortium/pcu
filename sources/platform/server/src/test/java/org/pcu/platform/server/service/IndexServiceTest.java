package org.pcu.platform.server.service;

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





import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pcu.connectors.index.PcuIndex;
import org.pcu.connectors.index.PcuIndexException;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
public class IndexServiceTest {

	@InjectMocks
	private IndexService indexService;

	@Mock
	private PcuIndex pcuIndex;

	@Test
	public void shouldNotThrowExceptionWhenDeleted() throws PcuIndexException {
		Mockito.when(pcuIndex.deleteIndex(Mockito.anyString())).thenReturn(true);
		assertThatCode(() -> {
			indexService.deleteIndex("documents");
		}).doesNotThrowAnyException();
	}

	@Test
	public void shouldThrowExceptionWhenExceptionInIndexDelete() throws PcuIndexException {
		Mockito.when(pcuIndex.deleteIndex(Mockito.anyString())).thenThrow(PcuIndexException.class);
		assertThatThrownBy(() -> {
			indexService.deleteIndex("documents");
		}).isInstanceOf(PcuIndexException.class);
	}

	@Test
	public void shouldThrowExceptionWhenNotDeleted() throws PcuIndexException {
		Mockito.when(pcuIndex.deleteIndex(Mockito.anyString())).thenReturn(false);
		assertThatThrownBy(() -> {
			indexService.deleteIndex("documents");
		}).isInstanceOf(PcuIndexException.class);
	}

	@Test
	public void shouldNotThrowExceptionWhenCreated() throws PcuIndexException {
		Mockito.when(pcuIndex.createIndex(Mockito.anyString())).thenReturn(true);
		assertThatCode(() -> {
			indexService.createIndex("documents");
		}).doesNotThrowAnyException();
	}

	@Test
	public void shouldThrowExceptionWhenExceptionInIndexCreate() throws PcuIndexException {
		Mockito.when(pcuIndex.createIndex(Mockito.anyString())).thenThrow(PcuIndexException.class);
		assertThatThrownBy(() -> {
			indexService.createIndex("documents");
		}).isInstanceOf(PcuIndexException.class);
	}

	@Test
	public void shouldThrowExceptionWhenNotCreated() throws PcuIndexException {
		Mockito.when(pcuIndex.createIndex(Mockito.anyString())).thenReturn(false);
		assertThatThrownBy(() -> {
			indexService.createIndex("documents");
		}).isInstanceOf(PcuIndexException.class);
	}
}
