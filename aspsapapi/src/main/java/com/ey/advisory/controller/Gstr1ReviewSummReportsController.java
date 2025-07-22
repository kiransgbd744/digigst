/**
 * 
 */
package com.ey.advisory.controller;

import java.util.List;

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
import com.ey.advisory.app.search.reports.BasicCommonSecParamRSReports;
import com.ey.advisory.app.services.reports.Gstr1ASPAdvAdjSavableHandler;
import com.ey.advisory.app.services.reports.Gstr1ASPAdvRecSavableHandler;
import com.ey.advisory.app.services.reports.Gstr1ASPB2CSSavableHandler;
import com.ey.advisory.app.services.reports.Gstr1ASPHSNRateReportHandler;
import com.ey.advisory.app.services.reports.Gstr1ASPHsnSummaryReportHandler;
import com.ey.advisory.app.services.reports.Gstr1ASPInvSavableHandler;
import com.ey.advisory.app.services.reports.Gstr1ASPVerticalProcessedReportHandler;
import com.ey.advisory.app.services.reports.Gstr1AspNilRatedReportHandler;
import com.ey.advisory.app.services.reports.Gstr1EntityLevelSummaryReportHandler;
import com.ey.advisory.app.services.reports.Gstr1GstErrorReportHandler;
import com.ey.advisory.app.services.reports.Gstr1GstTransReportHandler;
import com.ey.advisory.app.services.reports.Gstr1GstnErrorRateReportHandler;
import com.ey.advisory.app.services.reports.Gstr1GstnErrorReportHandler;
import com.ey.advisory.app.services.reports.Gstr1GstnSummaryTablesHandler;
import com.ey.advisory.app.services.reports.Gstr1GstnTransReportHandler;
import com.ey.advisory.app.services.reports.Gstr1ProcessedRecScreenHandler;
import com.ey.advisory.app.services.reports.Gstr1RSHsnRateLevelProcessedReportHandler;
import com.ey.advisory.app.services.reports.Gstr1RSumErrorAspReportHandler;
import com.ey.advisory.app.services.reports.Gstr1ReviewRateScreenHandler;
import com.ey.advisory.app.services.reports.Gstr1ReviewSummProcessedReportHandler;
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

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Laxmi.Salukuti
 *
 */
@RestController
public class Gstr1ReviewSummReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1ReviewSummReportsController.class);

	@Autowired
	@Qualifier("Gstr1ReviewSummProcessedReportHandler")
	private Gstr1ReviewSummProcessedReportHandler gstr1RSProcessedReportHandler;

	@Autowired
	@Qualifier("Gstr1RSumErrorAspReportHandler")
	private Gstr1RSumErrorAspReportHandler gstr1RSumErrorAspReportHandler;

	@Autowired
	@Qualifier("Gstr1RSHsnRateLevelProcessedReportHandler")
	private Gstr1RSHsnRateLevelProcessedReportHandler gstr1RSHsnRateLevelPReportHandler;

	@Autowired
	@Qualifier("Gstr1ASPB2CSSavableHandler")
	private Gstr1ASPB2CSSavableHandler gstr1ASPB2CSSavableHandler;

	@Autowired
	@Qualifier("Gstr1ASPAdvAdjSavableHandler")
	private Gstr1ASPAdvAdjSavableHandler gstr1ASPAdvAdjSavableHandler;

	@Autowired
	@Qualifier("Gstr1ASPAdvRecSavableHandler")
	private Gstr1ASPAdvRecSavableHandler gstr1ASPAdvRecSavableHandler;

	@Autowired
	@Qualifier("Gstr1ASPInvSavableHandler")
	private Gstr1ASPInvSavableHandler gstr1ASPInvSavableHandler;

	@Autowired
	@Qualifier("Gstr1ASPVerticalProcessedReportHandler")
	private Gstr1ASPVerticalProcessedReportHandler gstr1ASPVerticalProcessedReportHandler;

	@Autowired
	@Qualifier("Gstr1ASPHsnSummaryReportHandler")
	private Gstr1ASPHsnSummaryReportHandler gstr1ASPHsnSummaryReportHandler;

	@Autowired
	@Qualifier("Gstr1ASPHSNRateReportHandler")
	private Gstr1ASPHSNRateReportHandler gstr1ASPHSNRateReportHandler;

	@Autowired
	@Qualifier("Gstr1AspNilRatedReportHandler")
	private Gstr1AspNilRatedReportHandler gstr1AspNilRatedReportHandler;

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
	@Qualifier("Gstr1GstnTransReportHandler")
	private Gstr1GstnTransReportHandler gstr1GstnTransReportHandler;

	@Autowired
	@Qualifier("Gstr1GstTransReportHandler")
	private Gstr1GstTransReportHandler gstr1GstTransReportHandler;

	@Autowired
	@Qualifier("Gstr1GstnSummaryTablesHandler")
	private Gstr1GstnSummaryTablesHandler gstr1GstnSummaryTablesHandler;

	@Autowired
	@Qualifier("Gstr1ProcessedRecScreenHandler")
	private Gstr1ProcessedRecScreenHandler gstr1ProcessedRecScreenHandler;

	@Autowired
	@Qualifier("Gstr1ReviewRateScreenHandler")
	private Gstr1ReviewRateScreenHandler gstr1ReviewRateScreenHandler;

	@Autowired
	@Qualifier("Gstr1EntityLevelSummaryReportHandler")
	private Gstr1EntityLevelSummaryReportHandler gstr1EntityLevelSummaryReportHandler;

	@Autowired
	@Qualifier("BasicCommonSecParamRSReports")
	BasicCommonSecParamRSReports basicCommonSecParamRSReports;
	

	@Autowired
	@Qualifier("GstnApi")
	GstnApi gstnApi;

	@RequestMapping(value = "/ui/downloadGstr1RSReports", method = RequestMethod.POST, produces = {
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
			
			if(criteria.getReturnType()==null || criteria.getReturnType().isEmpty()){
				criteria.setReturnType(APIConstants.GSTR1.toUpperCase());
			}

			/**
			 * Start - Set Data Security Attributes
			 */
			LOGGER.debug("DataStatus Adapter Filters Setting to Request BEGIN");
			
			Gstr1ReviwSummReportsReqDto setDataSecurity = basicCommonSecParamRSReports
					.setGstr1DataSecuritySearchParams(criteria);

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

			if (criteria.getDataType() != null && criteria.getDataType()
					.equalsIgnoreCase(DownloadReportsConstant.OUTWARD)) {
				if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.PROCESSEDASUPLOADED)) {
					workbook = gstr1RSProcessedReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "Gstr1_ProcessedTransactionalRecords";
				} else if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.PROCESSEDSAVEABLE)) {
					workbook = gstr1RSHsnRateLevelPReportHandler
							.downloadRSHsnProcessedReport(setDataSecurity);
					fileName = "Gstr1_HsnRateLevelRrocessedRecords";
				} else if (criteria.getType() != null && criteria.getType()
						.equalsIgnoreCase(DownloadReportsConstant.ASPERROR)) {
					workbook = gstr1RSumErrorAspReportHandler
							.downloadRErrorAspReport(setDataSecurity);
					fileName = "GSTR1_" + finalGstinString + "_"
							+ criteria.getTaxperiod()
							+ "_Consolidated_PE_ERROR_DigiGST";
				} else if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.B2CSSAVABLE)) {
					workbook = gstr1ASPB2CSSavableHandler
							.getGstr1B2CSSavableReports(setDataSecurity);
					fileName = "GSTR-1_ASP_B2CS_Savable";
				} else if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.ASPPROCESSEDVERTICAL)) {
					workbook = gstr1ASPVerticalProcessedReportHandler
							.downloadGstr1VerticalProcessedReport(
									setDataSecurity);
					fileName = "Gstr1_AspProcessedRecordsOutwardVertical";
				} else if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.ADVRECSAVABLE)) {
					workbook = gstr1ASPAdvRecSavableHandler
							.getGstr1AdvRecSavableReports(setDataSecurity);
					fileName = "GSTR1_ASP_Advance_Received_Savable";
				} else if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.ADVADJSAVABLE)) {
					workbook = gstr1ASPAdvAdjSavableHandler
							.getGstr1AdjSavableReports(setDataSecurity);
					fileName = "GSTR-1_ASP_Advance_Adjusted_Savable";
				} else if (criteria.getType() != null && criteria.getType()
						.equalsIgnoreCase(DownloadReportsConstant.INVSAVABLE)) {
					workbook = gstr1ASPInvSavableHandler
							.getGstr1InvSavableReports(setDataSecurity);
					fileName = "GSTR-1_ASP_Invoice_Series_Savable";
				} else if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.NILRATESAVABLE)) {
					workbook = gstr1AspNilRatedReportHandler
							.downloadGstr1NilRatedReport(setDataSecurity);
					fileName = "GSTR1_ASP_NIL_EXEMPT_NON_Savable";
				} else if (criteria.getType() != null && criteria.getType()
						.equalsIgnoreCase(DownloadReportsConstant.HSNSUMMARY)) {
					
					Boolean rateIncludedInHsn = gstnApi
							.isRateIncludedInHsn(criteria.getTaxperiod());
					if (rateIncludedInHsn) {
						workbook = gstr1ASPHSNRateReportHandler
								.downloadGstr1HsnRateReport(setDataSecurity);
						fileName = "GSTR-1_ASP_HSN_Summary_Rate_savable";
					} else {
						workbook = gstr1ASPHsnSummaryReportHandler
								.downloadGstr1HsnSummaryReport(setDataSecurity);
						fileName = "GSTR-1_ASP_HSN_Summary_Savable";
					}
				} else if (criteria.getType() != null && criteria.getType()

						.equalsIgnoreCase(DownloadReportsConstant.GSTNERROR)) {
					if (gstnApi.isDelinkingEligible(
							APIConstants.GSTR1.toUpperCase())) {
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
						fileName = "GSTR1_" + finalGstinString + "_"
								+ criteria.getTaxperiod()
								+ "_Consolidated_PE_ERROR_GSTN";
					} else {
						fileName = "GSTR1_" + finalGstinString + "_"
								+ criteria.getTaxperiod() + "_PE_ERROR_GSTN";
					}
				}

				else if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.TRANSACTIONAL)) {
					if (gstnApi.isDelinkingEligible(
							APIConstants.GSTR1.toUpperCase())) {
						workbook = gstr1GstnTransReportHandler
								.getGstr1InvSavableReports(setDataSecurity);
						fileName = "GSTR1_" + criteria.getTaxperiod()
								+ "_GSTN_Data_Transactional_Data";
					} else {
						workbook = gstr1GstTransReportHandler
								.getGstr1gstSavableReports(setDataSecurity);
						fileName = "GSTR1_" + criteria.getTaxperiod()
								+ "_GSTN_Data_Transactional_Data";
					}
				} else if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.SUMMARYTABLES)) {
					workbook = gstr1GstnSummaryTablesHandler
							.getGstr1SummTablesReports(setDataSecurity);
					fileName = "GSTR-1_GSTN_Summary_Tables_Saved_Submitted";
				}

				else if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.PROCESSRECSCREEN)) {
					workbook = gstr1ProcessedRecScreenHandler
							.getGstr1SummTablesReports(setDataSecurity);
					fileName = "GSTR-1_Processed_Records_Screen_Download";
				} else if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.GSTR1REVIEWSUMMARY)) {
					Boolean rateIncludedInHsn = gstnApi
							.isRateIncludedInHsn(criteria.getTaxperiod());
					if (rateIncludedInHsn) {
						workbook = gstr1ReviewRateScreenHandler
								.getGstr1RevSummRateTablesReports(
										setDataSecurity);
						fileName = "GSTR-1_Review_Summary_Screen_Rate_Download";
					} else {
						workbook = gstr1ProcessedRecScreenHandler
								.getGstr1RevSummTablesReports(setDataSecurity);
						fileName = "GSTR-1_Review_Summary_Screen_Download";
					}
				}

				else if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.GSTR1ENTITYLEVELSUMMARY)) {
					workbook = gstr1EntityLevelSummaryReportHandler
							.downloadEntityLevelSummary(setDataSecurity);
					fileName = "GSTR-1_Entity_Level_Summary_Download";
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
