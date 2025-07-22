package com.ey.advisory.controllers.gstr3b;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.docs.dto.Gstr3BReportDownloadReqDto;
import com.ey.advisory.common.GenUtil;
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
 * @author Rajesh N K
 *
 */

@RestController
@Slf4j
public class GSTR3BReportDownloadController {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@PostMapping(value = "/ui/gstr3bInwardOutwardReport")
	public ResponseEntity<String> ReportGeneration(
			@RequestBody String inputParams) {

		JsonObject requestObject = (new JsonParser()).parse(inputParams)
				.getAsJsonObject();

		Gson gson = GsonUtil.newSAPGsonInstance();

		String reqJson = requestObject.get("req").getAsJsonObject().toString();

		try {

			Gstr3BReportDownloadReqDto reqDto = gson.fromJson(
					reqJson.toString(), Gstr3BReportDownloadReqDto.class);

			String groupCode = TenantContext.getTenantId();
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();

			String gstin = reqDto.getGstins();
			String taxPeriod = reqDto.getTaxPeriod();
			String reportType = reqDto.getReportType();

			String gstins = "'" + gstin + "'";

			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
			entity.setGstins(GenUtil.convertStringToClob(gstins));
			entity.setTaxPeriod(taxPeriod);
			entity.setReportCateg("GSTR3B");
			entity.setReportType(reportType);
			entity.setDataType(reportType);
			entity.setCreatedBy(userName);
			entity.setCreatedDate(LocalDateTime.now());

			Long derivedTaxPeriod = Long.valueOf(
					taxPeriod.substring(2).concat(taxPeriod.substring(0, 2)));

			entity.setDerivedRetPeriod(derivedTaxPeriod);

			entity = fileStatusDownloadReportRepo.save(entity);

			Long id = entity.getId();

			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);

			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR3B_INWARD_OUTWARD_REPORT,
					jobParams.toString(), userName, 1L, null, null);
			if ("INWARD".equalsIgnoreCase(reportType)) {
				reportType = "GSTR3B Inward Report";
			} else if ("OUTWARD".equalsIgnoreCase(reportType)) {
				reportType = "GSTR3B Outward Report";
			}
			jobParams.addProperty("reportType", reportType);

			JsonElement respBody = gson.toJsonTree(jobParams);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"Unexpected occured in GSTR3B Report CSV Download Controller"
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
