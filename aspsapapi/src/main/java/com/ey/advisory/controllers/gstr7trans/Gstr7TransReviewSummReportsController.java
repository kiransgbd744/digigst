package com.ey.advisory.controllers.gstr7trans;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.FileStatusRepository;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.docs.dto.FileStatusReportDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.reports.GSTR7ReviewScreenHandler;
import com.ey.advisory.app.services.reports.Gstr7AspErrorReportHandler;
import com.ey.advisory.app.services.reports.Gstr7ConGStnErrorReportHandler;
import com.ey.advisory.app.services.reports.Gstr7ProcessedReportHandler;
import com.ey.advisory.app.services.reports.Gstr7ProcessedScreenHandler;
import com.ey.advisory.app.services.reports.Gstr7RefidReportHandler;
import com.ey.advisory.app.services.search.gstr7trans.Gstr7TransEntityLevelReportDownloadServiceImpl;
import com.ey.advisory.app.services.search.gstr7trans.Gstr7TransReportDownloadService;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.ReportTypeConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.dto.Gstr7TransReviwSummReportsReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import joptsimple.internal.Strings;

@RestController
public class Gstr7TransReviewSummReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr7TransReviewSummReportsController.class);

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;
	
	@Autowired
	@Qualifier("Gstr7TransEntityLevelReportDownloadServiceImpl")
	private Gstr7TransEntityLevelReportDownloadServiceImpl gstr7TransReportDownloadService;
	
	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@RequestMapping(value = "/ui/downloadGstr7TransRSReports", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> downloadreviewSummReport(
			@RequestBody String jsonParams) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Inside Async Report Gstr7TransReviewSummReportsController");
		}

		JsonObject requestObject = JsonParser.parseString(jsonParams)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		List<String> gstinList = null;
		JsonObject jobParams = new JsonObject();
		JsonObject resp = new JsonObject();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Request for Gstr7TransReviewSummReportsController : %s",
					json.toString());
			LOGGER.debug(msg);
		}

		try {
			Gstr7TransReviwSummReportsReqDto criteria = gson.fromJson(json,
					Gstr7TransReviwSummReportsReqDto.class);

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

			String groupCode = TenantContext.getTenantId();
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName() != null
					? user.getUserPrincipalName() : "SYSTEM";

			if (Strings.isNullOrEmpty(criteria.getType())) {
				LOGGER.error("Invalid report type");
				throw new Exception("Invalid report type");
			}
			String reportType = getReportType(criteria.getType());
			Long entityId = criteria.getEntityId().get(0);
			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Setting request data to entity to SaveorPersist";
				LOGGER.debug(msg);
			}

			entity.setEntityId(entityId);
			entity.setTaxPeriod(criteria.getTaxperiod());
			entity.setDerivedRetPeriod(Long.valueOf(
					GenUtil.getDerivedTaxPeriod(criteria.getTaxperiod())));
			entity.setCreatedBy(userName);
//			entity.setGstins(
//					GenUtil.convertStringToClob(String.join(",", gstinList)));
			entity.setGstins(GenUtil.convertStringToClob(convertToQueryFormat(gstinList)));
			entity.setDataType(APIConstants.GSTR7.toUpperCase());
			entity.setReportCateg(APIConstants.GSTR7.toUpperCase());
			entity.setReportType(reportType);
			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setReqPayload(gson.toJsonTree(criteria).toString());
			entity.setStatus("active");
			entity.setCreatedBy(userName);
			entity.setCreatedDate(LocalDateTime.now());

			entity = fileStatusDownloadReportRepo.save(entity);
			Long id = entity.getId();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully saved to DB with Report Id : %d", id);
				LOGGER.debug(msg);
			}

			jobParams.addProperty("id", id);
			jobParams.addProperty("reportType", criteria.getType());
			jobParams.addProperty("dataType", entity.getDataType());

			if (DownloadReportsConstant.GSTR7ASUPLOADED
					.equalsIgnoreCase(criteria.getType())) {
				asyncJobsService.createJob(TenantContext.getTenantId(),
						JobConstants.GSTR7_TRANS_ASYNC_PROCESS_REPORT,
						jobParams.toString(), userName, 1L, null, null);
			} else if (DownloadReportsConstant.GSTR7ASPERROR
					.equalsIgnoreCase(criteria.getType())) {
				asyncJobsService.createJob(groupCode,
						JobConstants.GSTR7_TRANS_ASYNC_ASP_ERROR,
						jobParams.toString(), userName, 1L, null, null);
			} else if (DownloadReportsConstant.GSTR7GSTNERROR
					.equalsIgnoreCase(criteria.getType())) {
				asyncJobsService.createJob(groupCode,
						JobConstants.GSTR7_TRANS_ASYNC_GSTN_ERROR,
						jobParams.toString(), userName, 1L, null, null);
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully created JOB with job params as : %s",
						jobParams.toString());
				LOGGER.debug(msg);
			}

			jobParams.addProperty("reportType", reportType);
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);

		} catch (Exception e) {

			APIRespDto dto = new APIRespDto("Failed",
					"Unexpected occured in Async Report Gstr7TransReviewSummReportsController"
							+ e.getMessage());
			resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error Occured in Async Report Gstr7TransReviewSummReportsController";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/gstr7TransEntityLevelReportDownload")
	public ResponseEntity<String> ReportProcess(
			@RequestBody String jsonParams) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Request for Gstr7TransReviewSummReportsController: %s",
					jsonParams);
			LOGGER.debug(msg);
		}

		JsonObject requestObject = JsonParser.parseString(jsonParams)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		List<String> gstinList = null;
		JsonObject jobParams = new JsonObject();
		JsonObject resp = new JsonObject();

		try {
			
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName() != null
					? user.getUserPrincipalName() : "SYSTEM";

			String reportType = "Entity Level Summary";

			JsonArray gstins = json.getAsJsonArray("gstins");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			gstinList = gson.fromJson(gstins, listType);
			String taxPeriod = json.get("taxPeriod").getAsString();
			Long entityId = Long.valueOf(json.get("entityId").getAsString());

			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Setting request data to entity to SaveorPersist";
				LOGGER.debug(msg);
			}
			entity.setEntityId(entityId);
			entity.setTaxPeriod(taxPeriod);
			entity.setDerivedRetPeriod(
					Long.valueOf(GenUtil.getDerivedTaxPeriod(taxPeriod)));
			entity.setCreatedBy(userName);
//			entity.setGstins(
//					GenUtil.convertStringToClob(String.join(",", gstinList)));
			
			entity.setGstins(GenUtil.convertStringToClob(convertToQueryFormat(gstinList)));
			
			entity.setDataType(APIConstants.GSTR7.toUpperCase());
			entity.setReportCateg(APIConstants.GSTR7.toUpperCase());
			entity.setReportType(reportType);
			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setReqPayload(json.toString());

			entity.setStatus("active");
			entity.setCreatedBy(userName);
			entity.setCreatedDate(LocalDateTime.now());
			entity = fileStatusDownloadReportRepo.save(entity);
			Long id = entity.getId();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully saved to DB with Report Id : %d", id);
				LOGGER.debug(msg);
			}

			jobParams.addProperty("id", id);
			jobParams.addProperty("dataType", entity.getReportType());
			jobParams.addProperty("reportType", entity.getReportType());

//			asyncJobsService.createJob(TenantContext.getTenantId(),
//					JobConstants.GSTR7_TRANS_ASYNC_ENTITY_LEVEL,
//					jobParams.toString(), userName, 1L, null, null);
			
			gstr7TransReportDownloadService.generateReports(id, entity.getReportType());

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully created JOB with job params as : %s",
						jobParams.toString());
				LOGGER.debug(msg);
			}

			jobParams.addProperty("reportType", reportType);
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);

		} catch (Exception e) {

			APIRespDto dto = new APIRespDto("Failed",
					"Unexpected occured in Async Report Gstr7TransReviewSummReportsController"
							+ e.getMessage());
			resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error Occured in Async Report Gstr7TransReviewSummReportsController";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}
	
	private String convertToQueryFormat(List<String> list) {

		String queryData = null;

		if (list == null || list.isEmpty())
			return null;

		queryData = "'" + list.get(0) + "'";
		for (int i = 1; i < list.size(); i++) {
			queryData += "," + "'" + list.get(i) + "'";
		}

		return queryData;

	}

	private String getReportType(String type) {

		String reportType = null;
		try {
			switch (type) {

			case DownloadReportsConstant.GSTR7ASUPLOADED:
				reportType = "DigiGST Processed Data";
				break;

			case DownloadReportsConstant.GSTR7GSTNERROR:
				reportType = "Consolidated GSTN Error";
				break;

			case DownloadReportsConstant.GSTR7ASPERROR:
				reportType = "Consolidated DigiGst Error";
				break;

			case DownloadReportsConstant.GSTR7REFIDERROR:
				reportType = "GSTR7REFIDERROR";
				break;

			case DownloadReportsConstant.GSTR7PROCESSSCREEN:
				reportType = "GSTR7_Processed_Screen_download";
				break;

			case DownloadReportsConstant.GSTR7REVIEWSCREEN:
				reportType = "GSTR7_ReviewSummary_Screen_download";
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