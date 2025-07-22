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
import com.ey.advisory.app.search.reports.BasicCommonSecParamRSReports;
import com.ey.advisory.app.services.reports.AspAdvAdjAmenSecReportHandler;
import com.ey.advisory.app.services.reports.AspAdvAdjSecReportHandler;
import com.ey.advisory.app.services.reports.AspAdvRecAmenSecReportHandler;
import com.ey.advisory.app.services.reports.AspAdvRecSecReportHandler;
import com.ey.advisory.app.services.reports.AspB2BASectionReportHandler;
import com.ey.advisory.app.services.reports.AspB2BSectionReportHandler;
import com.ey.advisory.app.services.reports.AspB2CLASectionReportHandler;
import com.ey.advisory.app.services.reports.AspB2CLSectionReportHandler;
import com.ey.advisory.app.services.reports.AspB2CSASectionReportHandler;
import com.ey.advisory.app.services.reports.AspB2CSSectionReportHandler;
import com.ey.advisory.app.services.reports.AspCDNRASectionReportHandler;
import com.ey.advisory.app.services.reports.AspCDNRSectionReportHandler;
import com.ey.advisory.app.services.reports.AspCDNURASectionReportHandler;
import com.ey.advisory.app.services.reports.AspCDNURSectionReportHandler;
import com.ey.advisory.app.services.reports.AspEXPASectionReportHandler;
import com.ey.advisory.app.services.reports.AspEXPSectionReportHandler;
import com.ey.advisory.app.services.reports.AspNilSectionReportHandler;
import com.ey.advisory.app.services.reports.AspSecDocReportHandler;
import com.ey.advisory.app.services.reports.AspSecHsnSummaryReportHandler;
import com.ey.advisory.app.services.reports.AspSezWithTaxReportHandler;
import com.ey.advisory.app.services.reports.AspSezWopReportHandler;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class AspProcessSubmitSectionwiseReportsController {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AspProcessSubmitSectionwiseReportsController.class);
	
	@Autowired
	@Qualifier("AspB2BSectionReportHandler")
	private AspB2BSectionReportHandler aspB2BSectionReportHandler;

	@Autowired
	@Qualifier("AspB2BASectionReportHandler")
	private AspB2BASectionReportHandler aspB2BASectionReportHandler;

	@Autowired
	@Qualifier("AspB2CLSectionReportHandler")
	private AspB2CLSectionReportHandler aspB2CLSectionReportHandler;

	@Autowired
	@Qualifier("AspB2CLASectionReportHandler")
	private AspB2CLASectionReportHandler aspB2CLASectionReportHandler;

	@Autowired
	@Qualifier("AspEXPSectionReportHandler")
	private AspEXPSectionReportHandler aspEXPSectionReportHandler;

	@Autowired
	@Qualifier("AspEXPASectionReportHandler")
	private AspEXPASectionReportHandler aspEXPASectionReportHandler;

	@Autowired
	@Qualifier("AspCDNRSectionReportHandler")
	private AspCDNRSectionReportHandler  aspCDNRSectionReportHandler;

	@Autowired
	@Qualifier("AspCDNRASectionReportHandler")
	private AspCDNRASectionReportHandler aspCDNRASectionReportHandler;

	@Autowired
	@Qualifier("AspCDNURSectionReportHandler")
	private AspCDNURSectionReportHandler aspCDNURSectionReportHandler;

	@Autowired
	@Qualifier("AspCDNURASectionReportHandler")
	private AspCDNURASectionReportHandler aspCDNURASectionReportHandler;

	@Autowired
	@Qualifier("AspB2CSSectionReportHandler")
	private AspB2CSSectionReportHandler aspB2CSSectionReportHandler;

	@Autowired
	@Qualifier("AspB2CSASectionReportHandler")
	private AspB2CSASectionReportHandler aspB2CSASectionReportHandler;

	@Autowired
	@Qualifier("AspNilSectionReportHandler")
	private AspNilSectionReportHandler aspNilSectionReportHandler;

	@Autowired
	@Qualifier("AspAdvRecSecReportHandler")
	private AspAdvRecSecReportHandler aspAdvRecSecReportHandler;

	@Autowired
	@Qualifier("AspAdvRecAmenSecReportHandler")
	private AspAdvRecAmenSecReportHandler aspAdvRecAmenSecReportHandler;

	@Autowired
	@Qualifier("AspAdvAdjSecReportHandler")
	private AspAdvAdjSecReportHandler aspAdvAdjSecReportHandler;

	@Autowired
	@Qualifier("AspAdvAdjAmenSecReportHandler")
	private  AspAdvAdjAmenSecReportHandler  aspAdvAdjAmenSecReportHandler;

	@Autowired
	@Qualifier("AspSecHsnSummaryReportHandler")
	private AspSecHsnSummaryReportHandler aspSecHsnSummaryReportHandler;

	@Autowired
	@Qualifier("AspSecDocReportHandler")
	private AspSecDocReportHandler aspSecDocReportHandler;

	@Autowired
	@Qualifier("AspSezWithTaxReportHandler")
	private AspSezWithTaxReportHandler aspSezWithTaxReportHandler;
	
	@Autowired
	@Qualifier("AspSezWopReportHandler")
	private AspSezWopReportHandler aspSezWopReportHandler;

	@Autowired
	@Qualifier("BasicCommonSecParamRSReports")
	BasicCommonSecParamRSReports basicCommonSecParamRSReports;

	@RequestMapping(value = "/ui/downloadAspvsSubmittedSecReports", method = RequestMethod.POST, produces = {
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
				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.B2B)) {
					workbook = aspB2BSectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_" + criteria.getTaxPeriodTo()
							+ "_B2B_SECTION4_SECTION6B_SECTION6C";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.B2BA)) {
					workbook = aspB2BASectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_" + criteria.getTaxPeriodTo() + "_B2BA_SECTION9A";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.B2CL)) {
					workbook = aspB2CLSectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_" + criteria.getTaxPeriodTo() + "_B2CL_SECTION5";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.B2CLA)) {
					workbook = aspB2CLASectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_" + criteria.getTaxPeriodTo() + "_B2CLA_SECTION9A";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.EXPORTS)) {
					workbook = aspEXPSectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "GSTR-1_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_" + criteria.getTaxPeriodTo() + "_EXPORTS_SECTION6A";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.EXPORTA)) {
					workbook = aspEXPASectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_" + criteria.getTaxPeriodTo()
							+ "_EXPORTS_Amended_SECTION9A";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.CDNR)) {
					workbook = aspCDNRSectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_" + criteria.getTaxPeriodTo()
							+ "_CDNR_SECTION9B";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.CDNRA)) {
					workbook = aspCDNRASectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_" + criteria.getTaxPeriodTo()
							+ "_CDNRA_SECTION9C";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.CDNUR)) {
					workbook = aspCDNURSectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_" + criteria.getTaxPeriodTo()
							+ "_CDNUR_SECTION9B";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.CDNURA)) {
					workbook = aspCDNURASectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_" + criteria.getTaxPeriodTo()
							+ "_CDNURA_SECTION9C";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.B2CS1)) {
					workbook = aspB2CSSectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_" + criteria.getTaxPeriodTo()
							+ "_B2CS_SECTION7";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.B2CSA)) {
					workbook = aspB2CSASectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_" + criteria.getTaxPeriodTo()
							+ "_B2CSA_SECTION10";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.NILEXTNON)) {
					workbook = aspNilSectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_" + criteria.getTaxPeriodTo()
							+ "_NIL_EXEMPT_NON_GST_SUPPLIES";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.ADVREC)) {
					workbook = aspAdvRecSecReportHandler
							.getGstr1AdvRecSavableReports(setDataSecurity);
					fileName = "ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_" + criteria.getTaxPeriodTo()
							+ "_ADV_RECEIVED_SECTION11";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.ADVADJ)) {
					workbook = aspAdvAdjSecReportHandler
							.getGstr1AdjSavableReports(setDataSecurity);
					fileName = "ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_" + criteria.getTaxPeriodTo()
							+ "_ADV_ADJUSTED_SECTION11";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.ADVRECA)) {
					workbook = aspAdvRecAmenSecReportHandler
							.getGstr1AdvRecSavableReports(setDataSecurity);
					fileName = "ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_" + criteria.getTaxPeriodTo()
							+ "_ADV_RECEIVED_AMENDED_SECTION11";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.ADVADJA)) {
					workbook = aspAdvAdjAmenSecReportHandler
							.getGstr1AdjSavableReports(setDataSecurity);
					fileName = "ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_" + criteria.getTaxPeriodTo()
							+ "_ADV_ADJUSTED_AMENDED_SECTION11";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.HSN)) {
					workbook = aspSecHsnSummaryReportHandler
							.downloadGstr1HsnSummaryReport(setDataSecurity);
					fileName = "ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_" + criteria.getTaxPeriodTo()
							+ "_HSN_SUMMARY_SECTION12";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.DOC_ISSUED)) {
					workbook = aspSecDocReportHandler
							.downloadGstr1DocReport(setDataSecurity);
					fileName = "ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_" + criteria.getTaxPeriodTo()
							+ "_DOCUMENTS_ISSUED_SECTION13";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.SEZWP)) {
					workbook = aspSezWithTaxReportHandler
							.downloadSezWithTaxReport(setDataSecurity);
					fileName = "ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_" + criteria.getTaxPeriodTo()
							+ "_SEZ_WITH_TAX";

				}
				
				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.SEZWOP)) {
					workbook = aspSezWopReportHandler
							.downloadSezWopReport(setDataSecurity);
					fileName = "ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_" + criteria.getTaxPeriodTo()
							+ "_SEZ_WITHOUT_TAX";

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

