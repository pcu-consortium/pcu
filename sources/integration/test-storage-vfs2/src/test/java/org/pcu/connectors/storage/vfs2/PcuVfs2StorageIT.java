package org.pcu.connectors.storage.vfs2;

/*-
 * #%L
 * PCU Integration test : storage vfs2
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





import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.apache.commons.vfs2.FileSystemException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.pcu.connectors.storage.PcuStorage;
import org.pcu.connectors.storage.PcuStorageException;
import org.pcu.integration.TemporaryFolderExtension;

@ExtendWith({ TemporaryFolderExtension.class })
@RunWith(JUnitPlatform.class)
public class PcuVfs2StorageIT {

	@RegisterExtension
	public TemporaryFolderExtension temporaryFolder = new TemporaryFolderExtension();

	private static PcuStorage pcuStorage;
	private String containerName;
	private String documentId;

	private final static String SOURCE_FILE = "file/20171206 POSS/PCU@POSS_20171206.pdf";

	@BeforeEach
	public void before() throws FileSystemException {
		containerName = "pcu-storage-test" + UUID.randomUUID().toString();
		documentId = UUID.randomUUID().toString();
		pcuStorage = new PcuVfs2Storage.Builder("file:/" + temporaryFolder.getRoot().getPath()).build();
	}

	@Test
	public void storageContainerVfs2OnFilesystem() throws IOException, InterruptedException {

		assertThat(pcuStorage).isNotNull();

		// create container
		assertThatCode(() -> {
			boolean created = pcuStorage.createContainer(containerName);
			assertThat(created).isTrue();
		}).doesNotThrowAnyException();

		// try create container again
		assertThatCode(() -> {
			boolean created = pcuStorage.createContainer(containerName);
			assertThat(created).isFalse();
		}).doesNotThrowAnyException();

		// delete container
		assertThatCode(() -> {
			boolean deleted = pcuStorage.deleteContainer(containerName);
			assertThat(deleted).isTrue();
		}).doesNotThrowAnyException();
	}

	@Test
	public void storageVfs2OnFilesystemOK() throws IOException, InterruptedException, URISyntaxException {

		assertThat(pcuStorage).isNotNull();

		// create container
		assertThatCode(() -> {
			boolean created = pcuStorage.createContainer(containerName);
			assertThat(created).isTrue();
		}).doesNotThrowAnyException();

		// create file in container
		URI sourceUriFile = PcuVfs2StorageIT.class.getClassLoader().getResource(SOURCE_FILE).toURI();
		File sourceFile = new File(sourceUriFile);
		double sourceFileLength = sourceFile.length();

		assertThatCode(() -> {
			InputStream sourceInputStream = PcuVfs2StorageIT.class.getClassLoader().getResourceAsStream(SOURCE_FILE);
			boolean created = pcuStorage.upload(sourceInputStream, containerName, documentId);
			assertThat(created).isTrue();
		}).doesNotThrowAnyException();

		Path uploadedFile = Paths.get(temporaryFolder.getRoot().getPath(), containerName, documentId);
		assertThat(uploadedFile).isNotNull();
		double uploadedFileLength = uploadedFile.toFile().length();
		assertThat(uploadedFileLength).isEqualTo(sourceFileLength);

		// try create file in container where file already exists
		assertThatCode(() -> {
			InputStream sourceInputStream = PcuVfs2StorageIT.class.getClassLoader()
					.getResourceAsStream("file/20171206 POSS/PCU@POSS_20171206.pdf");
			boolean created = pcuStorage.upload(sourceInputStream, containerName, documentId);
			assertThat(created).isFalse();
		}).doesNotThrowAnyException();

		// try create file in container that does not exists
		assertThatThrownBy(() -> {
			InputStream sourceInputStream = PcuVfs2StorageIT.class.getClassLoader().getResourceAsStream(SOURCE_FILE);
			pcuStorage.upload(sourceInputStream, "fakeContainerName", documentId);
		}).isInstanceOf(PcuStorageException.class).hasMessageContaining("container does not exists");

		// dowload file
		Path dowloadedFilePath = Paths.get(temporaryFolder.getRoot().getPath(), containerName, documentId + "copy");
		assertThatCode(() -> {
			InputStream downloadedInputStream = pcuStorage.download(containerName, documentId);
			assertThat(downloadedInputStream).isNotNull();
			File targetFile = dowloadedFilePath.toFile();
			Files.copy(downloadedInputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			downloadedInputStream.close();

		}).doesNotThrowAnyException();

		assertThat(dowloadedFilePath).exists();
		double dowloadedFileLength = dowloadedFilePath.toFile().length();
		assertThat(dowloadedFileLength).isEqualTo(sourceFileLength);
		assertThat(dowloadedFileLength).isEqualTo(uploadedFileLength);

		// try download a file that does not exists
		assertThatThrownBy(() -> {
			pcuStorage.download(containerName, "fakeDocumentId");
		}).isInstanceOf(PcuStorageException.class).hasMessageContaining("file does not exists");

		// try download a file from a container that does not exists
		assertThatThrownBy(() -> {
			pcuStorage.download("fakeContainerName", documentId);
		}).isInstanceOf(PcuStorageException.class).hasMessageContaining("container does not exists");

		// delete file
		assertThatCode(() -> {
			boolean deleted = pcuStorage.delete(containerName, documentId);
			assertThat(deleted).isTrue();
		}).doesNotThrowAnyException();

		// try delete file that does not exists
		assertThatCode(() -> {
			boolean deleted = pcuStorage.delete(containerName, documentId);
			assertThat(deleted).isFalse();
		}).doesNotThrowAnyException();

		// try delete file from a container that does not exists
		assertThatThrownBy(() -> {
			pcuStorage.delete("fakeContainerName", documentId);
		}).isInstanceOf(PcuStorageException.class).hasMessageContaining("container does not exists");

	}

	@AfterEach
	public void after() {
		// TODO cleaning
	}

}
