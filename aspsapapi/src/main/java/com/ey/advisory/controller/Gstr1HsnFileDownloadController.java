package com.ey.advisory.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@RestController
@Slf4j
public class Gstr1HsnFileDownloadController {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	FileStatusRepository fileStatusRepo;

	@Autowired
	@Qualifier("AsyncFileStatusReportHandlerImpl")
	AsyncReportHandler asyncFileStatusReportHandler;

	@PostMapping(value = "/ui/downloadHsnFileReports")
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
			entity.setFileId(reqDto.getFileId());
			entity.setReportType("VerticalUploadHSN");
			entity.setReportCateg(reqDto.getReportCateg());
			
			entity.setDataType(reqDto.getDataType());
			entity.setStatus("active");
			entity.setCreatedBy(userName);
			entity.setCreatedDate(LocalDateTime.now());
			Optional<Gstr1FileStatusEntity> fileStatusEntity = fileStatusRepo
					.findById(reqDto.getFileId());
			if (fileStatusEntity.isPresent()) {
			entity.setUpldFileName(fileStatusEntity.get().getFileName());
			}
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Request for Download CSV Report Controller: %s",
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
			jobParams.addProperty("reportType", reqDto.getType());
			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR1_HSN_DOWNLOAD, jobParams.toString(),
					userName, 1L, null, null);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully created JOB with job params as : %s",
						jobParams.toString());
				LOGGER.debug(msg);
			}
			JsonObject jobParams1 = new JsonObject();
			jobParams1.addProperty("id", id);
			jobParams1.addProperty("reportType", "VerticalUploadHSN");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(jobParams1);
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

}
