package com.ey.advisory.controllers.gstr7trans;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
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
public class Gstr7TransDownloadReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr7TransDownloadReportsController.class);

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	FileStatusRepository fileStatusRepo;

	@RequestMapping(value = "/ui/downloadGstr7TransFileReports", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> ReportProcess(
			@RequestBody String jsonParams) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Inside Async Report Gstr7TransDownloadReportsController");
		}

		JsonObject requestObject = JsonParser.parseString(jsonParams)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		String groupCode = TenantContext.getTenantId();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Request for Gstr7TransDownloadReportsController: %s",
					json.toString());
			LOGGER.debug(msg);
		}

		try {
			FileStatusReportDto reqDto = gson.fromJson(json,
					FileStatusReportDto.class);

			Long fileId = reqDto.getFileId();
			if (fileId == null && fileId == 0L) {
				LOGGER.error(
						"File Id not found in the request and it is invalid");
				throw new Exception(
						"File Id not found in the request and it is invalid");
			}

			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Setting request data to entity to SaveorPersist";
				LOGGER.debug(msg);
			}
			entity.setFileId(reqDto.getFileId());
			entity.setReportType(getReportType(reqDto.getType()));
			entity.setReportCateg("GSTR7");
			entity.setDataType("GSTR7");
			entity.setStatus("active");
			entity.setCreatedBy(userName);
			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			

			/*
			
			entity.setCreatedDate(
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			entity.setDataType(reqDto.getDataType());
			entity.setEntityId(setDataSecurity.getEntityId().get(0));
			entity.setDocDateFrom(reqDto.getDocDateFrom());
			entity.setDocDateTo(reqDto.getDocDateTo());
			entity.setTaxPeriod(reqDto.getTaxPeriod());
			Long derivedRetPeriod = Long.valueOf(GenUtil
					.convertTaxPeriodToInt(reqDto.getTaxPeriod()).toString());
			entity.setDerivedRetPeriod(derivedRetPeriod);
			*/

			Optional<Gstr1FileStatusEntity> fileStatusEntity = fileStatusRepo
					.findById(reqDto.getFileId());

			if (fileStatusEntity.isPresent()) {
				entity.setUpldFileName(fileStatusEntity.get().getFileName());
			}
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Request for Gstr7TransDownloadReportsController: %s",
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
					JobConstants.GSTR7_TRANS_FILEDOWNLOAD, jobParams.toString(),
					userName, 1L, null, null);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully created JOB with job params as : %s",
						jobParams.toString());
				LOGGER.debug(msg);
			}
			JsonObject jobParams1 = new JsonObject();
			jobParams1.addProperty("id", id);
			jobParams1.addProperty("reportType", getReportType(reqDto.getType()));
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(jobParams1);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"Unexpected occured in GSTR7 Trans Async Report CSV Download Controller"
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
	
	private String getReportType(String type) {

		String reportType = null;
		try {
			switch (type) {

			case DownloadReportsConstant.TOTALERECORDS:
				reportType = "Total Records";
				break;

			case DownloadReportsConstant.ERROR:
				reportType = "Error Records";
				break;

			case DownloadReportsConstant.PROCESSED:
				reportType = "Processed Records";
				break;

			default:
				reportType = "Invalid report type";

			}
		} catch (Exception e) {
			LOGGER.error("Invalid report type");
		}

		return reportType;
	}

}