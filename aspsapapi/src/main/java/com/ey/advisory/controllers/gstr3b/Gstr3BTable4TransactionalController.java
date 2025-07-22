package com.ey.advisory.controllers.gstr3b;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
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

/**
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@RestController
public class Gstr3BTable4TransactionalController {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@PostMapping(value = "/ui/downloadTable4TransactionalReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> download180DaysReversalRespReport(
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject request = requestObject.getAsJsonObject("req");
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Inside downloadTable4TransactionalReport"
								+ " method in Gstr3BTable4TransactionalController "
								+ "with request: %s", request);
				LOGGER.debug(msg);
			}
			String taxPeriod = request.has("taxPeriod")
					? request.get("taxPeriod").getAsString() : "";
			if (Strings.isNullOrEmpty(taxPeriod))
				throw new AppException("tax period cannot be empty");

			String gstin = request.has("gstin")
					? request.get("gstin").getAsString() : "";
			if (Strings.isNullOrEmpty(gstin))
				throw new AppException("gstin cannot be empty");

			Long id = createAsyncReport(gstin, taxPeriod, request.toString());
			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);
			String reportType = "GSTR3B Table4 Transactional Report";
			jobParams.addProperty("reportType", reportType);
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			APIRespDto dto = new APIRespDto("Failed", ex.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while generating async report";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, ex);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}
	}

	private Long createAsyncReport(String gstin, String taxPeriod,
			String requestObject) {
		try {
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();

			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();

			entity.setReqPayload(requestObject.toString());
			entity.setCreatedBy(userName);
			entity.setCreatedDate(
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setReportCateg("GSTR3B");
			entity.setDataType("GSTR3B");
			entity.setReportType(ReportTypeConstants.GSTR3B_Table4_Transactional);
			entity.setGstins(GenUtil.convertStringToClob(gstin));
			entity.setTaxPeriod(taxPeriod);
			entity = fileStatusDownloadReportRepo.save(entity); 

			Long id = entity.getId();
			String groupCode = TenantContext.getTenantId();
			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);

			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR3B_TABLE4_TRANSACTIONAL,
					jobParams.toString(), userName, 1L, null, null);

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Successfully created a job for Gstr3b Table4"
								+ " Transactional report with id: %s", id);
				LOGGER.debug(msg);
			}
			return id;
		} catch (Exception ee) {
			String msg = "Error while creating Gstr3b Table4 Transactional report job";
			LOGGER.error(msg, ee);
			throw new AppException(ee.getMessage());
		}
	}
}
