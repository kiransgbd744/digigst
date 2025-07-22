/**	
 * 
 */
package com.ey.advisory.controller.gstr8;

import java.lang.reflect.Type;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.services.savetogstn.gstr8.Gstr8SaveToGstnService;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1SummarySaveStatusRespDto;
import com.ey.advisory.app.services.daos.get2a.GetGstr1SummarySaveStatusService;
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
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@Slf4j
@RestController
public class Gstr8ReturnsController {

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("GetGstr1SummarySaveStatusService")
	private GetGstr1SummarySaveStatusService gstr1SummarySaveStatusService;

	@Autowired
	private Gstr8SaveToGstnService gstr8SaveToGstnService;

	@Autowired
	AsyncJobsService asyncJobsService;
	
	@Autowired
	FileStatusDownloadReportRepository downloadRepository;

	@PostMapping(value = "/ui/gstr8SavetoGstn", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr8SavetoGstn(@RequestBody String request) {
		return gstr8SaveToGstnService.saveGstr8DataToGstn(request);
	}

	@PostMapping(value = "/ui/gstr8InvLvlSaveStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr1SummarySaveStatus(
			@RequestBody String jsonString) {

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
		Type listType = new TypeToken<List<Gstr1GetInvoicesReqDto>>() {
		}.getType();
		List<Gstr1GetInvoicesReqDto> dtos = gson.fromJson(asJsonArray,
				listType);
		try {
			List<Gstr1SummarySaveStatusRespDto> summaryList = gstr1SummarySaveStatusService
					.findByGstr8Criteria(dtos);
			JsonObject resps = new JsonObject();
			if (!summaryList.isEmpty()) {
				JsonElement respBody = gson.toJsonTree(summaryList);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			} else {
				return createGstinNodataSuccessResp();
			}
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Detail Status. ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping(value = "/ui/gstr8ProcessedReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr8ProcessedReport(
			@RequestBody String jsonString) {

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		List<String> gstinList = null;
		JsonObject jobParams = new JsonObject();
		JsonObject resp = new JsonObject();
		try {
			Gstr1ReviwSummReportsReqDto criteria = gson.fromJson(json,
					Gstr1ReviwSummReportsReqDto.class);
			Map<String, List<String>> dataSecAttrs = criteria.getDataSecAttrs();
			if (!dataSecAttrs.isEmpty()) {
				for (String key : dataSecAttrs.keySet()) {
					if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
						if (!dataSecAttrs.get(OnboardingConstant.GSTIN)
								.isEmpty()
								&& dataSecAttrs.get(OnboardingConstant.GSTIN)
										.size() > 0) {
							gstinList = dataSecAttrs
									.get(OnboardingConstant.GSTIN);
						}
					}
				}
			}

			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName() != null
					? user.getUserPrincipalName() : "SYSTEM";
			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
			entity.setTaxPeriod(criteria.getTaxperiod());
			entity.setDerivedRetPeriod(Long.valueOf(
					GenUtil.getDerivedTaxPeriod(criteria.getTaxperiod())));
			entity.setCreatedBy(userName);
			entity.setGstins(
					GenUtil.convertStringToClob(String.join(",", gstinList)));
			entity.setReportCateg(APIConstants.GSTR8.toUpperCase());
			entity.setReportType(ReportTypeConstants.GSTR8_PROCESSED_REPORT);
			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setReqPayload(gson.toJsonTree(criteria).toString());
			entity.setDataType(APIConstants.GSTR8.toUpperCase());
			entity = downloadRepository.save(entity);
			Long id = entity.getId();
			jobParams.addProperty("id", id);
			jobParams.addProperty("reportType", entity.getReportType());
			jobParams.addProperty("dataType", criteria.getReportType());
			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.GSTR8_PROCESSED_REPORT, jobParams.toString(),
					userName, 1L, null, null);
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

	@RequestMapping(value = "/ui/getGstr8Summary", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getProcessedRecords(
			@RequestBody String jsonString) {
		return gstr8SaveToGstnService.getGstr8Summary(jsonString);
	}

	public ResponseEntity<String> createGstinNodataSuccessResp() {
		JsonObject resp = new JsonObject();
		resp.add("hdr", new Gson().toJsonTree(
				new APIRespDto("S", " No Data for the selected gstins. ")));
		return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/ui/gstr8EntityLvlReport")
	public ResponseEntity<String> ReportProcess(
			@RequestBody String jsonString) {

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		List<String> gstinList = null;
		JsonObject jobParams = new JsonObject();
		JsonObject resp = new JsonObject();
		try {
			Gstr1ReviwSummReportsReqDto criteria = gson.fromJson(json,
					Gstr1ReviwSummReportsReqDto.class);
			Map<String, List<String>> dataSecAttrs = criteria.getDataSecAttrs();
			if (!dataSecAttrs.isEmpty()) {
				for (String key : dataSecAttrs.keySet()) {
					if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
						if (!dataSecAttrs.get(OnboardingConstant.GSTIN)
								.isEmpty()
								&& dataSecAttrs.get(OnboardingConstant.GSTIN)
										.size() > 0) {
							gstinList = dataSecAttrs
									.get(OnboardingConstant.GSTIN);
						}
					}
				}
			}

			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName() != null
					? user.getUserPrincipalName() : "SYSTEM";
			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
			entity.setTaxPeriod(criteria.getTaxperiod());
			entity.setDerivedRetPeriod(Long.valueOf(
					GenUtil.getDerivedTaxPeriod(criteria.getTaxperiod())));
			entity.setCreatedBy(userName);
			entity.setGstins(
					GenUtil.convertStringToClob(String.join(",", gstinList)));
			entity.setReportCateg(APIConstants.GSTR8.toUpperCase());
			entity.setReportType(ReportTypeConstants.GSTR8_ENTITY_REPORT);
			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setReqPayload(gson.toJsonTree(criteria).toString());
			entity.setDataType(APIConstants.GSTR8.toUpperCase());
			entity = downloadRepository.save(entity);
			Long id = entity.getId();
			jobParams.addProperty("id", id);
			jobParams.addProperty("reportType", entity.getReportType());
			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.GSTR8_ENTITY_REPORT, jobParams.toString(),
					userName, 1L, null, null);
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
}
