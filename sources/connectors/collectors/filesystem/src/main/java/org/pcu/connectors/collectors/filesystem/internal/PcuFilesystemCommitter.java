package org.pcu.connectors.collectors.filesystem.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.pcu.platform.IngestDocumentRequest;
import org.pcu.platform.client.PcuPlatformClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.norconex.committer.core.CommitterException;
import com.norconex.committer.core.ICommitter;
import com.norconex.commons.lang.map.Properties;

public class PcuFilesystemCommitter implements ICommitter {

	private static final Logger LOGGER = LogManager.getLogger(PcuFilesystemCommitter.class);

	/** Default committer directory */
	public static final String DEFAULT_DIRECTORY = "pcu-committer-json";

	private PcuPlatformClient pcuPlatformclient;

	private List<PcuFilesystemDocument> addJSON = new ArrayList<>();
	private List<PcuFilesystemDocument> removeJSON = new ArrayList<>();

	@Override
	public void add(String reference, InputStream content, Properties metadata) {
		LOGGER.debug("Add document with reference " + reference);
		try {
			PcuFilesystemDocument doc = new PcuFilesystemDocument();

			doc.setId(reference);
			doc.setIndex("files");
			doc.setType("file");

			StringWriter writer = new StringWriter();
			metadata.storeToJSON(writer);
			doc.setMetadata(new ObjectMapper().readTree(writer.toString()));

			addJSON.add(doc);

		} catch (IOException e) {
			throw new CommitterException("Cannot create Json document to add for reference: " + reference, e);
		}

	}

	@Override
	public void remove(String reference, Properties metadata) {
		LOGGER.debug("Remove document with reference " + reference);
		PcuFilesystemDocument doc = new PcuFilesystemDocument();
		doc.setId(reference);
		doc.setIndex("files");
		doc.setType("file");
		removeJSON.add(doc);
	}

	@Override
	public void commit() {

		LOGGER.debug("Commit documents");
		// send to PCU, but for now log
		LOGGER.info("Commit added documents");
		addJSON.forEach(doc -> LOGGER.info(doc.toString()));
		addJSON.forEach(doc -> {
			IngestDocumentRequest ingestDocumentRequest = new IngestDocumentRequest();
			ingestDocumentRequest.setId(doc.getId());
			ingestDocumentRequest.setIndex(doc.getIndex());
			ingestDocumentRequest.setType(doc.getType());
			ingestDocumentRequest.setDocument(doc.getMetadata());
			pcuPlatformclient.ingest(ingestDocumentRequest);
		});
		addJSON.clear();
		LOGGER.info("Commit removed documents");
		removeJSON.forEach(doc -> LOGGER.info(doc.toString()));
		removeJSON.forEach(doc -> {
			pcuPlatformclient.deleteDocument(doc.getIndex(), doc.getType(), doc.getId());
		});
		removeJSON.clear();
	}

	public void setPcuPlatformClient(PcuPlatformClient pcuPlatformclient) {
		this.pcuPlatformclient = pcuPlatformclient;
	}

}