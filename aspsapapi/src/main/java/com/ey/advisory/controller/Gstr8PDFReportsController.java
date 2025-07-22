/**
 * 
 */
package com.ey.advisory.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

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
import com.ey.advisory.app.data.services.pdf.Gstr8PDFGenerationReportImpl;
import com.ey.advisory.app.util.Anx1CsvDownloadUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.ReportTypeConstants;
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
 * @author Ravindra V S
 *
 */
@RestController
@Slf4j
public class Gstr8PDFReportsController {

	@Autowired
	@Qualifier("Gstr7MultiplePdfZipReportService")
	Gstr7MultiplePdfZipReportService zipReportService;
	
	@Autowired
	@Qualifier("Anx1CsvDownloadUtil")
	private Anx1CsvDownloadUtil anx1CsvDownloadUtil;

	@Autowired
	@Qualifier("Gstr8PDFGenerationReportImpl")
	Gstr8PDFGenerationReportImpl gstr8ReportImpl;

	@Autowired
	FileStatusDownloadReportRepository downloadRepository;

	@Autowired
	AsyncJobsService asyncJobsService;
	
	@PostMapping(value = "/ui/Gstr8GstinLevelPdfReports", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void generateItc04PdfReport(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {
		
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject reqObj = JsonParser.parseString(jsonString)
				.getAsJsonObject().get("req").getAsJsonObject();

		Gstr2AProcessedRecordsReqDto reqDto = gson.fromJson(reqObj,
				Gstr2AProcessedRecordsReqDto.class);
		
		Gstr2AProcessedRecordsReqDto request = zipReportService.setGstr7DataSecuritySearchParams(reqDto);
		String retunPeriod = reqDto.getRetunPeriod();
		request.setRetunPeriod(retunPeriod);
		boolean isDigigst = reqDto.getIsDigigst();
		request.setIsDigigst(isDigigst);
		String gstn = reqDto.getGstin();
		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Inside controller with  Request : %s", request.toString());
			LOGGER.debug(msg);
		}
		String fileName = null;
			
			if(isDigigst){
				 fileName=String.format("GSTR8__%s_%s_DigiGST", gstn, retunPeriod);
			 }
			 else{
				 fileName=String.format("GSTR8__%s_%s_GSTN", gstn, retunPeriod);
			 }
			
			JasperPrint jasperPrint = gstr8ReportImpl
					.generatePdfGstr8Report(request,gstn);
			response.setContentType("application/x-download");

			response.setHeader("Content-Disposition",
					String.format("attachment; filename=" + fileName + ".pdf"));

			OutputStream out;
			try {
				out = response.getOutputStream();
				JasperExportManager.exportReportToPdfStream(jasperPrint, out);
			} catch (IOException | JRException ex) {
				LOGGER.error("Exception while generating Gstr8SummaryReport PDF ",
						ex);
			}
		
	}
	
	@PostMapping(value = "/ui/Gstr8EntityLevelPdfReports", produces = {
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
			entity.setDataType("GSTR8");
			entity.setReportCateg("GSTR8");
			entity.setReportType(ReportTypeConstants.GSTR8_PDF);
			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setReqPayload(gson.toJsonTree(req).toString());
			entity = downloadRepository.save(entity);

			Long id = entity.getId();
			String reportType = entity.getReportType();
			jobParams = new JsonObject();
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Inside controller with  Report id : %id", id.toString());
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
					JobConstants.GSTR8_BULK_PDF_DOWNLOAD, jobParams.toString(),
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
			LOGGER.error("Exception while generating Bulk Gstr8 PDF ", e);
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
