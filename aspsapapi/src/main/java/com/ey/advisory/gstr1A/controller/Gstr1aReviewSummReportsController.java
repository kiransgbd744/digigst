package com.ey.advisory.gstr1A.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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
import com.ey.advisory.app.gstr1.einv.Gstr1aProcessedRecScreenHandler;
import com.ey.advisory.app.gstr1a.services.Gstr1aASPAdvAdjSavableHandler;
import com.ey.advisory.app.gstr1a.services.Gstr1aASPAdvRecSavableHandler;
import com.ey.advisory.app.gstr1a.services.Gstr1aASPB2CSSavableHandler;
import com.ey.advisory.app.gstr1a.services.Gstr1aASPHSNRateReportHandler;
import com.ey.advisory.app.gstr1a.services.Gstr1aASPHsnSummaryReportHandler;
import com.ey.advisory.app.gstr1a.services.Gstr1aASPInvSavableHandler;
import com.ey.advisory.app.gstr1a.services.Gstr1aASPVerticalProcessedReportHandler;
import com.ey.advisory.app.gstr1a.services.Gstr1aAspNilRatedReportHandler;
import com.ey.advisory.app.gstr1a.services.Gstr1aRSHsnRateLevelProcessedReportHandler;
import com.ey.advisory.app.gstr1a.services.Gstr1aReviewRateScreenHandler;
import com.ey.advisory.app.search.reports.BasicCommonSecParamRSReports;
import com.ey.advisory.app.services.reports.Gstr1ASPHSNRateReportHandler;
import com.ey.advisory.app.services.reports.Gstr1AspNilRatedReportHandler;
import com.ey.advisory.app.services.reports.Gstr1GstErrorReportHandler;
import com.ey.advisory.app.services.reports.Gstr1GstnErrorRateReportHandler;
import com.ey.advisory.app.services.reports.Gstr1GstnErrorReportHandler;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;


@RestController
public class Gstr1aReviewSummReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1aReviewSummReportsController.class);


	@Autowired
	@Qualifier("Gstr1aRSHsnRateLevelProcessedReportHandler")
	private Gstr1aRSHsnRateLevelProcessedReportHandler gstr1RSHsnRateLevelPReportHandler;

	@Autowired
	@Qualifier("Gstr1aASPB2CSSavableHandler")
	private Gstr1aASPB2CSSavableHandler gstr1ASPB2CSSavableHandler;

	@Autowired
	@Qualifier("Gstr1aASPAdvAdjSavableHandler")
	private Gstr1aASPAdvAdjSavableHandler gstr1ASPAdvAdjSavableHandler;

	@Autowired
	@Qualifier("Gstr1aASPAdvRecSavableHandler")
	private Gstr1aASPAdvRecSavableHandler gstr1ASPAdvRecSavableHandler;

	@Autowired
	@Qualifier("Gstr1aASPInvSavableHandler")
	private Gstr1aASPInvSavableHandler gstr1ASPInvSavableHandler;

	@Autowired
	@Qualifier("Gstr1aASPVerticalProcessedReportHandler")
	private Gstr1aASPVerticalProcessedReportHandler gstr1ASPVerticalProcessedReportHandler;

	@Autowired
	@Qualifier("Gstr1aASPHsnSummaryReportHandler")
	private Gstr1aASPHsnSummaryReportHandler gstr1ASPHsnSummaryReportHandler;

	@Autowired
	@Qualifier("Gstr1aASPHSNRateReportHandler")
	private Gstr1aASPHSNRateReportHandler gstr1ASPHSNRateReportHandler;

	@Autowired
	@Qualifier("Gstr1aAspNilRatedReportHandler")
	private Gstr1aAspNilRatedReportHandler gstr1AspNilRatedReportHandler;

	@Autowired
	@Qualifier("Gstr1aProcessedRecScreenHandler")
	private Gstr1aProcessedRecScreenHandler gstr1ProcessedRecScreenHandler;

	@Autowired
	@Qualifier("Gstr1aReviewRateScreenHandler")
	private Gstr1aReviewRateScreenHandler gstr1ReviewRateScreenHandler;

	@Autowired
	@Qualifier("BasicCommonSecParamRSReports")
	BasicCommonSecParamRSReports basicCommonSecParamRSReports;
	
	@Autowired
	@Qualifier("Gstr1GstnErrorReportHandler")
	private Gstr1GstnErrorReportHandler gstr1GstnErrorReportHandler;

	@Autowired
	@Qualifier("Gstr1GstnErrorRateReportHandler")
	private Gstr1GstnErrorRateReportHandler gstr1GstnErrorRateReportHandler;

	@Autowired
	@Qualifier("Gstr1GstErrorReportHandler")
	private Gstr1GstErrorReportHandler gstr1GstErrorReportHandler;

	

	@Autowired
	@Qualifier("GstnApi")
	GstnApi gstnApi;

	@RequestMapping(value = "/ui/downloadGstr1aRSReports", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadreviewSummReport(@RequestBody String jsonString,
			HttpServletResponse response) {

        JsonObject requestObject = JsonParser.parseString(jsonString).getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		// String groupcode = TenantContext.getTenantId();
		// TenantContext.setTenantId(groupcode);
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			String fileName = null;
			Workbook workbook = null;
			Gstr1ReviwSummReportsReqDto criteria = gson.fromJson(json,
					Gstr1ReviwSummReportsReqDto.class);
			
			criteria.setReturnType(APIConstants.GSTR1A.toUpperCase());


			/**
			 * Start - Set Data Security Attributes
			 */
			LOGGER.debug("DataStatus Adapter Filters Setting to Request BEGIN");
			
			Gstr1ReviwSummReportsReqDto setDataSecurity = basicCommonSecParamRSReports
					.setGstr1DataSecuritySearchParams(criteria);
			
			List<String> selectedGstins = setDataSecurity.getDataSecAttrs()
					.get(OnboardingConstant.GSTIN);
			LOGGER.debug("DataStatus Adapter Filters Setting to Request END");
			/**
			 * End - Set Data Security Attributes
			 */
			StringBuffer buffer = new StringBuffer();
			
			selectedGstins.forEach(gstin -> buffer.append(gstin).append("_"));
			String finalGstinString = buffer.toString().substring(0,
					buffer.toString().length() - 1);
			
			
			if (criteria.getDataType() != null && criteria.getDataType()
					.equalsIgnoreCase(DownloadReportsConstant.OUTWARD)) {
				 if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.PROCESSEDSAVEABLE)) {
					workbook = gstr1RSHsnRateLevelPReportHandler
							.downloadRSHsnProcessedReport(setDataSecurity);
					fileName = "Gstr1A_HsnRateLevelRrocessedRecords";
					//required
				} else if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.B2CSSAVABLE)) {
					workbook = gstr1ASPB2CSSavableHandler
							.getGstr1B2CSSavableReports(setDataSecurity);
					fileName = "GSTR-1A_ASP_B2CS_Savable";
					//required
				} else if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.ASPPROCESSEDVERTICAL)) {
					workbook = gstr1ASPVerticalProcessedReportHandler
							.downloadGstr1VerticalProcessedReport(
									setDataSecurity);
					fileName = "GSTR1A_AspProcessedRecordsOutwardVertical";
					//required(summary level)
				} else if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.ADVRECSAVABLE)) {
					workbook = gstr1ASPAdvRecSavableHandler
							.getGstr1AdvRecSavableReports(setDataSecurity);
					fileName = "GSTR1A_ASP_Advance_Received_Savable";
					//required
				} else if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.ADVADJSAVABLE)) {
					workbook = gstr1ASPAdvAdjSavableHandler
							.getGstr1AdjSavableReports(setDataSecurity);
					fileName = "GSTR-1A_ASP_Advance_Adjusted_Savable";
					//required
				} else if (criteria.getType() != null && criteria.getType()
						.equalsIgnoreCase(DownloadReportsConstant.INVSAVABLE)) {
					workbook = gstr1ASPInvSavableHandler
							.getGstr1InvSavableReports(setDataSecurity);
					fileName = "GSTR-1A_ASP_Invoice_Series_Savable";
					//required
				} else if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.NILRATESAVABLE)) {
					workbook = gstr1AspNilRatedReportHandler
							.downloadGstr1NilRatedReport(setDataSecurity);
					fileName = "GSTR1A_ASP_NIL_EXEMPT_NON_Savable";
					//required
				} else if (criteria.getType() != null && criteria.getType()
						.equalsIgnoreCase(DownloadReportsConstant.HSNSUMMARY)) {
					
					Boolean rateIncludedInHsn = gstnApi
							.isRateIncludedInHsn(criteria.getTaxperiod());
					if (rateIncludedInHsn) {
						workbook = gstr1ASPHSNRateReportHandler
								.downloadGstr1HsnRateReport(setDataSecurity);
						fileName = "GSTR-1A_ASP_HSN_Summary_Rate_savable";
					} else {
						workbook = gstr1ASPHsnSummaryReportHandler
								.downloadGstr1HsnSummaryReport(setDataSecurity);
						fileName = "GSTR-1A_ASP_HSN_Summary_Savable";
					}
					//required
				} 	else if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.PROCESSRECSCREEN)) {
					workbook = gstr1ProcessedRecScreenHandler
							.getGstr1SummTablesReports(setDataSecurity);
					fileName = "GSTR-1A_Processed_Records_Screen_Download";
				} else if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.GSTR1REVIEWSUMMARY)) {
					Boolean rateIncludedInHsn = gstnApi
							.isRateIncludedInHsn(criteria.getTaxperiod());
					if (rateIncludedInHsn) {
						workbook = gstr1ReviewRateScreenHandler
								.getGstr1RevSummRateTablesReports(
										setDataSecurity);
						fileName = "GSTR-1A_Review_Summary_Screen_Rate_Download";
					} else {
						workbook = gstr1ProcessedRecScreenHandler
								.getGstr1RevSummTablesReports(setDataSecurity);
						fileName = "GSTR-1A_Review_Summary_Screen_Download";
					}
				}else if (criteria.getType() != null && criteria.getType()

						.equalsIgnoreCase(DownloadReportsConstant.GSTNERROR)) {
					if (gstnApi.isDelinkingEligible(
							APIConstants.GSTR1A.toUpperCase())) {
						workbook = gstr1GstnErrorReportHandler
								.getGstr1InvSavableReports(setDataSecurity);
					} else {
						// Write new handler to fetch the limited columns that
						// is required in report
						workbook = gstr1GstErrorReportHandler
								.getGstr1GstSavableReports(setDataSecurity);
					}

					Boolean rateIncludedInHsn = gstnApi
							.isRateIncludedInHsn(criteria.getTaxperiod());
					if (rateIncludedInHsn) {
						workbook = gstr1GstnErrorRateReportHandler
								.getGstr1Rate(setDataSecurity);
					} else {
						// Write new handler to fetch the limited column that
						// is required in report
						workbook = gstr1GstErrorReportHandler
								.getGstr1GstSavableReports(setDataSecurity);
					}

					if (StringUtils.isEmpty(criteria.getGstnRefId())) {
						fileName = "GSTR1A_" + finalGstinString + "_"
								+ criteria.getTaxperiod()
								+ "_Consolidated_PE_ERROR_GSTN";
					} else {
						fileName = "GSTR1A_" + finalGstinString + "_"
								+ criteria.getTaxperiod() + "_PE_ERROR_GSTN";
					}
				}


			} else {
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
