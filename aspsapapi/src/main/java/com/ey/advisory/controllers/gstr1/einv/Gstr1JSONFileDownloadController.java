/**
 * 
 */
package com.ey.advisory.controllers.gstr1.einv;

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

import com.ey.advisory.common.CombineAndZipTxtFiles;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
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
public class Gstr1JSONFileDownloadController {

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	Gstr1BatchRepository repo;

	@Autowired
	CombineAndZipTxtFiles combineAndZipCsvFiles;

	@RequestMapping(value = "/ui/gstr1JsonDownloadDocument", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void fileDownloads(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		LOGGER.debug("inside fileDownloads method and file type is {} ",
				"gstr1JsonDownload");

		File tempDir = null;
		String responseFullPath = null;
		String requestFullPath = null;

		JsonObject errorResp = new JsonObject();

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();

		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		String requestFileName = null;
		String responseFileName = null;

		String supplierGstin = json.get("supplierGstin").getAsString();
		String returnPeriod = json.get("returnPeriod").getAsString();
		String createdOn = json.get("createdOn").getAsString();
		String section = json.get("section").getAsString();

		String returnType = json.has("returnType")
				? json.get("returnType").getAsString() : "GSTR1";

		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse(createdOn, formatter);

		StringBuffer buffer = null;
		StringBuffer pollingBuffer = null;
		Clob clobRequestPayloadResp = null;
		Clob clobResponsePayloadResp = null;
		FileOutputStream requestOutputStream = null;
		FileOutputStream responseOutputStream = null;
		InputStream inputStream = null;

		tempDir = createTempDir(supplierGstin, returnPeriod);

		try {
			if (Strings.isNullOrEmpty(returnPeriod)
					|| supplierGstin.isEmpty()) {
				String msg = "Return Period And Gstin cannot be empty or Date format is not correct";
				LOGGER.error(msg);
			}
			if (Strings.isNullOrEmpty(returnPeriod)
					|| !supplierGstin.isEmpty()) {

				if (Strings.isNullOrEmpty(returnPeriod)
						|| !supplierGstin.isEmpty()) {

					Gstr1SaveBatchEntity entity = null;

					if (returnType != null
							&& APIConstants.GSTR1A.equalsIgnoreCase(returnType)) {

						entity = repo
								.findByGstinAndReturnPeriodAndRefIdAndSectionGstr1A(
										supplierGstin, returnPeriod, dateTime,
										section, APIConstants.GSTR1A);
					} else {
						entity = repo
								.findByGstinAndReturnPeriodAndCreatedOnAndSection(
										supplierGstin, returnPeriod, dateTime,
										section);
					}

					clobRequestPayloadResp = entity != null
							? entity.getSaveRequestPayload() : null;

					// converting clob to String'
					if (clobRequestPayloadResp != null) {
						
						LOGGER.debug(" INSIDE block A");
						Reader respReder = clobRequestPayloadResp
								.getCharacterStream();

						buffer = new StringBuffer();
						int ch = 0;
						while ((ch = respReder.read()) != -1) {
							buffer.append("" + (char) ch);
						}
						respReder.close();
					}
					
					LOGGER.debug(" buffer {} ",buffer );
					
					String stringRequestPayloadString = buffer != null
							? buffer.toString() : null;

					clobResponsePayloadResp = entity != null
							? (entity.getGetResponsePayload() != null
									? entity.getGetResponsePayload()
									: entity.getSaveResponsePayload())
							: null;

					// converting clob to String'
					if (clobResponsePayloadResp != null) {
						Reader respReder = clobResponsePayloadResp
								.getCharacterStream();

						pollingBuffer = new StringBuffer();
						int ch = 0;
						while ((ch = respReder.read()) != -1) {
							pollingBuffer.append("" + (char) ch);
						}
						respReder.close();
					}
					String stringResponsePayloadString = pollingBuffer != null
							? pollingBuffer.toString() : null;

					// Request Payload
					requestFileName = "requestPayload";
					requestFullPath = tempDir.getAbsolutePath() + File.separator
							+ requestFileName + ".txt";

					 requestOutputStream = new FileOutputStream(
							requestFullPath, true);
					byte[] strRequestPayloadToBytes = stringRequestPayloadString
							.getBytes();
					requestOutputStream.write(strRequestPayloadToBytes);
					//requestOutputStream.close();

					// Response Payload
					responseFileName = "responsePayload";
					responseFullPath = tempDir.getAbsolutePath()
							+ File.separator + responseFileName + ".txt";
					responseOutputStream = new FileOutputStream(
							responseFullPath, true);
					
					byte[] strResponsePayloadToBytes = null;
					
					if (stringResponsePayloadString != null) {
						strResponsePayloadToBytes = stringResponsePayloadString
								.getBytes();
					} else {
						
						strResponsePayloadToBytes = new byte[0];
					}
					/*byte[] strResponsePayloadToBytes = stringResponsePayloadString
							.getBytes();*/
					responseOutputStream.write(strResponsePayloadToBytes);
					//responseOutputStream.close();

				}

				// Zipping
				String zipFileName = null;
				File zipFile = null;
				if (tempDir.list().length > 0) {
					Long taxPeriodValue = Long.valueOf(returnPeriod);
					
					if (returnType != null
							&& APIConstants.GSTR1A.equalsIgnoreCase(returnType)) {

					zipFileName = combineAndZipCsvFiles.zipfolder(
							taxPeriodValue, tempDir, supplierGstin, "GSTR1A",
							"_" + section);
					}
					else
					{

						zipFileName = combineAndZipCsvFiles.zipfolder(
								taxPeriodValue, tempDir, supplierGstin, "GSTR1",
								"_" + section);
						
						
					}
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
