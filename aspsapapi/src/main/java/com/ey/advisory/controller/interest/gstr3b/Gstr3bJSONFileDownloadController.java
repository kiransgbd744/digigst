/**
 * 
 */
package com.ey.advisory.controller.interest.gstr3b;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.sql.Clob;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.Gstr3BSaveStatusEntity;
import com.ey.advisory.app.data.repositories.client.Gstr3BSaveStatusRepository;
import com.ey.advisory.common.CombineAndZipTxtFiles;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ravindra V S
 *
 */

@RestController
@Slf4j
public class Gstr3bJSONFileDownloadController {

	@Autowired
	@Qualifier("gstr3BSaveStatusRepository")
	Gstr3BSaveStatusRepository repo;

	@Autowired
	CombineAndZipTxtFiles combineAndZipCsvFiles;

	@RequestMapping(value = "/ui/gstr2bJsonDownloadDocument", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void fileDownloads(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		LOGGER.debug("inside fileDownloads method and file type is {} ",
				"gstr2bJsonDownload");

		File tempDir = null;
		String responseFullPath = null;
		String requestFullPath = null;

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();

		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		String requestFileName = null;
		String responseFileName = null;

		JsonObject errorResp = new JsonObject();

		String gstin = json.get("gstin").getAsString();
		String taxPeriod = json.get("taxPeriod").getAsString();
		String createdOn = json.get("createdOn").getAsString();

		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse(createdOn, formatter);

		LocalDateTime utcDateTime = EYDateUtil.toUTCDateTimeFromIST(dateTime);

		StringBuffer buffer = null;
		StringBuffer pollingBuffer = null;
		Clob clobRequestPayloadResp = null;
		Clob clobPollingRequestPayloadResp = null;
		String stringResponsePayloadResp = null;
		FileOutputStream requestOutputStream = null;
		FileOutputStream responseOutputStream = null;
		InputStream inputStream = null;

		tempDir = createTempDir(gstin, taxPeriod);

		try {

			if (Strings.isNullOrEmpty(taxPeriod) || gstin.isEmpty()) {
				String msg = "Return Period And Gstin cannot be empty or Date format is not correct";
				LOGGER.error(msg);
			}
			if (Strings.isNullOrEmpty(taxPeriod) || !gstin.isEmpty()) {

				if (Strings.isNullOrEmpty(taxPeriod) || !gstin.isEmpty()) {

					Gstr3BSaveStatusEntity entity = repo
							.findByGstinAndTaxPeriodAndCreatedOn(gstin,
									taxPeriod, utcDateTime);

					clobRequestPayloadResp = entity != null
							? entity.getSaveRequestPayload() : null;

					// converting Response clob to String'
					if (clobRequestPayloadResp != null) {
						Reader respReder = clobRequestPayloadResp
								.getCharacterStream();

						buffer = new StringBuffer();
						int ch = 0;
						while ((ch = respReder.read()) != -1) {
							buffer.append("" + (char) ch);
						}
						respReder.close();
					}
					String stringRequestPayloadString = buffer != null
							? buffer.toString() : null;

					stringResponsePayloadResp = entity != null
							? entity.getSaveResponsePayload() : null;

					// Request Payload
					requestFileName = "requestPayload";
					requestFullPath = tempDir.getAbsolutePath() + File.separator
							+ requestFileName + ".txt";

					 requestOutputStream = new FileOutputStream(
							requestFullPath, true);
					byte[] strRequestPayloadToBytes = stringRequestPayloadString
							.getBytes();
					requestOutputStream.write(strRequestPayloadToBytes);
					requestOutputStream.close();

					// Polling
					clobPollingRequestPayloadResp = entity != null
							? (entity.getPollingResponsePayload() != null
									? entity.getPollingResponsePayload() : null)
							: null;

					if (clobPollingRequestPayloadResp != null) {
						Reader respReder = clobPollingRequestPayloadResp
								.getCharacterStream();

						pollingBuffer = new StringBuffer();
						int ch = 0;
						while ((ch = respReder.read()) != -1) {
							pollingBuffer.append("" + (char) ch);
						}
						respReder.close();
					}
					String stringPollingRequestPayloadResp = pollingBuffer != null
							? pollingBuffer.toString() : null;

					// Response Payload
					responseFileName = "responsePayload";
					responseFullPath = tempDir.getAbsolutePath()
							+ File.separator + responseFileName + ".txt";
					 responseOutputStream = new FileOutputStream(
							responseFullPath, true);

					byte[] strResponsePayloadToBytes = null;
					if (stringPollingRequestPayloadResp != null) {
						strResponsePayloadToBytes = stringPollingRequestPayloadResp
								.getBytes();
					} else {
						strResponsePayloadToBytes = stringResponsePayloadResp
								.getBytes();
					}
					responseOutputStream.write(strResponsePayloadToBytes);
					responseOutputStream.close();

				}

				// Zipping
				String zipFileName = null;
				File zipFile = null;
				if (tempDir.list().length > 0) {
					Long taxPeriodValue = Long.valueOf(taxPeriod);
					zipFileName = combineAndZipCsvFiles.zipfolder(
							taxPeriodValue, tempDir, gstin, "GSTR3B", "");

					zipFile = new File(tempDir, zipFileName);
				}

			    inputStream = FileUtils.openInputStream(zipFile);
				response.setContentType("application/csv");
				response.setHeader("Content-Disposition", String
						.format("attachment; filename =%s ", zipFileName));
				IOUtils.copy(inputStream, response.getOutputStream());
				response.flushBuffer();

			}
		} catch (Exception ex) {
			String msg = "Error occured while downloading the Zip File ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
		}finally {
			// sonar obs
			if (requestOutputStream != null) {
				try {
					requestOutputStream.close();
				} catch (IOException e) {
		LOGGER.debug("An error occurred while closing requestOutputStream", e);

				}
			}if (responseOutputStream != null) {
				try {
					responseOutputStream.close();
				} catch (IOException e) {
		LOGGER.debug("An error occurred while closing responseOutputStream", e);

				}
			}if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
		LOGGER.debug("An error occurred while closing inputStream", e);

				}
			}
		}
	}

	private static File createTempDir(String gstin, String taxperiod)
			throws IOException {

		String tempFolderPrefix = "JSONRequestResponsePayload" + "_" + gstin
				+ "_" + taxperiod;
		return Files.createTempDirectory(tempFolderPrefix).toFile();
	}

}
