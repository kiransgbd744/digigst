/**
 * 
 */
package com.ey.advisory.controller;

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
import com.ey.advisory.app.services.reports.ANX1EntityLevelSummaryReportHandler;
import com.ey.advisory.app.services.reports.Anx1AspErrorProcessedReportHandler;
import com.ey.advisory.app.services.reports.Anx1AspInwardProcessedUploadedReportHandler;
import com.ey.advisory.app.services.reports.Anx1AspSavableProcessedReportHandler;
import com.ey.advisory.app.services.reports.Anx1GSTNErrorReportHandler;
import com.ey.advisory.app.services.reports.Anx1ProcessedAsUploadedReportHandler;
import com.ey.advisory.app.services.reports.Anx1ProcessedAspB2CReportHandler;
import com.ey.advisory.app.services.reports.Anx1ProcessedAspEcomReportHandler;
import com.ey.advisory.app.services.reports.Anx1ProcessedAspImpsReportHandler;
import com.ey.advisory.app.services.reports.Anx1ProcessedAspRcReportHandler;
import com.ey.advisory.app.services.reports.Anx1ProcessedAspVerticalReportHandler;
import com.ey.advisory.app.services.reports.Anx1ProcessedScreenDownloadHandler;
import com.ey.advisory.app.services.reports.Anx1ReviwDetailedScreenDownloadHandler;
import com.ey.advisory.app.services.reports.Anx1ReviwSummScreenDownloadHandler;
import com.ey.advisory.app.services.reports.Anx1SumLevelReportHandler;
import com.ey.advisory.app.services.reports.Anx1TransactionalLevelReportHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.dto.Anx1ProcessedRecordsReqDto;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@RestController
public class Anx1ReviewSummReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ReviewSummReportsController.class);

	@Autowired
	@Qualifier("ANX1EntityLevelSummaryReportHandler")
	private ANX1EntityLevelSummaryReportHandler aNX1EntityLevelSummaryReportHandler;

	@Autowired
	@Qualifier("Anx1TransactionalLevelReportHandler")
	private Anx1TransactionalLevelReportHandler anx1TransactionalLevelReportHandler;

	@Autowired
	@Qualifier("Anx1SumLevelReportHandler")
	private Anx1SumLevelReportHandler anx1SumLevelReportHandler;

	@Autowired
	@Qualifier("BasicCommonSecParamRSReports")
	BasicCommonSecParamRSReports basicCommonSecParamRSReports;

	@Autowired
	@Qualifier("Anx1GSTNErrorReportHandler")
	private Anx1GSTNErrorReportHandler anx1GSTNErrorReportHandler;

	@Autowired
	@Qualifier("Anx1ProcessedAspVerticalReportHandler")
	private Anx1ProcessedAspVerticalReportHandler anx1ProcessedAspVerticalReportHandler;

	@Autowired
	@Qualifier("Anx1ProcessedAsUploadedReportHandler")
	private Anx1ProcessedAsUploadedReportHandler anx1AspUploadedReportHandler;

	@Autowired
	@Qualifier("Anx1AspInwardProcessedUploadedReportHandler")
	private Anx1AspInwardProcessedUploadedReportHandler aspInwardUploadedReportHandler;

	@Autowired
	@Qualifier("Anx1ProcessedAspImpsReportHandler")
	private Anx1ProcessedAspImpsReportHandler anx1ProcessedAspImpsReportHandler;

	@Autowired
	@Qualifier("Anx1ProcessedAspRcReportHandler")
	private Anx1ProcessedAspRcReportHandler anx1ProcessedAspRcReportHandler;

	@Autowired
	@Qualifier("Anx1ReviwSummScreenDownloadHandler")
	private Anx1ReviwSummScreenDownloadHandler anx1ReviwSummScreenDownloadHandler;

	@Autowired
	@Qualifier("Anx1ReviwDetailedScreenDownloadHandler")
	private Anx1ReviwDetailedScreenDownloadHandler anx1ReviwSummDetailedScreenDownloadHandler;

	@Autowired
	@Qualifier("Anx1ProcessedScreenDownloadHandler")
	private Anx1ProcessedScreenDownloadHandler anx1ProcessedScreenDownloadHandler;

	@Autowired
	@Qualifier("Anx1AspErrorProcessedReportHandler")
	private Anx1AspErrorProcessedReportHandler aspErrorProcessedReportHandler;

	@Autowired
	@Qualifier("Anx1AspSavableProcessedReportHandler")
	private Anx1AspSavableProcessedReportHandler aspSavableProcessedReportHandler;

	@Autowired
	@Qualifier("Anx1ProcessedAspB2CReportHandler")
	private Anx1ProcessedAspB2CReportHandler anx1ProcessedAspB2CReportHandler;

	@Autowired
	@Qualifier("Anx1ProcessedAspEcomReportHandler")
	private Anx1ProcessedAspEcomReportHandler anx1ProcessedAspEcomReportHandler;

	@RequestMapping(value = "/ui/downloadAnx1RSReports", method = RequestMethod.POST, produces = {
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

			Gstr1ReviwSummReportsReqDto setDataSecurity = basicCommonSecParamRSReports
					.setDataSecuritySearchParams(criteria);

			LOGGER.debug("DataStatus Adapter Filters Setting to Request END");
			/**
			 * End - Set Data Security Attributes
			 */

			if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.ANXGSTNERROR)) {
				workbook = anx1GSTNErrorReportHandler
						.getAnx1GstnReports(setDataSecurity);
				fileName = "ANX1_GSTN_Error_Report";
			}

			else if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(
							DownloadReportsConstant.ANXENTITYLEVELSUMMARY)) {
				workbook = aNX1EntityLevelSummaryReportHandler
						.downloadEntityLevelSummary(setDataSecurity);
				fileName = "Anx1_EntityLevelSummary";
			}

			else if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(
							DownloadReportsConstant.ANXTRANSACTIONAL)) {
				workbook = anx1TransactionalLevelReportHandler
						.downloadTransLevelSummary(setDataSecurity);
				fileName = "ANX1_GSTN_Transactional_Level_Tables_Saved_Submitted";
			}

			else if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.ANXSUMMARY)) {
				workbook = anx1SumLevelReportHandler
						.downloadSumLevelSummary(setDataSecurity);
				fileName = "ANX-1_GSTN_Summary_Tables_SavedSubmitted";
			}

			else if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(
							DownloadReportsConstant.ANX1REVIEWSUMMARY)) {
				Annexure1SummaryReqDto dto = new Annexure1SummaryReqDto();

				dto.setTaxPeriod(setDataSecurity.getTaxperiod());
				dto.setDataSecAttrs(setDataSecurity.getDataSecAttrs());
				dto.setEntityId(setDataSecurity.getEntityId());
				workbook = anx1ReviwSummScreenDownloadHandler
						.findRSummScreenDownload(dto);
				fileName = "ANX-1_Review_Summary_Screen_Download";
			}

			else if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(
							DownloadReportsConstant.ANX1DETAILEDSUMMARY)) {
				Annexure1SummaryReqDto dto = new Annexure1SummaryReqDto();

				dto.setTaxPeriod(setDataSecurity.getTaxperiod());
				dto.setDataSecAttrs(setDataSecurity.getDataSecAttrs());
				dto.setEntityId(setDataSecurity.getEntityId());
				workbook = anx1ReviwSummDetailedScreenDownloadHandler
						.findReviewSumScreenDetailDownload(dto);
				fileName = "ANX-1_Review_Summary_Detailed_Screen_Download";
			}

			else if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(
							DownloadReportsConstant.ANX1PROCESSEDSUMMARY)) {
				Anx1ProcessedRecordsReqDto dto = new Anx1ProcessedRecordsReqDto(
						fileName);
				dto.setTaxPeriod(setDataSecurity.getTaxperiod());
				dto.setDataSecAttrs(setDataSecurity.getDataSecAttrs());
				dto.setEntityId(setDataSecurity.getEntityId());
				dto.setGstnUploadDate(setDataSecurity.getGstnUploadDate());
				workbook = anx1ProcessedScreenDownloadHandler
						.findProcessedDownload(dto);
				fileName = "ANX-1_Processed_Records_Screen_Download";
			}

			else if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(
							DownloadReportsConstant.ANX1ASPPROCESSEDVERTICAL)) {
				workbook = anx1ProcessedAspVerticalReportHandler
						.findProcessedVertical(setDataSecurity);
				fileName = "ANX-1_ASP_Processed_Records_Vertical";
			} else if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(
							DownloadReportsConstant.ANX1ASPPROCESSEASUPLOADED)) {
				workbook = anx1AspUploadedReportHandler
						.findProcessedUploaded(setDataSecurity);
				fileName = "ANX-1_ASP_Processed_Records_Transactional";
			} else if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(
							DownloadReportsConstant.ANX1CONSOLIDATEDERRORUPLOADED)) {
				workbook = aspErrorProcessedReportHandler
						.findErrorUploaded(setDataSecurity);
				fileName = "ANX-1_Consolidated_Error_Report";
			}

			else if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.IMPSSAVABLE)) {
				workbook = anx1ProcessedAspImpsReportHandler
						.findAnx1IMPSSavableReports(setDataSecurity);
				fileName = "ANX-1_ASP_ImportofServices3I_Savable";
			}

			else if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.RCSAVABLE)) {
				workbook = anx1ProcessedAspRcReportHandler
						.findRCSavableReports(setDataSecurity);
				fileName = "ANX-1_ASP_ReverseCharge3H_Savable";
			} else if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(
							DownloadReportsConstant.ANX1SAVABLEASPDATA)) {
				workbook = aspSavableProcessedReportHandler
						.findSavableUploaded(setDataSecurity);
				fileName = "ANX-1_ASP_Transactional_Savable";
			} else if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.B2CSAVABLE)) {
				workbook = anx1ProcessedAspB2CReportHandler
						.findAnx1B2CSavableReports(setDataSecurity);
				fileName = "ANX-1_ASP_B2C_Savable";
			}

			else if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.ECOMSAVABLE)) {
				workbook = anx1ProcessedAspEcomReportHandler
						.findAnx1EcomSavableReports(setDataSecurity);
				fileName = "ANX-1_ASP_ECOM_Savable";
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
