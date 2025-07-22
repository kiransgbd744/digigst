package com.ey.advisory.controllers.anexure1;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.docs.dto.FileStatusReportDto;
import com.ey.advisory.app.services.search.filestatussearch.AsyncReportHandler;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@RestController
@Slf4j
public class AsncOutwardFileStatusRetDownloadReportsController {

	@Autowired
	@Qualifier("AsyncFileStatusEinvReportHandlerImpl")
	private AsyncReportHandler asyncFileStatusReportHandler;

	@Autowired
	private FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	private static final List<String> OUTWARD_FILE_STATUS_RET_PROCESS = ImmutableList
			.of("RET N", "RET A", "RET P", "RET I", "RET E");

	private static final List<String> OUTWARD_FILE_STATUS_EWB_PROCESS = ImmutableList
			.of("EWB N", "EWB A", "EWB C", "EWB G", "EWB E", "EWB EN");

	@PostMapping(value = "/ui/downloadOutwardFileStatusRetCsvReports")
	public ResponseEntity<String> ReportProcess(
			@RequestBody String jsonParams) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Outward FileStatus Ret DownloadReports Controller";
			LOGGER.debug(msg);
		}

		JsonObject requestObject = (new JsonParser()).parse(jsonParams)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		String groupCode = TenantContext.getTenantId();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Request for Outward FileStatus Ret DownloadReports Controller: %s",
					json.toString());
			LOGGER.debug(msg);
		}
		try {
			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();

			FileStatusReportDto reqDto = gson.fromJson(json,
					FileStatusReportDto.class);

			if (LOGGER.isDebugEnabled()) {
				String msg = "Setting request data to entity to SaveorPersist";
				LOGGER.debug(msg);
			}

			asyncFileStatusReportHandler.setDataToEntity(entity, reqDto);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Request for Outward FileStatus Ret DownloadReports Controller: %s",
						json.toString());
				LOGGER.debug(msg);
			}

			entity = fileStatusDownloadReportRepo.save(entity);

			Long id = entity.getId();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully saved to DB with Report Id : %d", id);
				LOGGER.debug(msg);
			}

			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);

			if ("OUTWARD".equalsIgnoreCase(entity.getDataType())
					|| "OUTWARD_1A".equalsIgnoreCase(entity.getDataType())) {
				if (OUTWARD_FILE_STATUS_RET_PROCESS
						.contains(reqDto.getType().toUpperCase())) {
					asyncJobsService.createJob(groupCode,
							JobConstants.OUTWARD_FILE_STATUS_RET,
							jobParams.toString(), userName, 1L, null, null);
				}
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
					"Unexpected occurred in Async Report CSV Download Controller"
							+ e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error occurred in Async Report Controller";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}

	private String getReportType(String type, String status) {

		String reportType = null;
		try {
			if (OUTWARD_FILE_STATUS_RET_PROCESS.contains(type.toUpperCase())) {
				reportType = "Outward FileStatus Ret Processed Records";
			} else {
				reportType = "Invalid report type";
			}
		} catch (Exception e) {
			LOGGER.error("Invalid report type");
		}
		return reportType;
	}

	private String getEwbReportType(String type, String status) {

		String reportType = null;
		try {
			if (OUTWARD_FILE_STATUS_EWB_PROCESS.contains(type.toUpperCase())) {
				reportType = "Outward FileStatus Ewb Processed Records";
			} else {
				reportType = "Invalid report type";
			}
		} catch (Exception e) {
			LOGGER.error("Invalid report type");
		}
		return reportType;
	}

	@GetMapping(value = { "/ui/OutwardFileStatusRetDownloadDocument",
			"/ui/OutwardFileStatusEwbDownloadDocument" })

	public void fileDownloads(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		LOGGER.debug("inside Async Report file Downloads");

		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);
		String id = request.getParameter("configId");
		Optional<FileStatusDownloadReportEntity> entity = fileStatusDownloadReportRepo
				.findById(Long.valueOf(id));

		if (entity.isPresent()) {

			String fileName = entity.get().getFilePath();
			String fileFolder = "Anx1FileStatusReport";

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Downloading Document with fileName : %s and Folder Name: %s",
						fileName, fileFolder);
				LOGGER.debug(msg);
			}

			Document document = null;
			if (Strings.isNullOrEmpty(entity.get().getDocId())) {
				document = DocumentUtility.downloadDocument(fileName,
						fileFolder);
			} else {
				String docId = entity.get().getDocId();
				String msg = String.format(
						"Doc Id is available for File Name %s and Doc Id %s",
						fileName, docId);
				LOGGER.debug(msg);
				document = DocumentUtility
						.downloadDocumentByDocId(entity.get().getDocId());
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Document to download : %s",
						document);
				LOGGER.debug(msg);
			}

			if (document == null) {
				return;
			}


			InputStream inputStream = document.getContentStream().getStream();
			int read = 0;
			byte[] bytes = new byte[1024];

			if (document != null) {
				response.setHeader("Content-Disposition",
						String.format("attachment; filename = " + fileName));
				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
			}
		}
	}

	// Outward file status EWB downloads reports

	@PostMapping(value = "/ui/downloadOutwardFileStatusEwbCsvReports")
	public ResponseEntity<String> ReportEWBProcess(
			@RequestBody String jsonParams) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Outward FileStatus Ewb DownloadReports Controller";
			LOGGER.debug(msg);
		}
		JsonObject requestObject = (new JsonParser().parse(jsonParams))
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();

		Gson gson = GsonUtil.newSAPGsonInstance();
		String groupCode = TenantContext.getTenantId();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Request for Outward FileStatus Ewb DownloadReports Controller: %s",
					json.toString());
			LOGGER.debug(msg);
		}
		try {
			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();

			FileStatusReportDto reqDto = gson.fromJson(json,
					FileStatusReportDto.class);

			if (LOGGER.isDebugEnabled()) {
				String msg = "Setting request data to entity to SaveorPersist";
				LOGGER.debug(msg);
			}

			asyncFileStatusReportHandler.setDataToEntity(entity, reqDto);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Request for Outward FileStatus Ewb DownloadReports Controller: %s",
						json.toString());
				LOGGER.debug(msg);
			}

			entity = fileStatusDownloadReportRepo.save(entity);

			Long id = entity.getId();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully saved to DB with Report Id : %d", id);
				LOGGER.debug(msg);
			}

			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);

			if ("OUTWARD".equalsIgnoreCase(entity.getDataType()) || "OUTWARD_1A".equalsIgnoreCase(entity.getDataType())) {
				if (OUTWARD_FILE_STATUS_EWB_PROCESS
						.contains(reqDto.getType().toUpperCase())) {
					asyncJobsService.createJob(groupCode,
							JobConstants.OUTWARD_FILE_STATUS_EWB,
							jobParams.toString(), userName, 1L, null, null);
				}
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully created JOB with job params as : %s",
						jobParams.toString());
				LOGGER.debug(msg);
			}

			String reportType = getEwbReportType(reqDto.getType(),
					reqDto.getStatus());

			jobParams.addProperty("reportType", reportType);
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"Unexpected occurred in Async Report CSV Download Controller"
							+ e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error occurred in Async Report Controller";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}
	}
}
