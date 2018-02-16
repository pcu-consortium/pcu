package org.pcu.providers.metadata.api;

import io.swagger.annotations.Api;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * TODO Q also type param ?!
 *
 * @author mardut
 */
@Path("/metadata/api")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@Api(value = "metadata api")
public interface PcuMetadataApi {

	@Path("/metadata")
	@GET
	PcuMetadataResult extract(@QueryParam("url") String locator);

	// TODO allows to download URLs first, in order to upload their content and access their headers
   PcuMetadataResult extract(String fileName, String contentType, InputStream input);

}