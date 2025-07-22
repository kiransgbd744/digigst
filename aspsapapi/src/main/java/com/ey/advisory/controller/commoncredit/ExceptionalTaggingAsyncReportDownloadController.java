package com.ey.advisory.controller.commoncredit;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.ReportTypeConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;


@RestController
@Slf4j
public class ExceptionalTaggingAsyncReportDownloadController {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	FileStatusRepository fileStatusRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@PostMapping(value = "/ui/exceptionalTaggingDownload")
	public ResponseEntity<String> get2BPRRespReport(@RequestBody String jsonParams) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside ExceptionalTaggingAsyncReportDownloadController";
			LOGGER.debug(msg);
		}
		JsonElement jsonElement = JsonParser.parseString(jsonParams);
		JsonObject requestObject = jsonElement.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Request for Download Ewb Report Controller: %s",
					json.toString());
			LOGGER.debug(msg);
		}

		try

		{
			FileStatusReportDto reqDto = gson.fromJson(json,
					FileStatusReportDto.class);

			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Setting request data to entity to SaveorPersist";
				LOGGER.debug(msg);
			}

			entity.setFileId(reqDto.getFileId());
			Optional<Gstr1FileStatusEntity> fileStatusEntity = fileStatusRepo
					.findById(reqDto.getFileId());
			if (fileStatusEntity.isPresent()) {
				entity.setUpldFileName(fileStatusEntity.get().getFileName());
			}
			
			entity.setCreatedBy(userName);
			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setReportCateg("Common Credit Reversal");
			entity.setDataType("Exceptional Tagging");

			String reportType = null;
			if (ReportTypeConstants.ERROR.equalsIgnoreCase(reqDto.getType())) {
				reportType = "error";
				entity.setReportType("error");
			} else if (ReportTypeConstants.TOTAL_RECORDS
					.equalsIgnoreCase(reqDto.getType())) {
				reportType = "totalrecords";
				entity.setReportType("totalrecords");
			} else if (ReportTypeConstants.PROCESSED_RECORDS
					.equalsIgnoreCase(reqDto.getType())) {
				reportType = "processed";
				entity.setReportType("processed");
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
			jobParams.addProperty("reportType", reportType);

			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.COMMON_CREDIT_REVERSAL_DOWNLOAD, 
					jobParams.toString(), userName, 1L, null, null);

			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"Error occured while creating the request");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error Occured in "
					+ "ExceptionalTaggingAsyncReportDownloadController";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}
}
