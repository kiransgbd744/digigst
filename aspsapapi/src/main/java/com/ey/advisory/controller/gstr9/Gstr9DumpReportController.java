package com.ey.advisory.controller.gstr9;

import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.services.gstr9.Gstr9OutwardUtil;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.ReportTypeConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Slf4j
@RestController
@RequestMapping(value = "/ui/")
public class Gstr9DumpReportController {

	@Autowired
	Gstr9OutwardUtil gstr9OutwardUtil;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@PostMapping(value = "getDumpReportsDetails", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getDumpReportsDetails(
			@RequestBody String jsonString, HttpServletRequest req) {
		return getDumpReportsDetails(jsonString);
	}

	private ResponseEntity<String> getDumpReportsDetails(String jsonString) {

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		Gson gson = GsonUtil.gsonInstanceWithExpose();
		JsonObject resp = new JsonObject();
		try {

			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			LOGGER.debug("RequestJson {}" + obj);

			Long id = persistDataInDownReport(obj, userName);

			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);

			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.GSTR9_DUMP_REPORTS, jobParams.toString(),
					userName, 1L, null, null);

			jobParams.addProperty("reportType", "GSTR9 DIGIGST Compute");
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);

		} catch (Exception e) {

			String errMsg = "Unexpected occured in GSTR9 Dump Reports"
					+ e.getMessage();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.addProperty("resp", errMsg);
			LOGGER.error(errMsg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	private Long persistDataInDownReport(JsonObject obj, String userName) {

		JsonObject requestObject = obj.get("req").getAsJsonObject();
		String gstin = requestObject.get("gstin").getAsString();
		String fyOld = requestObject.get("fy").getAsString();

		String formattedFy = GenUtil.getFormattedFy(fyOld);

		String taxPeriod = GenUtil.getFinancialPeriodFromFY(formattedFy);
		FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
		entity.setReqPayload(requestObject.toString());
		entity.setCreatedBy(userName);
		entity.setCreatedDate(LocalDateTime.now());
		entity.setReportStatus(ReportStatusConstants.INITIATED);
		entity.setReportCateg("GSTR9 Dump Reports");
		entity.setDataType("GSTR9");
		entity.setReportType(ReportTypeConstants.GSTR9_DIGIGST_COMPUTE);
		entity.setGstins(GenUtil.convertStringToClob(gstin));
		entity.setTaxPeriod(taxPeriod);
		entity = fileStatusDownloadReportRepo.save(entity);
		return entity.getId();
	}
}
