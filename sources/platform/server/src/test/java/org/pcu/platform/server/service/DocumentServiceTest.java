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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
public class DocumentServiceTest {

	@InjectMocks
	private DocumentService documentService;

	@Mock
	private PcuIndex pcuIndex;

	@Test
	public void shouldNotThrowExceptionWhenDeleted() throws PcuIndexException {
		Mockito.when(pcuIndex.deleteDocument(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(true);
		assertThatCode(() -> {
			documentService.deleteDocument("documents", "document", "documentId");
		}).doesNotThrowAnyException();
	}

	@Test
	public void shouldThrowExceptionWhenExceptionInIndexDelete() throws PcuIndexException {
		Mockito.when(pcuIndex.deleteDocument(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenThrow(PcuIndexException.class);
		assertThatThrownBy(() -> {
			documentService.deleteDocument("documents", "document", "documentId");
		}).isInstanceOf(PcuIndexException.class);
	}

	@Test
	public void shouldThrowExceptionWhenNotDeleted() throws PcuIndexException {
		Mockito.when(pcuIndex.deleteDocument(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(false);
		assertThatThrownBy(() -> {
			documentService.deleteDocument("documents", "document", "documentId");
		}).isInstanceOf(PcuIndexException.class);
	}

	@Test
	public void shouldNotThrowExceptionWhenCreated() throws PcuIndexException {
		Mockito.when(pcuIndex.createDocument(Mockito.any(JsonNode.class), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(true);
		assertThatCode(() -> {
			documentService.createDocument(new ObjectMapper().createObjectNode(), "documents", "document",
					"documentId");
		}).doesNotThrowAnyException();
	}

	@Test
	public void shouldThrowExceptionWhenExceptionInIndexCreate() throws PcuIndexException {
		Mockito.when(pcuIndex.createDocument(Mockito.any(JsonNode.class), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenThrow(PcuIndexException.class);
		assertThatThrownBy(() -> {
			documentService.createDocument(new ObjectMapper().createObjectNode(), "documents", "document",
					"documentId");
		}).isInstanceOf(PcuIndexException.class);
	}

	@Test
	public void shouldThrowExceptionWhenNotCreated() throws PcuIndexException {
		Mockito.when(pcuIndex.createDocument(Mockito.any(JsonNode.class), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(false);
		assertThatThrownBy(() -> {
			documentService.createDocument(new ObjectMapper().createObjectNode(), "documents", "document",
					"documentId");
		}).isInstanceOf(PcuIndexException.class);
	}

}
