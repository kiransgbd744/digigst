/**
 * 
 */
package com.ey.advisory.controller.days.revarsal180;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.FileStatusRepository;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.docs.dto.FileStatusReportDto;
import com.ey.advisory.app.services.search.filestatussearch.AsyncReportHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportTypeConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.service.days.revarsal180.Reversal180DaysDownloadService;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
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
public class Reversal180DaysFileDownloadController {

	@Autowired
	@Qualifier("Reversal180DaysDownloadService")
	private Reversal180DaysDownloadService downloadService;
	
	@Autowired
	AsyncJobsService asyncJobsService;
	
	@Autowired
	FileStatusRepository fileStatusRepo;

	@Autowired
	@Qualifier("AsyncFileStatusReportHandlerImpl")
	AsyncReportHandler asyncFileStatusReportHandler;
	
	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@RequestMapping(value = "/ui/reversal180ReportsDownload", 
			method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void fileDownloads(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {
		LOGGER.debug("inside fileDownloads method and file type is {} ",
				"Reversal180DaysFileDownloadController");

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
			if (type.equalsIgnoreCase("error")) {
				filePair = downloadService
						.generateErrorReport(Integer.valueOf(fileId));
				fileName = filePair.getValue0();
				file = filePair.getValue1();
				 inputStream = FileUtils.openInputStream(file);
				response.setContentType("application/csv");
				response.setHeader("Content-Disposition", String
						.format("attachment; filename =%s.%s ", fileName, "csv"));
				IOUtils.copy(inputStream, response.getOutputStream());
				response.flushBuffer();
			}
			
			else if (type.equalsIgnoreCase("processed")) {
				filePair = downloadService
						.generatePSDReport(Integer.valueOf(fileId));
				fileName = filePair.getValue0();
				file = filePair.getValue1();
				 inputStream = FileUtils.openInputStream(file);
				response.setContentType("application/csv");
				response.setHeader("Content-Disposition", String
						.format("attachment; filename =%s.%s ", fileName, "csv"));
				IOUtils.copy(inputStream, response.getOutputStream());
				response.flushBuffer();
			}
			
			else if (type.equalsIgnoreCase("totalrecords")) {
				filePair = downloadService
						.generateTotalReport(Integer.valueOf(fileId));
				fileName = filePair.getValue0();
				file = filePair.getValue1();
				 inputStream = FileUtils.openInputStream(file);
				response.setContentType("application/csv");
				response.setHeader("Content-Disposition", String
						.format("attachment; filename =%s.%s ", fileName, "csv"));
				IOUtils.copy(inputStream, response.getOutputStream());
				response.flushBuffer();
			}

			
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
					// ADD LOGGER TODO
				}
			}
		}

	}
	
	@RequestMapping(value = "/ui/reversal180ReportsDownloadAsync", 
			method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> fileDownloadsAsyc(@RequestBody String jsonParams) throws Exception {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Download Reversal180DaysFileDownload Controller";
			LOGGER.debug(msg);
		}

		JsonObject requestObject = JsonParser.parseString(jsonParams)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		String groupCode = TenantContext.getTenantId();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();
		Long fileId = json.get("fileId").getAsLong();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Request for Reversal180DaysFileDownload Controller: %s",
					json.toString());
			LOGGER.debug(msg);
		}

		try {
			FileStatusReportDto reqDto = gson.fromJson(json,
					FileStatusReportDto.class);

			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Setting request data to entity to SaveorPersist";
				LOGGER.debug(msg);
			}

			asyncFileStatusReportHandler.setDataToEntity(entity, reqDto);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Request for Reversal180DaysFileDownload Controller: %s",
						json.toString());
				LOGGER.debug(msg);
			}
			entity.setFileId(fileId);
			Optional<Gstr1FileStatusEntity> fileStatusEntity = fileStatusRepo
					.findById(reqDto.getFileId());
			if (fileStatusEntity.isPresent()) {
				entity.setUpldFileName(fileStatusEntity.get().getFileName());
			}
			entity.setDataType("GSTR3B");
			entity = fileStatusDownloadReportRepo.save(entity);

			Long id = entity.getId();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully saved to DB with Report Id : %d", id);
				LOGGER.debug(msg);
			}

			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);

			if ("GSTR3B".equalsIgnoreCase(entity.getDataType())) {
				asyncJobsService.createJob(groupCode,
						JobConstants.REVERSAL_180_DAYS_FILE_DOWNLOAD,
						jobParams.toString(), userName, 1L, null, null);
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully created JOB with job params as : %s",
						jobParams.toString());
				LOGGER.debug(msg);
			}

			String reportType = getReportType(reqDto.getType(),
					reqDto.getStatus());

			jobParams.addProperty("reportType", reportType);
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"Unexpected occured in Reversal180DaysFileDownload Controller"
							+ e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error Occured in Reversal180DaysFileDownload Controller";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}
	
	private static String getReportType(String type, String status) {
		String reportType = null;
		try {
			String status1 = null;
			if (!Strings.isNullOrEmpty(status)) {

				if (status.equalsIgnoreCase("active")) {
					status1 = "Active";
				} else if (status.equalsIgnoreCase("inactive")) {
					status1 = "Inactive";
				}
			}
			switch (type) {

			case ReportTypeConstants.ERROR_180DAYS:
				reportType = "Error Records";
				if (!Strings.isNullOrEmpty(status1))
					reportType += status1;
				break;

			case ReportTypeConstants.PROCESSED_180DAYS:
				reportType = "Processed Records ";
				if (!Strings.isNullOrEmpty(status1))
					reportType += status1;
				break;

			case ReportTypeConstants.TOTAL_180DAYS:
				reportType = "Total Records";
				break;

			default:
				reportType = type;

			}

			return reportType;
		} catch (Exception e) {
			LOGGER.error("Invalid report type");
			return reportType;
		}
	}
}
