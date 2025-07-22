package com.ey.advisory.app.controllers.ims;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipTxtFiles;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ImsErrorCountJSONFileDownloadController {

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	Gstr1BatchRepository repo;

	@Autowired
	CombineAndZipTxtFiles combineAndZipCsvFiles;

	@Autowired
	@Qualifier("ImsSaveErrorReportDownloadServiceImpl")
	ImsSaveErrorReportDownloadServiceImpl imsSaveErrorReportDownloadServiceImpl;

	@RequestMapping(value = "/ui/imsErrorCountJsonDownloadDocument", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE})
	public void fileDownloads(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		LOGGER.debug("inside fileDownloads method and file type is {} ",
				"imsJsonDownload");
		Gson gson = GsonUtil.newSAPGsonInstance();
		File tempDir = null;
		//String responseFullPath = null;

		JsonObject errorResp = new JsonObject();

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();

		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		//String responseFileName = null;

		String gstin = json.get("gstin").getAsString();
		// String createdOn = json.get("createdOn").getAsString();
		String section = json.get("section").getAsString();
		String refId = json.get("refId").getAsString();

		String selectedType = json.get("type").getAsString();
		// String selectedType="XLSX";
		//StringBuffer pollingBuffer = null;
		//Clob clobResponsePayloadResp = null;
		//InputStream inputStream = null;

		//FileOutputStream responseOutputStream = null;

		tempDir = createTempDir(gstin);
		
		if ("JSON".equals(selectedType)) {
		    try {
		        if (gstin.isEmpty()) {
		            String msg = "Return Period And Gstin cannot be empty or Date format is not correct";
		            LOGGER.error(msg);
		        }

		        if (!gstin.isEmpty()) {
		            Gstr1SaveBatchEntity entity = repo.findByGstinAndSectionAndRefId(gstin, section, refId);

		            Clob clobResponsePayloadResp = (entity != null) ? 
		                ((entity.getGetResponsePayload() != null) ? entity.getGetResponsePayload() : entity.getSaveResponsePayload()) 
		                : null;

		            String stringResponsePayloadString = null;
		            
		            // Convert CLOB to String
		            if (clobResponsePayloadResp != null) {
		                try (Reader respReader = clobResponsePayloadResp.getCharacterStream();
		                     StringWriter writer = new StringWriter()) {
		                    IOUtils.copy(respReader, writer);
		                    stringResponsePayloadString = writer.toString();
		                }
		            }

		            // Define the naming convention: RefID_IMS_GSTN_Error_Report.json
		            String responseFileName = String.format("%s_IMS_GSTN_Error_Report.json", refId);
		            String responseFullPath = tempDir.getAbsolutePath() + File.separator + responseFileName;

		            // Write the response payload to the JSON file
		            try (FileOutputStream responseOutputStream = new FileOutputStream(responseFullPath, true)) {
		                byte[] strResponsePayloadToBytes = (stringResponsePayloadString != null) 
		                    ? stringResponsePayloadString.getBytes(StandardCharsets.UTF_8) 
		                    : new byte[0]; 

		                responseOutputStream.write(strResponsePayloadToBytes);
		            }

		            // Zipping process
		            String zipFileName = null;
		            File zipFile = null;
		            if (tempDir.list().length > 0) {
		                zipFileName = combineAndZipCsvFiles.zipfolder(null, tempDir, gstin, "IMS", "_" + section);
		                zipFile = new File(tempDir, zipFileName);
		            }

		            try (InputStream inputStream = FileUtils.openInputStream(zipFile)) {
		                response.setContentType("application/zip");
		                response.setHeader("Content-Disposition", String.format("attachment; filename=%s", zipFileName));
		                IOUtils.copy(inputStream, response.getOutputStream());
		                response.flushBuffer();
		            }
		        }
		    } catch (Exception ex) {
		        String msg = "Error occurred while downloading the Zip File";
		        LOGGER.error(msg, ex);
		        errorResp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		    }
		}
		
		
		// xlsx report download
		else {

			try {
				if (gstin.isEmpty()) {
					String msg = "Return Period And Gstin cannot be empty or Date format is not correct";
					LOGGER.error(msg);
				}

				Gstr1SaveBatchEntity entity = null;
				List<ImsErrorReportResponseDownloadDto> errorReports = new ArrayList<>();
				ImsErrorReportResponseDownloadDto dto = new ImsErrorReportResponseDownloadDto();
				
				entity = repo.findByGstinAndSectionAndRefId(gstin, section,
						refId);

				Clob clobResponsePayloadResp = entity != null
						? (entity.getGetResponsePayload() != null
								? entity.getGetResponsePayload()
								: entity.getSaveResponsePayload())
						: null;
				if (clobResponsePayloadResp != null) {
					
					Workbook workbook = imsSaveErrorReportDownloadServiceImpl
							.convertPayload(clobResponsePayloadResp,
									errorReports, dto);
				    // Modify file name to include RefID
				    String fileName = String.format("%s_IMS_GSTN_Error_Report.xlsx", refId);

				    if (workbook != null) {
				        response.setContentType("APPLICATION/OCTET-STREAM");
				        response.setHeader("Content-Disposition", 
				                String.format("attachment; filename=\"%s\"", fileName));

				        try {
				            workbook.save(response.getOutputStream(), SaveFormat.XLSX);
				        } catch (Exception e) {
				            String msg = String.format(
				                    "Exception occurred while generating the Excel sheet: %s", e.getMessage());
				            LOGGER.error(msg);
				            throw new AppException(e.getMessage(), e);
				        }
				        response.getOutputStream().flush();
				    }
			

				}
			}

			catch (Exception e) {
				String msg = String.format(
						"Exception occured while "
								+ "generating the excel sheet, %s ",
						e.getMessage());
				LOGGER.error(msg);
				throw new AppException(e.getMessage(), e);
			} finally {

			}

		}

	}

	private static File createTempDir(String gstin) throws IOException {

		String tempFolderPrefix = "JSONRequestResponsePayload" + "_" + gstin;
		return Files.createTempDirectory(tempFolderPrefix).toFile();
	}

}
