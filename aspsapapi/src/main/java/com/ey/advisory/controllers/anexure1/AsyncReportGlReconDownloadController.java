package com.ey.advisory.controllers.anexure1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.GlReconFileStatusRepository;
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
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class AsyncReportGlReconDownloadController {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	GlReconFileStatusRepository fileStatusRepo;

	@Autowired
	@Qualifier("AsyncFileStatusReportHandlerImpl")
	AsyncReportHandler asyncFileStatusReportHandler;

	@PostMapping(value = "/ui/downloadGlFileReports")
	public ResponseEntity<String> ReportProcess(
			@RequestBody String jsonParams) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Download CSV Report Controller";
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
					"Request for Download CSV Report Controller: %s",
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
						"Request for Download CSV Report Controller: %s",
						json.toString());
				LOGGER.debug(msg);
			}
			entity.setFileId(reqDto.getFileId());
			entity.setReportCateg("GL_DUMP");
			entity.setReportType("GL Recon");
			
			if (reqDto.getType().equalsIgnoreCase("totalrecords")) {
				entity.setReportType("TOTAL_RECORDS");
			} else if (reqDto.getType().equalsIgnoreCase("processed")
					&& reqDto.getStatus().equalsIgnoreCase("active")) {
				entity.setReportType("PSD_ACTIVE");
			} else if (reqDto.getType().equalsIgnoreCase("processed")
					&& reqDto.getStatus().equalsIgnoreCase("inActive")) {
				entity.setReportType("PSD_INACTIVE");
			} else if (reqDto.getType().equalsIgnoreCase("error")
					&& reqDto.getStatus().equalsIgnoreCase("active")) {
				entity.setReportType("ERR_ACTIVE");
			} else if (reqDto.getType().equalsIgnoreCase("error")
					&& reqDto.getStatus().equalsIgnoreCase("inActive")) {
				entity.setReportType("ERR_INACTIVE");
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

			asyncJobsService.createJob(groupCode,
					JobConstants.GL_DUMP_DOWNLOAD_REPORT, jobParams.toString(),
					userName, 1L, null, null);

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
					"Unexpected occured in Async Report CSV Download Controller"
							+ e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error Occured in Async Report Controller";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}

	private String getReportType(String type, String status) {
		String reportType = null;
		try {
			String status1 = null;
			if (!Strings.isNullOrEmpty(status)) {

				if (status.equalsIgnoreCase("active")) {
					status1 = " Active";
				} else if (status.equalsIgnoreCase("inactive")) {
					status1 = " Inactive";
				}
			}
			switch (type) {

			case "totalrecords":
				reportType = "Total Records";
				if (!Strings.isNullOrEmpty(status1))
					reportType += status1;
				break;

			case ReportTypeConstants.PROCESSED_RECORDS:
				reportType = "Processed Records";
				if (!Strings.isNullOrEmpty(status1))
					reportType += status1;
				break;

			case ReportTypeConstants.ERROR:
				reportType = "Error Records";
				if (!Strings.isNullOrEmpty(status1))
					reportType += status1;
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
