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
import com.ey.advisory.app.services.reports.gstr1a.Gstr1AAspAdvAdjAmenSecReportHandler;
import com.ey.advisory.app.services.reports.gstr1a.Gstr1AAspAdvAdjSecReportHandler;
import com.ey.advisory.app.services.reports.gstr1a.Gstr1AAspAdvRecAmenSecReportHandler;
import com.ey.advisory.app.services.reports.gstr1a.Gstr1AAspAdvRecSecReportHandler;
import com.ey.advisory.app.services.reports.gstr1a.Gstr1AAspB2BASectionReportHandler;
import com.ey.advisory.app.services.reports.gstr1a.Gstr1AAspB2BSectionReportHandler;
import com.ey.advisory.app.services.reports.gstr1a.Gstr1AAspB2CLASectionReportHandler;
import com.ey.advisory.app.services.reports.gstr1a.Gstr1AAspB2CLSectionReportHandler;
import com.ey.advisory.app.services.reports.gstr1a.Gstr1AAspB2CSASectionReportHandler;
import com.ey.advisory.app.services.reports.gstr1a.Gstr1AAspB2CSSectionReportHandler;
import com.ey.advisory.app.services.reports.gstr1a.Gstr1AAspCDNRASectionReportHandler;
import com.ey.advisory.app.services.reports.gstr1a.Gstr1AAspCDNRSectionReportHandler;
import com.ey.advisory.app.services.reports.gstr1a.Gstr1AAspCDNURASectionReportHandler;
import com.ey.advisory.app.services.reports.gstr1a.Gstr1AAspCDNURSectionReportHandler;
import com.ey.advisory.app.services.reports.gstr1a.Gstr1AAspEXPASectionReportHandler;
import com.ey.advisory.app.services.reports.gstr1a.Gstr1AAspEXPSectionReportHandler;
import com.ey.advisory.app.services.reports.gstr1a.Gstr1AAspNilSectionReportHandler;
import com.ey.advisory.app.services.reports.gstr1a.Gstr1AAspSecDocReportHandler;
import com.ey.advisory.app.services.reports.gstr1a.Gstr1AAspSecHsnSummaryReportHandler;
import com.ey.advisory.app.services.reports.gstr1a.Gstr1AAspSezWithTaxReportHandler;
import com.ey.advisory.app.services.reports.gstr1a.Gstr1AAspSezWopReportHandler;
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
public class Gstr1AAspProcessSubmitSectionwiseReportsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(
			Gstr1AAspProcessSubmitSectionwiseReportsController.class);

	@Autowired
	@Qualifier("Gstr1AAspB2BSectionReportHandler")
	private Gstr1AAspB2BSectionReportHandler aspB2BSectionReportHandler;

	@Autowired
	@Qualifier("Gstr1AAspB2BASectionReportHandler")
	private Gstr1AAspB2BASectionReportHandler aspB2BASectionReportHandler;

	@Autowired
	@Qualifier("Gstr1AAspB2CLSectionReportHandler")
	private Gstr1AAspB2CLSectionReportHandler aspB2CLSectionReportHandler;

	@Autowired
	@Qualifier("Gstr1AAspB2CLASectionReportHandler")
	private Gstr1AAspB2CLASectionReportHandler aspB2CLASectionReportHandler;

	@Autowired
	@Qualifier("Gstr1AAspEXPSectionReportHandler")
	private Gstr1AAspEXPSectionReportHandler aspEXPSectionReportHandler;

	@Autowired
	@Qualifier("Gstr1AAspEXPASectionReportHandler")
	private Gstr1AAspEXPASectionReportHandler aspEXPASectionReportHandler;

	@Autowired
	@Qualifier("Gstr1AAspCDNRSectionReportHandler")
	private Gstr1AAspCDNRSectionReportHandler aspCDNRSectionReportHandler;

	@Autowired
	@Qualifier("Gstr1AAspCDNRASectionReportHandler")
	private Gstr1AAspCDNRASectionReportHandler aspCDNRASectionReportHandler;

	@Autowired
	@Qualifier("Gstr1AAspCDNURSectionReportHandler")
	private Gstr1AAspCDNURSectionReportHandler aspCDNURSectionReportHandler;

	@Autowired
	@Qualifier("Gstr1AAspCDNURASectionReportHandler")
	private Gstr1AAspCDNURASectionReportHandler aspCDNURASectionReportHandler;

	@Autowired
	@Qualifier("Gstr1AAspB2CSSectionReportHandler")
	private Gstr1AAspB2CSSectionReportHandler aspB2CSSectionReportHandler;

	@Autowired
	@Qualifier("Gstr1AAspB2CSASectionReportHandler")
	private Gstr1AAspB2CSASectionReportHandler aspB2CSASectionReportHandler;

	@Autowired
	@Qualifier("Gstr1AAspNilSectionReportHandler")
	private Gstr1AAspNilSectionReportHandler aspNilSectionReportHandler;

	@Autowired
	@Qualifier("Gstr1AAspAdvRecSecReportHandler")
	private Gstr1AAspAdvRecSecReportHandler aspAdvRecSecReportHandler;

	@Autowired
	@Qualifier("Gstr1AAspAdvRecAmenSecReportHandler")
	private Gstr1AAspAdvRecAmenSecReportHandler aspAdvRecAmenSecReportHandler;

	@Autowired
	@Qualifier("Gstr1AAspAdvAdjSecReportHandler")
	private Gstr1AAspAdvAdjSecReportHandler aspAdvAdjSecReportHandler;

	@Autowired
	@Qualifier("Gstr1AAspAdvAdjAmenSecReportHandler")
	private Gstr1AAspAdvAdjAmenSecReportHandler aspAdvAdjAmenSecReportHandler;

	@Autowired
	@Qualifier("Gstr1AAspSecHsnSummaryReportHandler")
	private Gstr1AAspSecHsnSummaryReportHandler aspSecHsnSummaryReportHandler;

	@Autowired
	@Qualifier("Gstr1AAspSecDocReportHandler")
	private Gstr1AAspSecDocReportHandler aspSecDocReportHandler;

	@Autowired
	@Qualifier("Gstr1AAspSezWithTaxReportHandler")
	private Gstr1AAspSezWithTaxReportHandler aspSezWithTaxReportHandler;

	@Autowired
	@Qualifier("Gstr1AAspSezWopReportHandler")
	private Gstr1AAspSezWopReportHandler aspSezWopReportHandler;

	@Autowired
	@Qualifier("BasicCommonSecParamRSReports")
	BasicCommonSecParamRSReports basicCommonSecParamRSReports;

	@RequestMapping(value = "/ui/downloadGstr1aAspvsSubmittedSecReports", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadreviewSummReport(@RequestBody String jsonString,
			HttpServletResponse response) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
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
					fileName = "GSTR1A_ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_"
							+ criteria.getTaxPeriodTo()
							+ "_B2B_SECTION4_SECTION6B_SECTION6C";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.B2BA)) {
					workbook = aspB2BASectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "GSTR1A_ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_"
							+ criteria.getTaxPeriodTo() + "_B2BA_SECTION9A";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.B2CL)) {
					workbook = aspB2CLSectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "GSTR1A_ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_"
							+ criteria.getTaxPeriodTo() + "_B2CL_SECTION5";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.B2CLA)) {
					workbook = aspB2CLASectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "GSTR1A_ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_"
							+ criteria.getTaxPeriodTo() + "_B2CLA_SECTION9A";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.EXPORTS)) {
					workbook = aspEXPSectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "GSTR-1A_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_"
							+ criteria.getTaxPeriodTo() + "_EXPORTS_SECTION6A";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.EXPORTA)) {
					workbook = aspEXPASectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "GSTR1A_ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_"
							+ criteria.getTaxPeriodTo()
							+ "_EXPORTS_Amended_SECTION9A";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.CDNR)) {
					workbook = aspCDNRSectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "GSTR1A_ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_"
							+ criteria.getTaxPeriodTo() + "_CDNR_SECTION9B";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.CDNRA)) {
					workbook = aspCDNRASectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "GSTR1A_ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_"
							+ criteria.getTaxPeriodTo() + "_CDNRA_SECTION9C";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.CDNUR)) {
					workbook = aspCDNURSectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "GSTR1A_ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_"
							+ criteria.getTaxPeriodTo() + "_CDNUR_SECTION9B";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.CDNURA)) {
					workbook = aspCDNURASectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "GSTR1A_ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_"
							+ criteria.getTaxPeriodTo() + "_CDNURA_SECTION9C";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.B2CS1)) {
					workbook = aspB2CSSectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "GSTR1A_ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_"
							+ criteria.getTaxPeriodTo() + "_B2CS_SECTION7";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.B2CSA)) {
					workbook = aspB2CSASectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "GSTR1A_ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_"
							+ criteria.getTaxPeriodTo() + "_B2CSA_SECTION10";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.NILEXTNON)) {
					workbook = aspNilSectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "GSTR1A_ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_"
							+ criteria.getTaxPeriodTo()
							+ "_NIL_EXEMPT_NON_GST_SUPPLIES";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.ADVREC)) {
					workbook = aspAdvRecSecReportHandler
							.getGstr1AdvRecSavableReports(setDataSecurity);
					fileName = "GSTR1A_ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_"
							+ criteria.getTaxPeriodTo()
							+ "_ADV_RECEIVED_SECTION11";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.ADVADJ)) {
					workbook = aspAdvAdjSecReportHandler
							.getGstr1AdjSavableReports(setDataSecurity);
					fileName = "GSTR1A_ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_"
							+ criteria.getTaxPeriodTo()
							+ "_ADV_ADJUSTED_SECTION11";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.ADVRECA)) {
					workbook = aspAdvRecAmenSecReportHandler
							.getGstr1AdvRecSavableReports(setDataSecurity);
					fileName = "GSTR1A_ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_"
							+ criteria.getTaxPeriodTo()
							+ "_ADV_RECEIVED_AMENDED_SECTION11";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.ADVADJA)) {
					workbook = aspAdvAdjAmenSecReportHandler
							.getGstr1AdjSavableReports(setDataSecurity);
					fileName = "GSTR1A_ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_"
							+ criteria.getTaxPeriodTo()
							+ "_ADV_ADJUSTED_AMENDED_SECTION11";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.HSN)) {
					workbook = aspSecHsnSummaryReportHandler
							.downloadGstr1HsnSummaryReport(setDataSecurity);
					fileName = "GSTR1A_ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_"
							+ criteria.getTaxPeriodTo()
							+ "_HSN_SUMMARY_SECTION12";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.DOC_ISSUED)) {
					workbook = aspSecDocReportHandler
							.downloadGstr1DocReport(setDataSecurity);
					fileName = "GSTR1A_ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_"
							+ criteria.getTaxPeriodTo()
							+ "_DOCUMENTS_ISSUED_SECTION13";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.SEZWP)) {
					workbook = aspSezWithTaxReportHandler
							.downloadSezWithTaxReport(setDataSecurity);
					fileName = "GSTR1A_ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_"
							+ criteria.getTaxPeriodTo() + "_SEZ_WITH_TAX";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.SEZWOP)) {
					workbook = aspSezWopReportHandler
							.downloadSezWopReport(setDataSecurity);
					fileName = "GSTR1A_ASP_" + finalGstinString + "_"
							+ criteria.getTaxPeriodFrom() + "_"
							+ criteria.getTaxPeriodTo() + "_SEZ_WITHOUT_TAX";

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
