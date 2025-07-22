/**
 * 
 */
package com.ey.advisory.controller.itc04stocktrack;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.services.itc04stocktrack.Itc04StockTrackService;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.DateUtil;
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
import com.ey.advisory.core.dto.ITC04RequestDto;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 * 
 */
@Slf4j
@RestController
public class Itc04StockTrackingController {

	private static final ImmutableMap<String, String> immutableRetMap = ImmutableMap
			.<String, String>builder().put("Q1", "13").put("Q2", "14")
			.put("Q3", "15").put("Q4", "16").put("H1", "17").put("H2", "18")
			.build();

	@Autowired
	FileStatusDownloadReportRepository downloadRepository;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	Itc04StockTrackService itc04stockTracking;

	@Autowired
	@Qualifier("Itc04StockTrackDownloadReportServiceImpl")
	private AsyncReportDownloadService itc04StockTrackingReport;

	@RequestMapping(value = "/ui/itc04sttrdownloadrpt", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> itc04sttrdownloadrpt(
			@RequestBody String jsonString) {

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		JsonObject jobParams = new JsonObject();
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			ITC04RequestDto criteria = gson.fromJson(json,
					ITC04RequestDto.class);

			LOGGER.debug("DataStatus Adapter Filters Setting to Request END");

			Map<String, List<String>> dataSecAttrs = criteria.getDataSecAttrs();

			List<String> gstinList = null;

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
			entity.setCreatedBy(userName);
			entity.setGstins(
					GenUtil.convertStringToClob(String.join(",", gstinList)));
			entity.setReportCateg(APIConstants.ITC_04);
			entity.setReportType(ReportTypeConstants.STOCK_TRACKING_REPORT);
			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setReqPayload(gson.toJsonTree(criteria).toString());
			entity.setDataType(APIConstants.ITC_04);
			if (criteria.getRequestType().equalsIgnoreCase("ChallanDate")
					&& !Strings.isNullOrEmpty(criteria.getFromChallanDate())
					&& !Strings.isNullOrEmpty(criteria.getToChallanDate())) {
				entity.setDocDateFrom(DateUtil.tryConvertUsingFormat(
						criteria.getFromChallanDate(),
						DateTimeFormatter.ofPattern("yyyy-MM-dd")));
				entity.setDocDateTo(DateUtil.tryConvertUsingFormat(
						criteria.getToChallanDate(),
						DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			} else if (criteria.getRequestType()
					.equalsIgnoreCase("ReturnPeriod")
					&& !Strings.isNullOrEmpty(criteria.getFromReturnPeriod())
					&& !Strings.isNullOrEmpty(criteria.getToReturnPeriod())) {

				entity.setDerivedRetPeriodFrom(Long.valueOf(
						criteria.getFy().substring(0, 4) + immutableRetMap
								.get(criteria.getFromReturnPeriod())));
				entity.setDerivedRetPeriodFromTo(Long.valueOf(
						criteria.getFy().substring(0, 4) + immutableRetMap
								.get(criteria.getFromReturnPeriod())));
			} else {
				String errMsg = "Invalid Filters.";
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}

			entity.setFyYear(criteria.getFy());
			entity = downloadRepository.save(entity);
			Long id = entity.getId();
			jobParams.addProperty("id", id);
			jobParams.addProperty("reportType", "Stock Tracking Report");
			jobParams.addProperty("dataType", APIConstants.ITC_04);

			// itc04StockTrackingReport.generateReports(id);
			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.ITC04_STOCK_TRACKING_REPORT,
					jobParams.toString(), userName, 1L, null, null);
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String errMsg = "Unexpected error occured in ITC04 Credit Download Async Report.";
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.addProperty("resp", e.getMessage());
			LOGGER.error(errMsg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}

	@RequestMapping(value = "/ui/itc04StockTracking", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> itc04StockTracking(
			@RequestBody String jsonString) {
		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		ITC04RequestDto reqDto = gson.fromJson(json, ITC04RequestDto.class);
		return itc04stockTracking.getScreenDetails(reqDto);
	}

	@RequestMapping(value = "/ui/itc04initiateReport", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> itc04initiateReport(
			@RequestBody String jsonString) {
		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		ITC04RequestDto reqDto = gson.fromJson(json, ITC04RequestDto.class);
		return itc04stockTracking.triggerInitiateReport(reqDto);
	}

}
