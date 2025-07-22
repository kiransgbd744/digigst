/**
 * 
 */
package com.ey.advisory.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.services.pdf.Gstr1SummaryMultiPDFService;
import com.ey.advisory.app.data.services.pdf.Gstr1SummaryPDFGenerationReport;
import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1SimpleDocGstnSummarySearchService;
import com.ey.advisory.app.util.Anx1CsvDownloadUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.ReportTypeConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Laxmi.Salukuti
 *
 */
@Slf4j
@RestController
public class Gstr1SummaryPDFGenerationController {

	@Autowired
	@Qualifier("Gstr1SummaryPDFGenerationReportImpl")
	Gstr1SummaryPDFGenerationReport gstr1SummaryPDFGenerationReport;

	@Autowired
	@Qualifier("Gstr1SimpleDocGstnSummarySearchService")
	Gstr1SimpleDocGstnSummarySearchService tableSearchService;

	@Autowired
	@Qualifier("Gstr1SummaryMultiPDFService")
	Gstr1SummaryMultiPDFService gstr1SummaryMultiPDFService;

	@Autowired
	CombineAndZipXlsxFiles combineAndZipXlsxFiles;

	@Autowired
	@Qualifier("Anx1CsvDownloadUtil")
	private Anx1CsvDownloadUtil anx1CsvDownloadUtil;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	@Autowired
	FileStatusDownloadReportRepository downloadRepository;

	@Autowired
	@Qualifier("AsyncGSTR1PDFDownloadServiceImpl")
	private AsyncReportDownloadService asyncReportDownloadService;

	@Autowired
	AsyncJobsService asyncJobsService;

	@PostMapping(value = "/ui/generateGstr1SummaryPdfReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void generateGstr1PdfReport(@RequestBody String jsonString,
			HttpServletResponse response) {

		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));

		// Gson gson = new Gson();
		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		// JsonObject obj = new
		// JsonParser().parse(jsonString).getAsJsonObject();
		String reqJson = requestObject.get("req").getAsJsonObject().toString();
		Gson gson = GsonUtil.newSAPGsonInstance();

		Annexure1SummaryReqDto annexure1SummaryRequest = gson
				.fromJson(reqJson.toString(), Annexure1SummaryReqDto.class);

		LOGGER.debug("GSTN Data Summary Execution BEGIN ");
		SearchResult<Gstr1CompleteSummaryDto> gstnSumryResult = (SearchResult<Gstr1CompleteSummaryDto>) tableSearchService
				.find(annexure1SummaryRequest, null,
						Gstr1CompleteSummaryDto.class);
		LOGGER.debug("OUTWARD GSTN Data Summary Execution END ");

		List<? extends Gstr1CompleteSummaryDto> gstnResult = gstnSumryResult
				.getResult();

		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(annexure1SummaryRequest);

		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();

		String gstin = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}
			}
		}
		String fileName = null;
		String sgstin=null;
		if (gstinList != null && !gstinList.isEmpty()) {
		sgstin = gstinList.get(0);
		}
		String taxPeriod = req.getTaxPeriod();
		boolean isDigigst = req.getIsDigigst();
		boolean isGstr1a = req.getIsGstr1a();
		JasperPrint jasperPrint = null;
		if (isDigigst) {
			if(Boolean.TRUE.equals(isGstr1a)){
				fileName = String.format("GSTR1A_%s_%s_DigiGST", sgstin, taxPeriod);
			} else {
				fileName = String.format("GSTR1_%s_%s_DigiGST", sgstin, taxPeriod);
			}
			
			jasperPrint = gstr1SummaryPDFGenerationReport
					.generatePdfGstr1Report(req, gstnResult);
		} else {
			if(Boolean.TRUE.equals(isGstr1a)){
				fileName = String.format("GSTR1A_%s_%s_GSTN", sgstin, taxPeriod);
			} else {
				fileName = String.format("GSTR1_%s_%s_GSTN", sgstin, taxPeriod);
			}
			
			jasperPrint = gstr1SummaryPDFGenerationReport
					.generatePdfGstr1ReportNew(req, gstnResult, sgstin);
		}

		
		response.setContentType("application/x-download");
		/*
		 * response.addHeader("Content-disposition",
		 * "attachment; filename=Gstr1SummaryReport.pdf");
		 */

		response.setHeader("Content-Disposition",
				String.format("attachment; filename=" + fileName + ".pdf"));

		OutputStream out;
		try {
			out = response.getOutputStream();
			JasperExportManager.exportReportToPdfStream(jasperPrint, out);
		} catch (IOException | JRException ex) {
			LOGGER.error("Exception while generating Gstr1SummaryReport PDF ",
					ex);
		}
	}

	// backup

	/*
	 * @PostMapping(value = "/ui/Gstr1EntityLevelSummaryPdfReports") public void
	 * gstr1EntityLevelSummaryPdfReport(@RequestBody String jsonString,
	 * HttpServletResponse response) throws Exception {
	 * 
	 * String fileName = null; InputStream inputStream = null;
	 * 
	 * JsonObject obj = new JsonParser().parse(jsonString).getAsJsonObject();
	 * String reqJson = obj.get("req").getAsJsonObject().toString(); Gson
	 * gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
	 * 
	 * Annexure1SummaryReqDto req = gsonEwb.fromJson(reqJson.toString(),
	 * Annexure1SummaryReqDto.class);
	 * 
	 * Annexure1SummaryReqDto annexure1SummaryRequest =
	 * processedRecordsCommonSecParam .setGstr1DataSecuritySearchParams(req);
	 * 
	 * if (LOGGER.isDebugEnabled()) { LOGGER.debug("Request Json {}", reqJson);
	 * }
	 * 
	 * String gstin = null; List<String> gstinList = null; Map<String,
	 * List<String>> dataSecAttrs = annexure1SummaryRequest .getDataSecAttrs();
	 * if (!dataSecAttrs.isEmpty()) { for (String key : dataSecAttrs.keySet()) {
	 * if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) { gstin = key; if
	 * (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty() &&
	 * dataSecAttrs.get(OnboardingConstant.GSTIN) .size() > 0) { gstinList =
	 * dataSecAttrs.get(OnboardingConstant.GSTIN); } } } }
	 * 
	 * LOGGER.debug("GSTN Data Summary Execution BEGIN ");
	 * SearchResult<Gstr1CompleteSummaryDto> gstnSumryResult =
	 * (SearchResult<Gstr1CompleteSummaryDto>) tableSearchService
	 * .find(annexure1SummaryRequest, null, Gstr1CompleteSummaryDto.class);
	 * LOGGER.debug("OUTWARD GSTN Data Summary Execution END ");
	 * 
	 * List<? extends Gstr1CompleteSummaryDto> gstnResult = gstnSumryResult
	 * .getResult(); try { File tempDir = createTempDir(); //for testing Long
	 * id=1010l; String zipFileName = gstr1SummaryMultiPDFService
	 * .generateGstr1SummaryPdfZip(tempDir, annexure1SummaryRequest, gstnResult,
	 * gstinList,id);
	 * 
	 * fileName = "GSTR1EntityLevelSummaryPDFReports"; LocalDateTime reqRTime =
	 * EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()); String recTime =
	 * reqRTime.toString(); String reqReceivedTime =
	 * recTime.replaceAll("[-T:.]", "");
	 * 
	 * if (tempDir.list().length > 0) { File zipFile = new File(tempDir,
	 * zipFileName); int read = 0; byte[] bytes = new byte[1024]; inputStream =
	 * FileUtils.openInputStream(zipFile);
	 * response.setHeader("Content-Disposition",
	 * String.format("attachment; filename=" + fileName + "_" + reqReceivedTime
	 * + ".zip"));
	 * 
	 * OutputStream outputStream = response.getOutputStream(); while ((read =
	 * inputStream.read(bytes)) != -1) { outputStream.write(bytes, 0, read); }
	 * response.getOutputStream().flush(); } inputStream.close();
	 * anx1CsvDownloadUtil.deleteTemporaryDirectory(tempDir); } catch
	 * (IOException | JRException ex) {
	 * LOGGER.error("Exception while generating Einvoice PDF ", ex); } }
	 */

	@PostMapping(value = "/ui/Gstr1EntityLevelSummaryPdfReports")
	public ResponseEntity<String> gstr1EntityLevelSummaryPdfReportAsync(
			@RequestBody String jsonString, HttpServletResponse response) {
		JsonObject jobParams = new JsonObject();
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			JsonObject obj = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Annexure1SummaryReqDto req = gson.fromJson(reqJson.toString(),
					Annexure1SummaryReqDto.class);
			
			boolean isGstr1a = false;
			Boolean isGstr1aObj = req.getIsGstr1a();
			if (isGstr1aObj) {
			    isGstr1a = isGstr1aObj;
			    req.setReturnType("GSTR1A");
			}
			
			List<String> gstinList = null;
			Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();
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
			entity.setTaxPeriod(req.getTaxPeriod());
			entity.setCreatedBy(userName);
			entity.setGstins(GenUtil
					.convertStringToClob(convertToQueryFormat(gstinList)));
			if(Boolean.TRUE.equals(isGstr1a)){
				entity.setReportCateg("GSTR 1A");
				entity.setReportType(ReportTypeConstants.GSTR1A_PDF);
				entity.setDataType("Outward_1A");
			} else {
				entity.setReportCateg("GSTR 1");
				entity.setReportType(ReportTypeConstants.GSTR1_PDF);
				entity.setDataType("Outward");
			}
			
			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setReqPayload(gson.toJsonTree(req).toString());
			entity = downloadRepository.save(entity);

			Long id = entity.getId();
			String reportType = entity.getReportType();

			jobParams.addProperty("id", id);
			jobParams.addProperty("reportType", reportType);

//			asyncReportDownloadService.generateReports(id);
			 asyncJobsService.createJob(TenantContext.getTenantId(),
			 JobConstants.GSTR1_BULK_PDF_DOWNLOAD, jobParams.toString(),
			 userName, 1L, null, null);

			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String errMsg = "Unexpected erroroccured in GSTR1 PDF Async Report";
			APIRespDto dto = new APIRespDto("Failed", errMsg + e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", respBody);
			LOGGER.error(errMsg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}
	}
	
	private String convertToQueryFormat(List<String> gstinset) {

		List<String> list = new ArrayList<String>();
		list.addAll(gstinset);

		String queryData = null;
		if (list == null || list.isEmpty())
			return null;

		queryData = list.get(0);
		for (int i = 1; i < list.size(); i++) {
			queryData += "," + list.get(i);
		}

		return queryData;
	}
}
