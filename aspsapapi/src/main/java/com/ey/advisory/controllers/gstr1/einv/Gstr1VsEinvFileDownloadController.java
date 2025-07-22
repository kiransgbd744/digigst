/**
 * 
 */
package com.ey.advisory.controllers.gstr1.einv;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.gstr1.einv.Gstr1vsEInvExSaveRespReportsService;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@RestController
@Slf4j
public class Gstr1VsEinvFileDownloadController {

	@Autowired
	@Qualifier("Gstr1vsEInvExSaveRespReportsService")
	private Gstr1vsEInvExSaveRespReportsService downloadService;

	@RequestMapping(value = "/ui/gstr1VsEinvReportsDownload", method 
			= RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void fileDownloads(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {
		LOGGER.debug("inside fileDownloads method and file type is {} ",
				"Gstr2ReconUserResponseErrorDownloadController");

		Pair<String, File> filePair = null;
		String fileName = null;
		File file = null;
		InputStream inputStream = null;
		try {
			String tenantCode = TenantContext.getTenantId();
			LOGGER.debug("Tenant Id Is {}", tenantCode);

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject json = requestObject.get("req").getAsJsonObject();

			String fileId = json.get("fileId").getAsString();
			String type = json.get("type").getAsString();
			if(!type.equalsIgnoreCase("error")){
				return;
			}

			filePair = downloadService
					.generateErrorReport(Integer.valueOf(fileId));
			fileName = filePair.getValue0();
			file = filePair.getValue1();
		    inputStream = FileUtils.openInputStream(file);
			response.setContentType("application/csv");
			response.setHeader("Content-Disposition",
					String.format("attachment; filename =%s.%s ", fileName,"csv"));
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();
		} catch (Exception ex) {
			String msg = String.format("File Download failed");
			LOGGER.error(msg, ex);
		}

		finally {
			if (file != null && file.exists()) {
				try {
					FileUtils.deleteDirectory(file);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(String.format(
								"Deleted the Temp directory/Folder '%s'",
								file.getAbsolutePath()));
					}
				} catch (Exception ex) {
					String msg = String.format(
							"Failed to remove the temp "
									+ "directory created for zip: '%s'. This will "
									+ "lead to clogging of disk space.",
							file.getAbsolutePath());
					LOGGER.error(msg, ex);
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
			     LOGGER.debug("An error occurred while closing inputStream", e);

				}
			}
		}

	}
}
