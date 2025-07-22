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
import com.ey.advisory.app.services.reports.Gstr1AdvAdjAmenSecReportHandler;
import com.ey.advisory.app.services.reports.Gstr1AdvAdjSecReportHandler;
import com.ey.advisory.app.services.reports.Gstr1AdvRecAmenSecReportHandler;
import com.ey.advisory.app.services.reports.Gstr1AdvRecSecReportHandler;
import com.ey.advisory.app.services.reports.Gstr1B2BASectionReportHandler;
import com.ey.advisory.app.services.reports.Gstr1B2BSectionReportHandler;
import com.ey.advisory.app.services.reports.Gstr1B2CLASectionReportHandler;
import com.ey.advisory.app.services.reports.Gstr1B2CLSectionReportHandler;
import com.ey.advisory.app.services.reports.Gstr1B2CSASectionReportHandler;
import com.ey.advisory.app.services.reports.Gstr1B2CSSectionReportHandler;
import com.ey.advisory.app.services.reports.Gstr1CDNRASectionReportHandler;
import com.ey.advisory.app.services.reports.Gstr1CDNRAndB2CSDownloadEntitySave;
import com.ey.advisory.app.services.reports.Gstr1CDNRSectionReportHandler;
import com.ey.advisory.app.services.reports.Gstr1CDNURASectionReportHandler;
import com.ey.advisory.app.services.reports.Gstr1CDNURSectionReportHandler;
import com.ey.advisory.app.services.reports.Gstr1EXPASectionReportHandler;
import com.ey.advisory.app.services.reports.Gstr1EXPSectionReportHandler;
import com.ey.advisory.app.services.reports.Gstr1NilSectionReportHandler;
import com.ey.advisory.app.services.reports.Gstr1SecDocReportHandler;
import com.ey.advisory.app.services.reports.Gstr1SecHsnRateReportHandler;
import com.ey.advisory.app.services.reports.Gstr1SecHsnSummaryReportHandler;
import com.ey.advisory.app.services.reports.Gstr1SezWithTaxReportHandler;
import com.ey.advisory.app.services.reports.Gstr1SezWopReportHandler;
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

@RestController
public class Gstr1SectionWiseReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1SectionWiseReportsController.class);

	@Autowired
	@Qualifier("Gstr1B2BSectionReportHandler")
	private Gstr1B2BSectionReportHandler gstr1B2BSectionReportHandler;

	@Autowired
	@Qualifier("Gstr1B2BASectionReportHandler")
	private Gstr1B2BASectionReportHandler gstr1B2BASectionReportHandler;

	@Autowired
	@Qualifier("Gstr1B2CLSectionReportHandler")
	private Gstr1B2CLSectionReportHandler gstr1B2CLSectionReportHandler;

	@Autowired
	@Qualifier("Gstr1B2CLASectionReportHandler")
	private Gstr1B2CLASectionReportHandler gstr1B2CLASectionReportHandler;

	@Autowired
	@Qualifier("Gstr1EXPSectionReportHandler")
	private Gstr1EXPSectionReportHandler gstr1EXPSectionReportHandler;

	@Autowired
	@Qualifier("Gstr1EXPASectionReportHandler")
	private Gstr1EXPASectionReportHandler gstr1EXPASectionReportHandler;

	@Autowired
	@Qualifier("Gstr1CDNRSectionReportHandler")
	private Gstr1CDNRSectionReportHandler gstr1CDNRSectionReportHandler;

	@Autowired
	@Qualifier("Gstr1CDNRASectionReportHandler")
	private Gstr1CDNRASectionReportHandler gstr1CDNRASectionReportHandler;

	@Autowired
	@Qualifier("Gstr1CDNURSectionReportHandler")
	private Gstr1CDNURSectionReportHandler gstr1CDNURSectionReportHandler;

	@Autowired
	@Qualifier("Gstr1CDNURASectionReportHandler")
	private Gstr1CDNURASectionReportHandler gstr1CDNURASectionReportHandler;

	@Autowired
	@Qualifier("Gstr1B2CSSectionReportHandler")
	private Gstr1B2CSSectionReportHandler gstr1B2CSSectionReportHandler;

	@Autowired
	@Qualifier("Gstr1B2CSASectionReportHandler")
	private Gstr1B2CSASectionReportHandler gstr1B2CSASectionReportHandler;

	@Autowired
	@Qualifier("Gstr1NilSectionReportHandler")
	private Gstr1NilSectionReportHandler gstr1NilSectionReportHandler;

	@Autowired
	@Qualifier("Gstr1AdvRecSecReportHandler")
	private Gstr1AdvRecSecReportHandler gstr1AdvRecSecReportHandler;

	@Autowired
	@Qualifier("Gstr1AdvRecAmenSecReportHandler")
	private Gstr1AdvRecAmenSecReportHandler gstr1AdvRecAmenSecReportHandler;

	@Autowired
	@Qualifier("Gstr1AdvAdjSecReportHandler")
	private Gstr1AdvAdjSecReportHandler gstr1AdvAdjSecReportHandler;

	@Autowired
	@Qualifier("Gstr1AdvAdjAmenSecReportHandler")
	private Gstr1AdvAdjAmenSecReportHandler gstr1AdvAdjAmenSecReportHandler;

	@Autowired
	@Qualifier("Gstr1SecHsnSummaryReportHandler")
	private Gstr1SecHsnSummaryReportHandler gstr1SecHsnSummaryReportHandler;
	
	@Autowired
	@Qualifier("Gstr1SecHsnRateReportHandler")
	private Gstr1SecHsnRateReportHandler gstr1SecHsnRateReportHandler;

	@Autowired
	@Qualifier("Gstr1SecDocReportHandler")
	private Gstr1SecDocReportHandler gstr1SecDocReportHandler;

	@Autowired
	@Qualifier("Gstr1SezWithTaxReportHandler")
	private Gstr1SezWithTaxReportHandler gstr1SezWithTaxReportHandler;
	
	@Autowired
	@Qualifier("Gstr1SezWopReportHandler")
	private Gstr1SezWopReportHandler gstr1SezWopReportHandler;

	@Autowired
	@Qualifier("BasicCommonSecParamRSReports")
	BasicCommonSecParamRSReports basicCommonSecParamRSReports;
	
	@Autowired
	@Qualifier("GstnApi")
	GstnApi gstnApi;
	
	@Autowired
	@Qualifier("Gstr1CDNRAndB2CSDownloadEntitySave")
	private Gstr1CDNRAndB2CSDownloadEntitySave downloadEntitySave;


	@RequestMapping(value = "/ui/downloadGstr1SecReports", method = RequestMethod.POST, produces = {
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
					downloadEntitySave.saveDownloadEntity(jsonString,criteria);

					/*workbook = gstr1B2BSectionReportHandler
							.downloadRSProcessedReport(setDataSecurity,null);
					fileName = "GSTR-1_" + finalGstinString + "_"
							+ criteria.getTaxperiod()
							+ "_B2B_SECTION4_SECTION6B_SECTION6C";*/

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.B2BA)) {
					workbook = gstr1B2BASectionReportHandler
							.downloadRSProcessedReport(setDataSecurity,null);
					fileName = "GSTR-1_" + finalGstinString + "_"
							+ criteria.getTaxperiod() + "_B2BA_SECTION9A";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.B2CL)) {
					workbook = gstr1B2CLSectionReportHandler
							.downloadRSProcessedReport(setDataSecurity,null);
					fileName = "GSTR-1_" + finalGstinString + "_"
							+ criteria.getTaxperiod() + "_B2CL_SECTION5";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.B2CLA)) {
					workbook = gstr1B2CLASectionReportHandler
							.downloadRSProcessedReport(setDataSecurity,null);
					fileName = "GSTR-1_" + finalGstinString + "_"
							+ criteria.getTaxperiod() + "_B2CLA_SECTION9A";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.EXPORTS)) {
					workbook = gstr1EXPSectionReportHandler
							.downloadRSProcessedReport(setDataSecurity,null);
					fileName = "GSTR-1_" + finalGstinString + "_"
							+ criteria.getTaxperiod() + "_EXPORTS_SECTION6A";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.EXPORTA)) {
					workbook = gstr1EXPASectionReportHandler
							.downloadRSProcessedReport(setDataSecurity,null);
					fileName = "GSTR-1_" + finalGstinString + "_"
							+ criteria.getTaxperiod()
							+ "_EXPORTS_Amended_SECTION9A";

				}
				
				//making async CDNR
				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.CDNR)) {
					
					downloadEntitySave.saveDownloadEntity(jsonString,criteria);
					
					
					/*workbook = gstr1CDNRSectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "GSTR-1_" + finalGstinString + "_"
							+ criteria.getTaxperiod() + "_CDNR_SECTION9B";*/

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.CDNRA)) {
					workbook = gstr1CDNRASectionReportHandler
							.downloadRSProcessedReport(setDataSecurity,null);
					fileName = "GSTR-1_" + finalGstinString + "_"
							+ criteria.getTaxperiod() + "_CDNRA_SECTION9C";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.CDNUR)) {
					workbook = gstr1CDNURSectionReportHandler
							.downloadRSProcessedReport(setDataSecurity,null);
					fileName = "GSTR-1_" + finalGstinString + "_"
							+ criteria.getTaxperiod() + "_CDNUR_SECTION9B";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.CDNURA)) {
					workbook = gstr1CDNURASectionReportHandler
							.downloadRSProcessedReport(setDataSecurity,null);
					fileName = "GSTR-1_" + finalGstinString + "_"
							+ criteria.getTaxperiod() + "_CDNURA_SECTION9C";

				}

				//making async B2CS
				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.B2CS1)) {
					downloadEntitySave.saveDownloadEntity(jsonString,criteria);
					/*workbook = gstr1B2CSSectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "GSTR-1_" + finalGstinString + "_"
							+ criteria.getTaxperiod() + "_B2CS_SECTION7";*/

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.B2CSA)) {
					workbook = gstr1B2CSASectionReportHandler
							.downloadRSProcessedReport(setDataSecurity,null);
					fileName = "GSTR-1_" + finalGstinString + "_"
							+ criteria.getTaxperiod() + "_B2CSA_SECTION10";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.NILEXTNON)) {
					workbook = gstr1NilSectionReportHandler
							.downloadRSProcessedReport(setDataSecurity);
					fileName = "GSTR-1_" + finalGstinString + "_"
							+ criteria.getTaxperiod()
							+ "_NIL_EXEMPT_NON_GST_SUPPLIES";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.ADVREC)) {
					workbook = gstr1AdvRecSecReportHandler
							.getGstr1AdvRecSavableReports(setDataSecurity);
					fileName = "GSTR-1_" + finalGstinString + "_"
							+ criteria.getTaxperiod()
							+ "_ADV_RECEIVED_SECTION11";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.ADVADJ)) {
					workbook = gstr1AdvAdjSecReportHandler
							.getGstr1AdjSavableReports(setDataSecurity);
					fileName = "GSTR-1_" + finalGstinString + "_"
							+ criteria.getTaxperiod()
							+ "_ADV_ADJUSTED_SECTION11";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.ADVRECA)) {
					workbook = gstr1AdvRecAmenSecReportHandler
							.getGstr1AdvRecSavableReports(setDataSecurity);
					fileName = "GSTR-1_" + finalGstinString + "_"
							+ criteria.getTaxperiod()
							+ "_ADV_RECEIVED_AMENDED_SECTION11";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.ADVADJA)) {
					workbook = gstr1AdvAdjAmenSecReportHandler
							.getGstr1AdjSavableReports(setDataSecurity);
					fileName = "GSTR-1_" + finalGstinString + "_"
							+ criteria.getTaxperiod()
							+ "_ADV_ADJUSTED_AMENDED_SECTION11";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.HSN)) {

					Boolean rateIncludedInHsn = gstnApi
							.isRateIncludedInHsn(criteria.getTaxperiod());
					if (rateIncludedInHsn) {
						workbook = gstr1SecHsnRateReportHandler
								.downloadGstr1HsnRateReport(setDataSecurity);
						fileName = "GSTR-1_" + finalGstinString + "_"
								+ criteria.getTaxperiod()
								+ "_HSN_SUMMARY_SECTION12";
					} else {
						workbook = gstr1SecHsnSummaryReportHandler
								.downloadGstr1HsnSummaryReport(setDataSecurity);
						fileName = "GSTR-1_" + finalGstinString + "_"
								+ criteria.getTaxperiod()
								+ "_HSN_SUMMARY_SECTION12";
					}
				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.DOCISSUED)) {
					workbook = gstr1SecDocReportHandler
							.downloadGstr1DocReport(setDataSecurity);
					fileName = "GSTR-1_" + finalGstinString + "_"
							+ criteria.getTaxperiod()
							+ "_DOCUMENTS_ISSUED_SECTION13";

				}

				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.SEZWP)) {
					workbook = gstr1SezWithTaxReportHandler
							.downloadSezWithTaxReport(setDataSecurity);
					fileName = "GSTR-1_" + finalGstinString + "_"
							+ criteria.getTaxperiod() + "_SEZ_WITH_TAX";

				}
				
				if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
						.equalsIgnoreCase(DownloadReportsConstant.SEZWOP)) {
					workbook = gstr1SezWopReportHandler
							.downloadSezWopReport(setDataSecurity);
					fileName = "GSTR-1_" + finalGstinString + "_"
							+ criteria.getTaxperiod() + "_SEZ_WITHOUT_TAX";

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
