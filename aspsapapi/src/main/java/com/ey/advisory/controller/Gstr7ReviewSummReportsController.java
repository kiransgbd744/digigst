package com.ey.advisory.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.reports.GSTR7ReviewScreenHandler;
import com.ey.advisory.app.services.reports.Gstr7AspErrorReportHandler;
import com.ey.advisory.app.services.reports.Gstr7ConGStnErrorReportHandler;
import com.ey.advisory.app.services.reports.Gstr7ProcessedReportHandler;
import com.ey.advisory.app.services.reports.Gstr7ProcessedScreenHandler;
import com.ey.advisory.app.services.reports.Gstr7RefidReportHandler;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class Gstr7ReviewSummReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr7ReviewSummReportsController.class);

	@Autowired
	@Qualifier("Gstr7ProcessedReportHandler")
	private Gstr7ProcessedReportHandler gstr7ProcessedReportHandler;

	@Autowired
	@Qualifier("Gstr7AspErrorReportHandler")
	private Gstr7AspErrorReportHandler gstr7AspErrorReportHandler;

	@Autowired
	@Qualifier("Gstr7ConGStnErrorReportHandler")
	private Gstr7ConGStnErrorReportHandler gstr7ConGStnErrorReportHandler;

	@Autowired
	@Qualifier("Gstr7RefidReportHandler")
	private Gstr7RefidReportHandler gstr7RefidReportHandler;
	
	@Autowired
	@Qualifier("Gstr7ProcessedScreenHandler")
	private Gstr7ProcessedScreenHandler gstr7ProcessedScreenHandler;
	
	@Autowired
	@Qualifier("GSTR7ReviewScreenHandler")
	private GSTR7ReviewScreenHandler gstr7ReviewScreenHandler;
	
	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;


	/*@Autowired
	@Qualifier("BasicCommonSecParamRSReports")
	BasicCommonSecParamRSReports basicCommonSecParamRSReports;*/

	@RequestMapping(value = "/ui/downloadGstr7RSReports", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadreviewSummReport(@RequestBody String jsonString,
			HttpServletResponse response) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		// String groupcode = TenantContext.getTenantId();
		// TenantContext.setTenantId(groupcode);
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			String fileName = null;
			Workbook workbook = null;
			Gstr1ReviwSummReportsReqDto criteria = gson.fromJson(json,
					Gstr1ReviwSummReportsReqDto.class);

			/**
			 * Start - Set Data Security Attributes
			 */
			LOGGER.debug("DataStatus Adapter Filters Setting to Request BEGIN");

			Gstr1ReviwSummReportsReqDto setDataSecurity = processedRecordsCommonSecParam
					.setGstr7ReportDataSecuritySearchParams(criteria);


			List<String> selectedGstins = setDataSecurity.getDataSecAttrs()
					.get(OnboardingConstant.GSTIN);
			StringBuffer buffer = new StringBuffer();
			selectedGstins.forEach(gstin -> buffer.append(gstin).append("_"));
			String finalGstinString = buffer.toString().substring(0,
					buffer.toString().length() - 1);
			LOGGER.debug("DataStatus Adapter Filters Setting to Request END");
			/**
			 * End - Set Data Security Attributes
			 */

			if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(
							DownloadReportsConstant.GSTR7ASUPLOADED)) {
				workbook = gstr7ProcessedReportHandler
						.downloadProcessedReport(setDataSecurity);
				fileName = "GSTR7_ProcessedAsUploadedRecords";

			}

			else if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.GSTR7GSTNERROR)) {
				workbook = gstr7ConGStnErrorReportHandler
						.downloadConProcessedReport(setDataSecurity);
				fileName = "GSTR7_" + finalGstinString
						+ "_ConsolidatedErrorRecords";

			} else if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.GSTR7ASPERROR)) {
				workbook = gstr7AspErrorReportHandler
						.downloadErrorReport(setDataSecurity);
				fileName = "GSTR7_" + finalGstinString + "_ASPErrorRecords";

			}

			else if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(
							DownloadReportsConstant.GSTR7REFIDERROR)) {
				workbook = gstr7RefidReportHandler
						.downloadRefProcessedReport(setDataSecurity);
				fileName = "GSTR7_" + finalGstinString + "_RefIDErrorRecords";

			} 
			
			else if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(
							DownloadReportsConstant.GSTR7PROCESSSCREEN)) {
				workbook = gstr7ProcessedScreenHandler
						.getGstr1SummTablesReports(setDataSecurity);
				fileName = "GSTR7_Processed_Screen_download";

			} 
			
			else if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(
							DownloadReportsConstant.GSTR7REVIEWSCREEN)) {
				workbook = gstr7ReviewScreenHandler
						.getreviewsumReports(setDataSecurity);
				fileName = "GSTR7_ReviewSummary_Screen_download";

			} 
			
			else {
				LOGGER.error("invalid request");
				throw new Exception("invalid request");
			}
			if (workbook == null) {
				workbook = new Workbook();
			}
			if (fileName != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", String
						.format("attachment; filename=" + fileName + ".xlsx"));
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
			}
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

		} catch (Exception ex) {
			String msg = "Unexpected error while retriving "
					+ "Data from Report ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
	}
}
