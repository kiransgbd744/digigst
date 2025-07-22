package com.ey.advisory.controllers.gstr6a;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.FileStatusRepository;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6AProcessedDataRequestDto;
import com.ey.advisory.app.services.reports.Gstr6aDownloadReportsScreenHandler;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.controller.gstr2a.Gstr6ReportsController;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Sasidhar
 *
 * 
 */
@RestController
public class Gstr6aReportsDownloadController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6ReportsController.class);

	@Autowired
	@Qualifier("Gstr6aDownloadReportsScreenHandler")
	private Gstr6aDownloadReportsScreenHandler gstr6aDownloadReportsScreenHandler;

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	FileStatusRepository fileStatusRepo;

	@RequestMapping(value = "/ui/gstr6aReportsDownloadsDashboard", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr6aReportsDownloadsForDashboard(
			@RequestBody String jsonString, HttpServletResponse response) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject dataObj = new JsonObject();
		try {
			String fileName = null;
			Workbook workbook = null;
			Gstr6AProcessedDataRequestDto criteria = gson.fromJson(json,
					Gstr6AProcessedDataRequestDto.class);
			workbook = gstr6aDownloadReportsScreenHandler
					.downloadGStr6aReportDashboard(criteria);
			String date = null;
			String time = null;
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());

			LocalDateTime istDateTimeFromUTC = EYDateUtil
					.toISTDateTimeFromUTC(now);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("ddMMyyyy");

			DateTimeFormatter FOMATTER1 = DateTimeFormatter.ofPattern("HHmmss");

			date = FOMATTER.format(istDateTimeFromUTC);
			time = FOMATTER1.format(istDateTimeFromUTC);
			fileName = "GSTR-6A_DownloadReport" + "_" + date + "" + time;
			if (workbook != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", String
						.format("attachment; filename=" + fileName + ".xlsx"));
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
				return new ResponseEntity<>(null, HttpStatus.OK);
			} else {
				dataObj.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
				dataObj.addProperty("resp", "No Data Available");
				return new ResponseEntity<>(dataObj.toString(), HttpStatus.OK);
			}
		} catch (Exception ex) {
			dataObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			dataObj.addProperty("resp", ex.getMessage());
			String msg = "Error occured while GSTR-6A_DownloadReport ";
			LOGGER.error(msg, ex);
			try {
				response.flushBuffer();
			} catch (IOException e) {
				String errMsg = "Exception occurred while flushing the response buffer";
				LOGGER.error(errMsg, ex);
			}
			return new ResponseEntity<>(dataObj.toString(), HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/ui/gstr6aReportsDownloads", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr6aAsyncReportsDownloads(
			@RequestBody String jsonString, HttpServletResponse response) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		String groupCode = TenantContext.getTenantId();
		Gson gson = GsonUtil.newSAPGsonInstance();
		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Request for Download CSV Report Controller: %s",
					json.toString());
			LOGGER.debug(msg);
		}

		try {
			Gstr6AProcessedDataRequestDto request = gson.fromJson(json,
					Gstr6AProcessedDataRequestDto.class);
			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Setting request data to entity to SaveorPersist";
				LOGGER.debug(msg);
			}
			entity.setReportType("Summary");
			entity.setReportCateg("GSTR6A");
			entity.setDataType("GSTR6A");
			entity.setStatus("active");
			entity.setCreatedBy(userName);

			List<String> docType = request.getDocType();
			if (docType != null && !docType.isEmpty()) {
				docType.replaceAll(String::toUpperCase);
			}
			if (docType.contains("CR")) {
				docType.remove("CR");
				docType.add("C");
			}
			if (docType.contains("DR")) {
				docType.remove("DR");
				docType.add("D");
			}

			String taxperiod = request.getTaxPeriod();
			List<String> tableType = request.getTableType();
			if (tableType != null && !tableType.isEmpty()) {
				tableType.replaceAll(String::toUpperCase);
			}
			String fromTaxperiod = request.getFromPeriod();
			String derivedStartPeriod = fromTaxperiod.substring(2, 6)
					+ fromTaxperiod.substring(0, 2);

			String toTaxperiod = request.getToPeriod();
			String derivedEndPeriod = toTaxperiod.substring(2, 6)
					+ toTaxperiod.substring(0, 2);

			Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

			String GSTIN = null;

			List<String> gstinList = null;

			if (!dataSecAttrs.isEmpty()) {
				for (String key : dataSecAttrs.keySet()) {

					if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
						GSTIN = key;
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
			entity.setDocType(convertToQueryFormat(docType));
			entity.setGstins(GenUtil
					.convertStringToClob(convertToQueryFormat(gstinList)));
			entity.setTableType(convertToQueryFormat(tableType));
			entity.setDerivedRetPeriodFrom(Long.valueOf(derivedStartPeriod));
			entity.setDerivedRetPeriodFromTo(Long.valueOf(derivedEndPeriod));
			entity.setCreatedDate(LocalDateTime.now());

			entity = fileStatusDownloadReportRepo.save(entity);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully saved to DB with Report Id : %d", entity.getId());
				LOGGER.debug(msg);
			}

			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", entity.getId());
			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR6A_SUMMARY_REPORT, jobParams.toString(),
					userName, 1L, null, null);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully created JOB with job params as : %s",
						jobParams.toString());
				LOGGER.debug(msg);
			}

			jobParams.addProperty("reportType", "GSTR6A Summary");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(jobParams);
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
}
