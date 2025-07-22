/**
 * 
 */
package com.ey.advisory.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.ey.advisory.app.data.services.pdf.Gstr7MultiplePdfZipReportService;
import com.ey.advisory.app.data.services.pdf.Gstr7PDFGenerationReportImpl;
import com.ey.advisory.app.util.Anx1CsvDownloadUtil;
import com.ey.advisory.app.util.OnboardingConstant;
//import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.ReportTypeConstants;
//import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.dto.Gstr2AProcessedRecordsReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Balakrishna.S
 *
 */
@RestController
@Slf4j
public class Gstr7PDFReportsController {

	@Autowired
	@Qualifier("Gstr7MultiplePdfZipReportService")
	Gstr7MultiplePdfZipReportService zipReportService;
	
	@Autowired
	@Qualifier("Anx1CsvDownloadUtil")
	private Anx1CsvDownloadUtil anx1CsvDownloadUtil;

	@Autowired
	@Qualifier("Gstr7PDFGenerationReportImpl")
	Gstr7PDFGenerationReportImpl gstr7ReportImpl;

	@Autowired
	FileStatusDownloadReportRepository downloadRepository;

	@Autowired
	AsyncJobsService asyncJobsService;
	
	private static final DateTimeFormatter formatter1 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");

	private static final DateTimeFormatter formatter2 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy ");
	//original
/*	@PostMapping(value = "/ui/gstr7PdfReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void generateItc04PdfReport(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		String fileName = null;
		InputStream inputStream = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject reqObj = JsonParser.parseString(jsonString)
				.getAsJsonObject().get("req").getAsJsonObject();

		Gstr2AProcessedRecordsReqDto reqDto = gson.fromJson(reqObj,
				Gstr2AProcessedRecordsReqDto.class);

		try {
			File tempDir = createTempDir();

			
			String zipFileName = zipReportService
					.generateGstr7PdfZip(tempDir, reqDto);
			
			fileName = "GSTR7PDFReports";
		//	LocalDateTime reqRTime = LocalDateTime.now();
			
			String recTime = getStandardTime(LocalDateTime.now());

			
			
		//	String recTime = reqRTime.toString();
			String reqReceivedTime = recTime.replaceAll("[-T:.]", "");

			if (tempDir.list().length > 0) {
				File zipFile = new File(tempDir, zipFileName);
				int read = 0;
				byte[] bytes = new byte[1024];
				inputStream = FileUtils.openInputStream(zipFile);
				response.setHeader("Content-Disposition",
						String.format("attachment; filename=" + fileName + "_"
								+ reqReceivedTime + ".zip"));

				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
				response.getOutputStream().flush();
			}
			inputStream.close();
			anx1CsvDownloadUtil.deleteTemporaryDirectory(tempDir);
		} catch (IOException | JRException ex) {
			LOGGER.error("Exception while generating Einvoice PDF ", ex);
		}
	}*/
	
	
	
	@PostMapping(value = "/ui/gstr7PdfReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void generateItc04PdfReport(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		//String fileName = null;
		InputStream inputStream = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject reqObj = JsonParser.parseString(jsonString)
				.getAsJsonObject().get("req").getAsJsonObject();

		Gstr2AProcessedRecordsReqDto reqDto = gson.fromJson(reqObj,
				Gstr2AProcessedRecordsReqDto.class);
		
		Gstr2AProcessedRecordsReqDto request = zipReportService.setGstr7DataSecuritySearchParams(reqDto);
		String retunPeriod = reqDto.getRetunPeriod();
		boolean isDigigst = reqDto.getIsDigigst();
	//	String sgstin = null;
		List<String> gstinList = null;
		
		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();
		
		gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
		String fileName = null;
		for (String gstn : gstinList) {
			
			if(isDigigst){
				 fileName=String.format("GSTR7__%s_%s_DigiGST", gstn, retunPeriod);
			 }
			 else{
				 fileName=String.format("GSTR7__%s_%s_GSTN", gstn, retunPeriod);
			 }
			
			JasperPrint jasperPrint = gstr7ReportImpl
					.generatePdfGstr7Report(request,gstn);
			response.setContentType("application/x-download");
			/*response.addHeader("Content-disposition",
					"attachment; filename=Gstr1SummaryReport.pdf");*/

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
		
	}
	
	@PostMapping(value = "/ui/gstr7PdfReportAsync", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> generateGstr6PdfReportAsync(
			@RequestBody String jsonParam, HttpServletResponse response)
			throws Exception {

		JsonObject jobParams = new JsonObject();
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonObject errorResp = new JsonObject();

		try {
			JsonObject obj = JsonParser.parseString(jsonParam)
					.getAsJsonObject();
			JsonObject reqJson = obj.getAsJsonObject("req");
			Annexure1SummaryReqDto req = gson.fromJson(reqJson,
					Annexure1SummaryReqDto.class);
			String groupCode = TenantContext.getTenantId();
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();
			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
			entity.setTaxPeriod(req.getTaxPeriod());
			entity.setCreatedBy(userName);
			entity.setDataType("GSTR 7");
			entity.setReportCateg("GSTR 7");
			entity.setReportType(ReportTypeConstants.GSTR7_PDF);
			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setReqPayload(gson.toJsonTree(req).toString());
			entity = downloadRepository.save(entity);

			Long id = entity.getId();
			String reportType = entity.getReportType();
			jobParams = new JsonObject();
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Inside controller with  Report id : %id", id);
				LOGGER.debug(msg);
			}
			jobParams.addProperty("id", id);
			jobParams.addProperty("reportType", reportType);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Inside controller with  Report id and report type : %id and  %reportType",
						id,reportType);
				LOGGER.debug(msg);
			}

			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR7_BULK_PDF_DOWNLOAD, jobParams.toString(),
					userName, 1L, null, null);

			resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Inside controller  returning response with  Report id and report type : %id and  %reportType",
						id,reportType);
				LOGGER.debug(msg);
			}

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception e) {
			LOGGER.error("Exception while generating Bulk Gstr7 PDF ", e);
			JsonArray respBody = new JsonArray();
			JsonObject json = new JsonObject();
			json.addProperty("msg", e.getMessage());
			respBody.add(json);
			LOGGER.error("Message", e);
			errorResp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			errorResp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(errorResp.toString(), HttpStatus.OK);
		}

	}
	
}
