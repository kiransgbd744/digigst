package com.ey.advisory.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.ledger.GetCashLedgerDetailsReqDto;
import com.ey.advisory.app.services.ledger.GetCashLedgerDetails;
import com.ey.advisory.app.util.Anx1CsvDownloadUtil;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.ConfigConstants;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@Slf4j
@RestController
public class GetCashLedgerDetailsController {

	@Autowired
	@Qualifier("getCashLedgerDetailsImpl")
	private GetCashLedgerDetails getCash;

	@Autowired
	@Qualifier("Anx1CsvDownloadUtil")
	private Anx1CsvDownloadUtil anx1CsvDownloadUtil;

	private static final String GROUP_CODE = GetCashLedgerDetailsController
			.staticTenantId();

	@PostMapping(value = "/ui/getCashLedgerDetails", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getCashLedgerDetails(
			@RequestBody String jsonReq, HttpServletRequest request) {
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug("getCashLedgerDetails method called with arg {}", jsonReq);
		String apiResp = getCash.findCash(jsonReq, GROUP_CODE);
		return new ResponseEntity<>(apiResp, HttpStatus.OK);
	}

	@RequestMapping(value = "/ui/getCashLedgerReportDownload", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void getCashLedgerReportDownload(@RequestBody String jsonReq,
			HttpServletResponse response) throws IOException {
		TenantContext.setTenantId(GROUP_CODE);
		Gson gson = GsonUtil.newSAPGsonInstance();
		LOGGER.debug("getCashLedgerReportDownload method called with arg {}",
				jsonReq);
		Workbook workbook = getCash.findCashReportDownload(jsonReq, GROUP_CODE);
		String fileName = DocumentUtility
				.getUniqueFileName(ConfigConstants.LEDGER_REPORT_DOWNLOAD);
		if (workbook != null) {
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition",
					String.format("attachment; filename=" + fileName));
			try {
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
			} catch (Exception e) {
				String msg = String.format(
						"Exception occured while "
								+ "generating the excel sheet, %s ",
						e.getMessage());
				LOGGER.error(msg);
				throw new AppException(e.getMessage(), e);
			}
			response.getOutputStream().flush();
		}
		/*
		 * JsonObject resp = new JsonObject(); resp.add("hdr",
		 * gson.toJsonTree(APIRespDto.createSuccessResp()));
		 */
	}

	@RequestMapping(value = "/ui/getCreditLedgerReportDownload", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void getCreditLedgerReportDownload(@RequestBody String jsonReq,
			HttpServletResponse response) throws IOException {
		TenantContext.setTenantId(GROUP_CODE);
		Gson gson = GsonUtil.newSAPGsonInstance();
		LOGGER.debug(
				"inside controller calling findCreditReportDownload method  with arg {}",
				jsonReq);
		Workbook workbook = getCash.findCreditReportDownload(jsonReq,
				GROUP_CODE);
		String fileName = DocumentUtility.getUniqueFileName(
				ConfigConstants.CREDIT_LEDGER_REPORT_DOWNLOAD);
		if (workbook != null) {
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition",
					String.format("attachment; filename=" + fileName));
			try {
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
			} catch (Exception e) {
				String msg = String.format(
						"Exception occured while "
								+ "generating the excel sheet, %s ",
						e.getMessage());
				LOGGER.error(msg);
				throw new AppException(e.getMessage(), e);
			}
			response.getOutputStream().flush();
		}
		/*
		 * JsonObject resp = new JsonObject(); resp.add("hdr",
		 * gson.toJsonTree(APIRespDto.createSuccessResp()));
		 */
	}
	
	/////////// Reversal and reclaim detailed screen excel download ////////
	@RequestMapping(value = "/ui/getReclaimDetailedExcelReport", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void getReclaimDetailedExcelReport(@RequestBody String jsonReq,
			HttpServletResponse response) throws IOException {
		TenantContext.setTenantId(GROUP_CODE);
		Gson gson = GsonUtil.newSAPGsonInstance();
		LOGGER.debug(
				"inside controller calling getReclaimDetailedExcelReport method  with arg {}",
				jsonReq);
		Workbook workbook = getCash.reversalAndReclaimExcelRpt(jsonReq,
				GROUP_CODE);
		String fileName = DocumentUtility.getUniqueFileName(
				ConfigConstants.RECLAIM_LEDGER_REPORT_DOWNLOAD);
		if (workbook != null) {
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition",
					String.format("attachment; filename=" + fileName));
			try {
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
			} catch (Exception e) {
				String msg = String.format(
						"Exception occured while "
								+ "generating the excel sheet, %s ",
						e.getMessage());
				LOGGER.error(msg);
				throw new AppException(e.getMessage(), e);
			}
			response.getOutputStream().flush();
		}
		/*
		 * JsonObject resp = new JsonObject(); resp.add("hdr",
		 * gson.toJsonTree(APIRespDto.createSuccessResp()));
		 */
	}
//------------------------------revrsal and reclaim excel report-------------	

	@PostMapping(value = "/ui/getCashLedgerDetails1", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getCashLedgDetails(
			@RequestBody String jsonReq, HttpServletRequest request) {

		String apiResp = "{\r\n  \"hdr\": {\r\n    \"status\": \"\",\r\n    \"message\": \"\"\r\n  },\r\n  \"resp\": [\r\n    {\r\n      \"dpt_dt\": \"09-03-2019\",\r\n      \"dpt_time\": \"09-03-2019\",\r\n      \"rpt_dt\": \"09-03-2019\",\r\n      \"refNo\": \"3432\",\r\n      \"ret_period\": \"092014\",\r\n      \"desc\": \"test\",\r\n      \"tr_typ\": \"test\",\r\n      \"igstTax\": \"433\",\r\n      \"igstInt\": \"423\",\r\n      \"igstPen\": \"423\",\r\n      \"igstFee\": \"53\",\r\n      \"igstOth\": \"56\",\r\n      \"igstTot\": \"43\",\r\n      \"igstBalTax\": \"34\",\r\n      \"igstBalInt\": \"345\",\r\n      \"igstBalPen\": \"323\",\r\n      \"igstBalFee\": \"6767\",\r\n      \"igstBalOth\": \"345\",\r\n      \"igstBalTot\": \"43\",\r\n      \"cgstTax\": \"324\",\r\n      \"cgstInt\": \"23\",\r\n      \"cgstPen\": \"43\",\r\n      \"cgstFee\": \"343\",\r\n      \"cgstOth\": \"32\",\r\n      \"cgstTot\": \"34\",\r\n      \"cgstBalTax\": \"423\",\r\n      \"cgstBalInt\": \"423\",\r\n      \"cgstBalPen\": \"42\",\r\n      \"cgstBalFee\": \"34\",\r\n      \"cgstBalOth\": \"42\",\r\n      \"cgstBalTot\": \"342\",\r\n      \"sgstTax\": \"423\",\r\n      \"sgstInt\": \"423\",\r\n      \"sgstPen\": \"432\",\r\n      \"sgstFee\": \"423\",\r\n      \"sgstOth\": \"423\",\r\n      \"sgstTot\": \"234\",\r\n      \"sgstBalTax\": \"423\",\r\n      \"sgstBalInt\": \"423\",\r\n      \"sgstBalPen\": \"434\",\r\n      \"sgstBalFee\": \"423\",\r\n      \"sgstBalOth\": \"34\",\r\n      \"sgstBalTot\": \"43\",\r\n      \"cessTax\": \"434\",\r\n      \"cessInt\": \"434\",\r\n      \"cessPen\": \"324\",\r\n      \"cessFee\": \"432\",\r\n      \"cessOth\": \"423\",\r\n      \"cessTot\": \"3423\",\r\n      \"cessBalTax\": \"423\",\r\n      \"cessBalInt\": \"4324\",\r\n      \"cessBalPen\": \"342\",\r\n      \"cessBalFee\": \"34\",\r\n      \"cessBalOth\": \"4543\",\r\n      \"cessBalTot\": \"534\"\r\n    }\r\n  ]\r\n}";
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp1 = new JsonObject();
		JsonObject jsonObject = new JsonParser().parse(apiResp)
				.getAsJsonObject();
		JsonElement reponseBody = gson.toJsonTree(jsonObject);
		resp1.add("resp", reponseBody);
		return new ResponseEntity<>(resp1.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/ui/getITCLedgerDetails1", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getITCLedgerDetails(
			@RequestBody String jsonReq, HttpServletRequest request) {

		String apiResp = "{\r\n  \"hdr\": {\r\n    \"status\": \"\",\r\n    \"message\": \"\"\r\n  },\r\n  \"resp\": [\r\n    {\r\n      \"date\": \"20-12-2019\",\r\n      \"refNo\": \"1234\",\r\n      \"taxPeriod\": \"062019\",\r\n      \"desc\": \"test\",\r\n      \"transType\": \"test\",\r\n      \"crDrIgst\": \"343\",\r\n      \"crDrCgst\": \"434\",\r\n      \"crDrSgst\": \"34\",\r\n      \"crDrCess\": \"43\",\r\n      \"crDrTotal\": \"433\",\r\n      \"balIgst\": \"434\",\r\n      \"balCgst\": \"54\",\r\n      \"balSgst\": \"565\",\r\n      \"balCess\": \"56\",\r\n      \"balTotal\": \"656\"\r\n    },\r\n     {\r\n      \"date\": \"02-06-2019\",\r\n      \"refNo\": \"124\",\r\n      \"taxPeriod\": \"072019\",\r\n      \"desc\": \"test\",\r\n      \"transType\": \"test\",\r\n      \"crDrIgst\": \"343\",\r\n      \"crDrCgst\": \"434\",\r\n      \"crDrSgst\": \"34\",\r\n      \"crDrCess\": \"43\",\r\n      \"crDrTotal\": \"433\",\r\n      \"balIgst\": \"434\",\r\n      \"balCgst\": \"54\",\r\n      \"balSgst\": \"565\",\r\n      \"balCess\": \"56\",\r\n      \"balTotal\": \"656\"\r\n    }\r\n  ]\r\n}";
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp1 = new JsonObject();
		JsonObject jsonObject = new JsonParser().parse(apiResp)
				.getAsJsonObject();
		JsonElement reponseBody = gson.toJsonTree(jsonObject);
		resp1.add("resp", reponseBody);
		return new ResponseEntity<>(resp1.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/ui/getLiabilityLedgerDetails1", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getLiabilityLedgerDetails(
			@RequestBody String jsonReq, HttpServletRequest request) {
		LOGGER.debug("getCashITCBalance method called with arg {}", jsonReq);
		String apiResp = "{\r\n  \"hdr\": {\r\n    \"status\": \"\",\r\n    \"message\": \"\"\r\n  },\r\n  \"resp\": [\r\n    {\r\n      \"date\": \"07-06-2019\",\r\n      \"refNo\": \"224\",\r\n      \"ledger\": \"test\",\r\n      \"desc\": \"test\",\r\n      \"tr_typ\": \"test\",\r\n      \"igstTax\": \"4\",\r\n      \"igstInt\": \"42\",\r\n      \"igstPen\": \"234\",\r\n      \"igstFee\": \"342\",\r\n      \"igstOth\": \"423\",\r\n      \"igstTot\": \"324\",\r\n      \"igstBalTax\": \"423\",\r\n      \"igstBalInt\": \"23\",\r\n      \"igstBalPen\": \"23\",\r\n      \"igstBalFee\": \"324\",\r\n      \"igstBalOth\": \"423\",\r\n      \"igstBalTot\": \"423\",\r\n      \"cgstTax\": \"324\",\r\n      \"cgstInt\": \"324\",\r\n      \"cgstPen\": \"42\",\r\n      \"cgstFee\": \"342\",\r\n      \"cgstOth\": \"423\",\r\n      \"cgstTot\": \"423\",\r\n      \"cgstBalTax\": \"423\",\r\n      \"cgstBalInt\": \"4234\",\r\n      \"cgstBalPen\": \"432\",\r\n      \"cgstBalFee\": \"423\",\r\n      \"cgstBalOth\": \"42\",\r\n      \"cgstBalTot\": \"423\",\r\n      \"sgstTax\": \"432\",\r\n      \"sgstInt\": \"423\",\r\n      \"sgstPen\": \"4324\",\r\n      \"sgstFee\": \"423\",\r\n      \"sgstOth\": \"423\",\r\n      \"sgstTot\": \"423\",\r\n      \"sgstBalTax\": \"432\",\r\n      \"sgstBalInt\": \"423\",\r\n      \"sgstBalPen\": \"3423\",\r\n      \"sgstBalFee\": \"3423\",\r\n      \"sgstBalOth\": \"342\",\r\n      \"sgstBalTot\": \"423\",\r\n      \"cessTax\": \"4234\",\r\n      \"cessInt\": \"423\",\r\n      \"cessPen\": \"423\",\r\n      \"cessFee\": \"423\",\r\n      \"cessOth\": \"423\",\r\n      \"cessTot\": \"2334\",\r\n      \"cessBalTax\": \"2334\",\r\n      \"cessBalInt\": \"324\",\r\n      \"cessBalPen\": \"4234\",\r\n      \"cessBalFee\": \"42\",\r\n      \"cessBalOth\": \"423\",\r\n      \"cessBalTot\": \"42\"\r\n    }\r\n  ]\r\n}";
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp1 = new JsonObject();
		JsonObject jsonObject = new JsonParser().parse(apiResp)
				.getAsJsonObject();
		JsonElement reponseBody = gson.toJsonTree(jsonObject);
		resp1.add("resp", reponseBody);
		return new ResponseEntity<>(resp1.toString(), HttpStatus.OK);
	}

	public static String staticTenantId() {
		// return "ern00002";
		// return "shi00005";
		return "y8nvcqp4f9";
	}

	@PostMapping(value = "/ui/getCashLedgerDetailsPDF")
	public void getCashLedgerDetailsPDF(@RequestBody String jsonReq,
			HttpServletResponse response) throws Exception {
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug("getCashLedgerDetailsPDF method called with arg {}",
				jsonReq);
		String fileName = null;
		InputStream inputStream = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject requestObject = JsonParser.parseString(jsonReq)
				.getAsJsonObject();
		JsonObject reqObject = requestObject.get("req").getAsJsonObject();
		GetCashLedgerDetailsReqDto dto = gson.fromJson(reqObject,
				GetCashLedgerDetailsReqDto.class);
		JasperPrint jasperPrint = null;
		LocalDateTime reqRTime = LocalDateTime.now();
		String recTime = reqRTime.toString();
		String reqReceivedTime = recTime.replaceAll("[-T:.]", "");
		fileName = dto.getGstin()+"_ElectronicCashLedger" + "_" + reqReceivedTime + ".pdf";
		File tempDir = null;
		
		try {
			tempDir = createTempDir();
			String fullPath = tempDir.getAbsolutePath() + File.separator + fileName;
			jasperPrint = getCash.generatePdfReport(dto,GROUP_CODE);
			if (jasperPrint == null) {
	            LOGGER.error("No data available to generate PDF");
	            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	            response.getWriter().write("No Data available to generate PDF");
	            return;
	        }
			try (OutputStream outStream = new BufferedOutputStream(
					new FileOutputStream(fullPath), 8192)) {
				JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
				outStream.flush();
			}
			if (tempDir.list().length > 0) {
				File file = new File(tempDir, fileName);
				int read = 0;
				byte[] bytes = new byte[1024];
				inputStream = FileUtils.openInputStream(file);
				response.setHeader("Content-Disposition",
						String.format("attachment; filename=" + fileName));
				OutputStream outputStream = response.getOutputStream();

				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
				response.getOutputStream().flush();
			}
		} catch (Exception ex) {
			LOGGER.error("Exception while generating Einvoice PDF ", ex);
			response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
			throw new Exception("Exception while generating Einvoice PDF", ex);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			anx1CsvDownloadUtil.deleteTemporaryDirectory(tempDir);
		}
		
	}
	
	@PostMapping(value = "/ui/getReclaimDetailedPdfReport")
	public void getReclaimDetailedPdfReport(@RequestBody String jsonReq,
			HttpServletResponse response) throws Exception {
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug("getreverseAndReclaimLedgerDetailsPDF method called with arg {}",
				jsonReq);
		String fileName = null;
		InputStream inputStream = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject requestObject = JsonParser.parseString(jsonReq)
				.getAsJsonObject();
		JsonObject reqObject = requestObject.get("req").getAsJsonObject();
		GetCashLedgerDetailsReqDto dto = gson.fromJson(reqObject,
				GetCashLedgerDetailsReqDto.class);
		JasperPrint jasperPrint = null;
		LocalDateTime reqRTime = LocalDateTime.now();
		String recTime = reqRTime.toString();
		String reqReceivedTime = recTime.replaceAll("[-T:.]", "");
		//need to change this name
		fileName = "ReversalandReclaimLedgerReport"+ "_" + reqReceivedTime + ".pdf";
		File tempDir = null;
		
		try {
			tempDir = createTempDir();
			String fullPath = tempDir.getAbsolutePath() + File.separator + fileName;
			jasperPrint = getCash.generateReversalAndReclaimPdfReport(dto,GROUP_CODE);
			if (jasperPrint == null) {
	            LOGGER.error("No data available to generate PDF");
	            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	            response.getWriter().write("No Data available to generate PDF");
	            return;
	        }
			try (OutputStream outStream = new BufferedOutputStream(
					new FileOutputStream(fullPath), 8192)) {
				JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
				outStream.flush();
			}
			if (tempDir.list().length > 0) {
				File file = new File(tempDir, fileName);
				int read = 0;
				byte[] bytes = new byte[1024];
				inputStream = FileUtils.openInputStream(file);
				response.setHeader("Content-Disposition",
						String.format("attachment; filename=" + fileName));
				OutputStream outputStream = response.getOutputStream();

				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
				response.getOutputStream().flush();
			}
		} catch (Exception ex) {
			LOGGER.error("Exception while generating Einvoice PDF ", ex);
			response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
			throw new Exception("Exception while generating Einvoice PDF", ex);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			anx1CsvDownloadUtil.deleteTemporaryDirectory(tempDir);
		}
		
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("PdfReports").toFile();
	}

}
