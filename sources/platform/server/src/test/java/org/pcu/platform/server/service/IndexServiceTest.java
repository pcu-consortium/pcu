package org.pcu.platform.server.service;

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
