package org.pcu.connectors.collectors.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.pcu.connectors.indexer.PcuIndexer;
import org.springframework.beans.factory.annotation.Autowired;

import com.norconex.committer.core.CommitterException;
import com.norconex.committer.core.ICommitter;
import com.norconex.commons.lang.map.Properties;

public class PcuFilesystemCommitter implements ICommitter {

	private static final Logger LOGGER = LogManager.getLogger(PcuFilesystemCommitter.class);

	/** Default committer directory */
	public static final String DEFAULT_DIRECTORY = "pcu-committer-json";

	@Autowired
	private PcuIndexer pcuIndexer;

	private List<JSONObject> addJSON = new ArrayList<>();
	private List<JSONObject> removeJSON = new ArrayList<>();

	@Override
	public void add(String reference, InputStream content, Properties metadata) {
		LOGGER.debug("Add document with reference " + reference);
		try {
			JSONObject doc = new JSONObject();
			doc.put("id", reference);

			StringWriter writer = new StringWriter();
			metadata.storeToJSON(writer);
			doc.put("metadata", writer.toString());

			doc.put("command", "add");

			addJSON.add(doc);

		} catch (IOException | JSONException e) {
			throw new CommitterException("Cannot create Json document to add for reference: " + reference, e);
		}

	}

	@Override
	public void remove(String reference, Properties metadata) {
		LOGGER.debug("Remove document with reference " + reference);
		try {
			JSONObject doc = new JSONObject();
			doc.put("id", reference);
			removeJSON.add(doc);

		} catch (/* IOException | */ JSONException e) {
			throw new CommitterException("Cannot create Json document to remove for reference: " + reference, e);
		}

	}

	@Override
	public void commit() {
		
		LOGGER.debug("Commit documents");
		// send to PCU, but for now log
		LOGGER.info("Commit added documents");
		addJSON.forEach(doc -> LOGGER.info(doc.toString()));
		addJSON.clear();
		LOGGER.info("Commit removed documents");
		removeJSON.forEach(doc -> LOGGER.info(doc.toString()));
		removeJSON.clear();

	}

	public void setPcuIndexer(PcuIndexer pcuIndexer) {
		this.pcuIndexer = pcuIndexer;
	}
	
}
