package com.ey.advisory.controllers.gstr3b;

import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
import com.ey.advisory.app.data.services.pdf.Gstr3BSummaryPDFGenerationReport;
import com.ey.advisory.app.services.search.filestatussearch.AsyncReportDownloadServiceGstr3b;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.ReportTypeConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.dto.Gstr3bGetInvoicesReqDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Jithendra Kumar B
 *
 */
@Slf4j
@RestController
public class Gstr3BSummeryPDFController {

	@Autowired
	@Qualifier("Gstr3BSummaryPDFGenerationReportImpl")
	Gstr3BSummaryPDFGenerationReport gstr3BSummaryPDFGenerationReport;

	@Autowired
	FileStatusDownloadReportRepository downloadRepository;

	@Autowired
	@Qualifier("AsyncGSTR3bPDFDownloadServiceImpl")
	AsyncReportDownloadServiceGstr3b asyncReportDownloadServiceGstr3b;

	@Autowired
	AsyncJobsService asyncJobsService;

	@PostMapping(value = "/ui/generateGstr3BSummaryPDFReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> generateGstr3BSummeryPdf(
			@RequestBody String jsonParam, HttpServletResponse response) {
		JsonObject errorResp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {

			JsonObject requestObject = (new JsonParser()).parse(jsonParam)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			Gstr3bGetInvoicesReqDto dto = gson.fromJson(reqObject,
					Gstr3bGetInvoicesReqDto.class);
			String gstin = dto.getGstin();
			String taxPeriod = dto.getTaxPeriod();
			boolean isDigigst = dto.getIsDigigst();
			String isVerified = dto.getIsVerified();
			if (Strings.isNullOrEmpty(gstin)
					|| Strings.isNullOrEmpty(taxPeriod)) {
				String msg = "Gstin and taxPeriod is mandatory to generate PDF Report";
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			LOGGER.debug("Gstr3B Generation process intialised {}", gstin);
			JasperPrint jasperPrint = gstr3BSummaryPDFGenerationReport
					.generateGstr3BSummaryPdfReport(gstin, taxPeriod, isDigigst,
							isVerified);
			APIRespDto apiResp = null;

			if (jasperPrint == null) {
				LOGGER.error(
						"No Summary Data for Selected gstin and taxPeriod");
				JsonObject resp = new JsonObject();
				String msg = "No Summary Data for Selected gstin and taxPeriod";
				JsonArray respBody = new JsonArray();
				JsonObject json = new JsonObject();
				apiResp = APIRespDto.creatErrorResp();
				json.addProperty("gstin", gstin);
				json.addProperty("msg", msg);
				respBody.add(json);
				resp.add("hdr", gson.toJsonTree(apiResp));
				resp.add("resp", gson.toJsonTree(respBody));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

			}

			DateTimeFormatter dtf = DateTimeFormatter
					.ofPattern("yyyyMMddHHmmss");
			String timeMilli = dtf.format(
					EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));

			response.setContentType("application/x-download");
			if (isDigigst) {
				response.addHeader("Content-disposition",
						"attachment; filename=" + "GSTR3B_" + gstin + "_"
								+ taxPeriod + "_" + "DigiGST" + ".pdf");
			} else {
				response.addHeader("Content-disposition",
						"attachment; filename=" + "GSTR3B_" + gstin + "_"
								+ taxPeriod + "_" + "GSTN" + ".pdf");
			}

			OutputStream out;
			out = response.getOutputStream();
			JasperExportManager.exportReportToPdfStream(jasperPrint, out);

			return new ResponseEntity<>(null, HttpStatus.OK);

		} catch (Exception e) {
			LOGGER.error("Exception while generating Gstr3B PDF ", e);
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

	@PostMapping(value = "/ui/generateBulkGstr3BSummaryPDFReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> generateBulkGstr3BSummeryPdf(
			@RequestBody String jsonParam, HttpServletResponse response) {
		JsonObject errorResp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonParam)
					.getAsJsonObject();

			JsonArray gstinArray = requestObject.get(APIConstants.GSTIN_LIST)
					.getAsJsonArray();
			String taxPeriod = requestObject.get(APIConstants.TAXPERIOD)
					.getAsString();
			String isVerified = requestObject.get("isVerified").getAsString();
			String resp = gstr3BSummaryPDFGenerationReport
					.generateBulkGstr3BSummaryPdfReport(gstinArray, response,
							taxPeriod, isVerified);
			APIRespDto apiResp = null;
			if (resp == null) {
				LOGGER.error(
						"No Summary Data for Selected gstin and taxPeriod");
				JsonObject respObj = new JsonObject();
				String msg = "No Summary Data for Selected gstin and taxPeriod";
				JsonArray respBody = new JsonArray();
				JsonObject json = new JsonObject();
				apiResp = APIRespDto.creatErrorResp();
				json.addProperty("msg", msg);
				respBody.add(json);
				respObj.add("hdr", gson.toJsonTree(apiResp));
				respObj.add("resp", gson.toJsonTree(respBody));
				return new ResponseEntity<>(respObj.toString(), HttpStatus.OK);

			}

			return new ResponseEntity<>(null, HttpStatus.OK);

		} catch (Exception e) {
			LOGGER.error("Exception while generating Bulk Gstr3B PDF ", e);
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

	@PostMapping(value = "/ui/generateBulkGstr3BSummaryPDFReportAsync", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> generateBulkGstr3BSummeryPdfAsync(
			@RequestBody String jsonParam, HttpServletResponse response) {

		JsonObject jobParams = new JsonObject();
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonObject errorResp = new JsonObject();

		try {
			JsonObject obj = JsonParser.parseString(jsonParam)
					.getAsJsonObject();
			String reqJson = obj.getAsJsonObject().toString();
			Annexure1SummaryReqDto req = gson.fromJson(reqJson.toString(),
					Annexure1SummaryReqDto.class);
			String groupCode = TenantContext.getTenantId();
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();
			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
			entity.setTaxPeriod(req.getTaxPeriod());
			entity.setCreatedBy(userName);
			entity.setDataType("GSTR 3B");
			entity.setReportCateg("GSTR 3B");
			entity.setReportType(ReportTypeConstants.GSTR3_PDF);
			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setReqPayload(gson.toJsonTree(req).toString());
			entity = downloadRepository.save(entity);

			Long id = entity.getId();
			String reportType = entity.getReportType();
			jobParams = new JsonObject();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Inside controller with  Report id : %id",
						id);
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
			
			/*jobParams.addProperty("isDigigst", req.getIsDigigst());
			jobParams.addProperty("isVerified", req.getIsVerified());*/

		//	asyncReportDownloadServiceGstr3b.generateReports(id);

			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR3B_BULK_PDF_DOWNLOAD, jobParams.toString(),
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
			LOGGER.error("Exception while generating Bulk Gstr3B PDF ", e);
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
