package com.ey.advisory.controllers.gstr3b;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.gstr3b.Gstr3bEntityLevelReportService;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.ReportTypeConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun KA
 *
 */
@RestController
@Slf4j
public class Gstr3bEntityLevelReportController {

	@Autowired
	@Qualifier("Gstr3bEntityLevelReportServiceImpl")
	Gstr3bEntityLevelReportService gstr3bEntityLevelReportService;

	@Autowired
	FileStatusDownloadReportRepository downloadRepository;

	@Autowired
	AsyncJobsService asyncJobsService;

	@PostMapping(value = "/ui/getGstr3bEntityLevelReportDownload", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr3BEntityReportDownload(@RequestBody String jsonString,
			HttpServletResponse response) {

		JsonObject requestObject = JsonParser.parseString(jsonString).getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		List<String> gstinList = null;
		JsonObject jobParams = new JsonObject();
		JsonObject resp = new JsonObject();
		try {
			Gstr1ReviwSummReportsReqDto criteria = gson.fromJson(json, Gstr1ReviwSummReportsReqDto.class);
			Map<String, List<String>> dataSecAttrs = criteria.getDataSecAttrs();
			if (!dataSecAttrs.isEmpty()) {
				for (String key : dataSecAttrs.keySet()) {
					if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
						if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
								&& dataSecAttrs.get(OnboardingConstant.GSTIN).size() > 0) {
							gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
						}
					}
				}
			}

			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName() != null ? user.getUserPrincipalName() : "SYSTEM";
			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
			entity.setEntityId(criteria.getEntityId().get(0));
			entity.setTaxPeriod(criteria.getTaxperiod());
			entity.setTaxPeriodFrom(criteria.getTaxPeriodFrom());
			entity.setTaxPeriodTo(criteria.getTaxPeriodTo());
			entity.setDerivedRetPeriod(Long.valueOf(GenUtil.getDerivedTaxPeriod(criteria.getTaxperiod())));
			entity.setCreatedBy(userName);
			entity.setGstins(GenUtil.convertStringToClob(String.join(",", gstinList)));
			entity.setReportCateg(APIConstants.GSTR3B.toUpperCase());
			entity.setReportType(ReportTypeConstants.GSTR3B_ENTITY_SUMMARY_REPORT);
			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setReqPayload(gson.toJsonTree(criteria).toString());
			entity.setDataType(APIConstants.GSTR3B.toUpperCase());
			entity = downloadRepository.save(entity);
			Long id = entity.getId();
			jobParams.addProperty("id", id);
			jobParams.addProperty("reportType", entity.getReportType());
			
			asyncJobsService.createJob(TenantContext.getTenantId(), JobConstants.GSTR3B_ENTITY_LEVEL_REPORT,
					jobParams.toString(), userName, 1L, null, null);
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Detail Status. ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getGstr3bOnboardingData", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr3bOnboardingData(@RequestBody String jsonString) throws IOException {

		JsonObject requestObject = JsonParser.parseString(jsonString).getAsJsonObject();

		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		String entityId = json.get("entityId").getAsString();
		String msg1 = String.format(
				"Inside Gstr3bEntityLevelReportController.getGstr3bOnboardingData() " + "method : %s {} ", jsonString);
		LOGGER.debug(msg1);
		try {

			String recResponse = gstr3bEntityLevelReportService.getGstr3bOnboardingDataByEntityId(entityId);

			JsonElement respBody = gson.toJsonTree(recResponse);
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			jsonObj.add("resp", respBody);

			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", "Unexpected occured in Controller" + e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error Occured in Controller";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}

}
