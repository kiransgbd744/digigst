package com.ey.advisory.controller.drc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import com.ey.advisory.app.data.entities.client.SignAndFileEntity;
import com.ey.advisory.app.data.entities.drc.TblDrcSaveStatus;
import com.ey.advisory.app.data.repositories.client.SignAndFileRepository;
import com.ey.advisory.app.data.repositories.client.drc.TblDrcSaveStatusRepo;
import com.ey.advisory.common.CombineAndZipTxtFiles;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class Gstr3bDrcJSONFileDownloadController {

	@Autowired
	@Qualifier("TblDrcSaveStatusRepo")
	TblDrcSaveStatusRepo repo;

	@Autowired
	CombineAndZipTxtFiles combineAndZipCsvFiles;

	@Autowired
	@Qualifier("SignAndFileRepository")
	SignAndFileRepository signAndFileRepo;

	/*
	 * public static void main(String[] args) {
	 * 
	 * JsonObject requestObject = JsonParser.
	 * parseString("{\n    \"req\": {\n        \"gstin\": \"33GSPTN0481G1ZA\",\n        \"taxPeriod\": \"032022\",\n        \"createdOn\": \"2023-08-07T12:24:49.130\",\n        \"dataType\": \"SaveStatus\" \n    }\n}\n"
	 * ) .getAsJsonObject(); JsonObject json =
	 * requestObject.get("req").getAsJsonObject();
	 * 
	 * String tenantCode = TenantContext.getTenantId();
	 * LOGGER.debug("Tenant Id Is {}", tenantCode);
	 * 
	 * 
	 * 
	 * JsonObject errorResp = new JsonObject();
	 * 
	 * String gstin = json.get("gstin").getAsString(); String taxPeriod =
	 * json.get("taxPeriod").getAsString(); String createdOn =
	 * json.get("createdOn").getAsString(); String dataType =
	 * json.get("dataType").getAsString();
	 * 
	 * DateTimeFormatter formatter = DateTimeFormatter
	 * .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"); LocalDateTime dateTime =
	 * LocalDateTime.parse(createdOn, formatter);
	 * 
	 * LocalDateTime utcDateTime = EYDateUtil.toUTCDateTimeFromIST(dateTime);
	 * 
	 * System.out.println(utcDateTime);
	 * 
	 * }
	 */

	@RequestMapping(value = "/ui/gstr3bDrcJsonDownloadDocument", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void fileDownloads(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		LOGGER.debug("inside fileDownloads method and file type is {} ",
				"gstr2bJsonDownload");

		File tempDir = null;
		String responseFullPath = null;
		String requestFullPath = null;

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();

		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		JsonObject errorResp = new JsonObject();

		String gstin = json.get("gstin").getAsString();
		String taxPeriod = json.get("taxPeriod").getAsString();
		String createdOn = json.get("createdOn").getAsString();
		String dataType = json.get("dataType").getAsString();

		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse(createdOn, formatter);

		LocalDateTime utcDateTime = EYDateUtil.toUTCDateTimeFromIST(dateTime);

		String requestFileName = null;
		String responseFileName = null;
		StringBuffer buffer = null;
		Clob clobRequestPayloadResp = null;
		String stringResponsePayloadResp = null;
		tempDir = createTempDir(gstin, taxPeriod);
		
		InputStream inputStream = null;

		FileOutputStream responseOutputStream = null;
		FileOutputStream requestOutputStream = null;

		try {

			if (Strings.isNullOrEmpty(taxPeriod)
					|| Strings.isNullOrEmpty(gstin)) {
				String msg = "Return Period And Gstin cannot be empty or Date format is not correct";
				LOGGER.error(msg);
				return;
			}
			TblDrcSaveStatus entity = repo.findByGstinAndTaxPeriodAndCreatedOn(
					gstin, taxPeriod, utcDateTime);
			if ("SaveStatus".equalsIgnoreCase(dataType)) {

				/*
				 * clobRequestPayloadResp = entity != null ?
				 * entity.getSaveRequestPayload() : null;
				 * 
				 * if (clobRequestPayloadResp != null) { Reader respReder =
				 * clobRequestPayloadResp .getCharacterStream();
				 * 
				 * buffer = new StringBuffer(); int ch = 0; while ((ch =
				 * respReder.read()) != -1) { buffer.append("" + (char) ch); }
				 * respReder.close(); } String stringRequestPayloadReq = buffer
				 * != null ? buffer.toString() : null;
				 */

				String stringRequestPayloadReq = entity != null
						? entity.getSaveRequestPayload() : null;

				String stringResponsePayloadRespp = entity != null
						? entity.getSaveResponsePayload() : null;

				// Request Payload
				requestFileName = "requestPayload";
				requestFullPath = tempDir.getAbsolutePath() + File.separator
						+ requestFileName + ".txt";

				requestOutputStream = new FileOutputStream(
						requestFullPath, true);
				
				byte[] strRequestPayloadToBytes = null;

				
				if (stringRequestPayloadReq != null) {
					strRequestPayloadToBytes = stringRequestPayloadReq
							.getBytes();
				} else {
					// Handle the null case, e.g., assign an empty byte
					// array or log a message
					strRequestPayloadToBytes = new byte[0]; // Default to
																// empty
																// array
				}

				
				/*byte[] strRequestPayloadToBytes = stringRequestPayloadReq
						.getBytes();*/
				requestOutputStream.write(strRequestPayloadToBytes);
				//requestOutputStream.close();

				// Response Payload
				responseFileName = "responsePayload";
				responseFullPath = tempDir.getAbsolutePath() + File.separator
						+ responseFileName + ".txt";
				responseOutputStream = new FileOutputStream(
						responseFullPath, true);

				byte[] strResponsePayloadToBytes = null;
				
				if (stringResponsePayloadRespp != null) {
					strResponsePayloadToBytes = stringResponsePayloadRespp
							.getBytes();
				} else {
					// Handle the null case, e.g., assign an empty byte
					// array or log a message
					strResponsePayloadToBytes = new byte[0]; // Default to
																// empty
																// array
				}

				/*strResponsePayloadToBytes = stringResponsePayloadRespp
						.getBytes();*/

				responseOutputStream.write(strResponsePayloadToBytes);
				//responseOutputStream.close();

			}

			/*
			 * if(entity.getStatus()==null ||
			 * !entity.getStatus().equalsIgnoreCase("failed")){ return; }
			 */
			if ("ErrorSaveStatus".equalsIgnoreCase(dataType)) {

				stringResponsePayloadResp = entity != null
						? entity.getSaveResponsePayload() : null;

				// Response Payload

				responseFileName = "responsePayload";
				responseFullPath = tempDir.getAbsolutePath() + File.separator
						+ responseFileName + ".txt";
				responseOutputStream = new FileOutputStream(
						responseFullPath, true);

				byte[] strResponsePayloadToBytes = null;
				
				if (stringResponsePayloadResp != null) {
					strResponsePayloadToBytes = stringResponsePayloadResp
							.getBytes();
				} else {
					// Handle the null case, e.g., assign an empty byte
					// array or log a message
					strResponsePayloadToBytes = new byte[0]; // Default to
																// empty
																// array
				}

				/*strResponsePayloadToBytes = stringResponsePayloadResp
						.getBytes();*/

				responseOutputStream.write(strResponsePayloadToBytes);
				//responseOutputStream.close();

			}
			if ("ErrorFiledStatus".equalsIgnoreCase(dataType)) {

				SignAndFileEntity entty = signAndFileRepo
						.findByGstinAndTaxPeriodAndReturnTypeAndCreatedOn(gstin,
								taxPeriod, APIIdentifiers.DRC, utcDateTime);

				stringResponsePayloadResp = entty != null ? entty.getErrorMsg()
						: null;

				// Response Payload
				responseFileName = "responsePayload";
				responseFullPath = tempDir.getAbsolutePath() + File.separator
						+ responseFileName + ".txt";
				responseOutputStream = new FileOutputStream(
						responseFullPath, true);

				byte[] strResponsePayloadToBytes = null;
				
				if (stringResponsePayloadResp != null) {
					strResponsePayloadToBytes = stringResponsePayloadResp
							.getBytes();
				} else {
					// Handle the null case, e.g., assign an empty byte
					// array or log a message
					strResponsePayloadToBytes = new byte[0]; // Default to
																// empty
																// array
				}

				/*strResponsePayloadToBytes = stringResponsePayloadResp
						.getBytes();*/

				responseOutputStream.write(strResponsePayloadToBytes);
				//responseOutputStream.close();

			}

			// Zipping
			String zipFileName = null;
			File zipFile = null;
			if (tempDir.list().length > 0) {
				Long taxPeriodValue = Long.valueOf(taxPeriod);
				zipFileName = combineAndZipCsvFiles.zipfolder(taxPeriodValue,
						tempDir, gstin, "DRC01B", "");

				zipFile = new File(tempDir, zipFileName);
			}

			inputStream = FileUtils.openInputStream(zipFile);
			response.setContentType("application/csv");
			response.setHeader("Content-Disposition",
					String.format("attachment; filename =%s ", zipFileName));
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();

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
