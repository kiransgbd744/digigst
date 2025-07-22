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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.ledger.GetCashITCBalanceReqDto;
import com.ey.advisory.app.docs.dto.ledger.GetCashLedgerDetailsReqDto;
import com.ey.advisory.app.services.ledger.GetCreditLedgerDetailsPdfImpl;
import com.ey.advisory.app.util.Anx1CsvDownloadUtil;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@Slf4j
@RestController
public class GetCreditAndLiablityLedgerPdfController {

	@Autowired
	@Qualifier("GetCreditLedgerDetailsPdfImpl")
	private GetCreditLedgerDetailsPdfImpl creditLiabPdf;

	@Autowired	
	@Qualifier("Anx1CsvDownloadUtil")
	private Anx1CsvDownloadUtil anx1CsvDownloadUtil;

	private static final String GROUP_CODE = GetCreditAndLiablityLedgerPdfController
			.staticTenantId();

	public static String staticTenantId() {
		// return "ern00002";
		// return "shi00005";
		return "y8nvcqp4f9";
	}

	@PostMapping(value = "/ui/getCreditLedgerDetailsPdf")
	public void getCashLedgerDetailsPDF(@RequestBody String jsonReq,
			HttpServletResponse response) throws Exception {
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug("getCreditLedgerDetailsPDF method called with arg {}",
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
		fileName = dto.getGstin()+"_ElectronicCreditLedger" + "_" + reqReceivedTime + ".pdf";
		File tempDir = null;
		
		try {
			tempDir = createTempDir();
			String fullPath = tempDir.getAbsolutePath() + File.separator + fileName;
			jasperPrint = creditLiabPdf.generateCreditPdfReport(dto,GROUP_CODE);
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
			LOGGER.error("Exception while generating Credit PDF ", ex);
			response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
			throw new Exception("Exception while generating Credit PDF", ex);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			anx1CsvDownloadUtil.deleteTemporaryDirectory(tempDir);
		}
		
	}
	
	
	@PostMapping(value = "/ui/getLiabilityLedgerDetailsPdf")
	public void getLiabilityLedgerDetailsPDF(@RequestBody String jsonReq,
			HttpServletResponse response) throws Exception {
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug("getLiabilityLedgerDetailsPDF method called with arg {}",
				jsonReq);
		String fileName = null;
		InputStream inputStream = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject requestObject = JsonParser.parseString(jsonReq)
				.getAsJsonObject();
		JsonObject reqObject = requestObject.get("req").getAsJsonObject();
		GetCashITCBalanceReqDto dto = gson.fromJson(reqObject,
				GetCashITCBalanceReqDto.class);
		JasperPrint jasperPrint = null;
		LocalDateTime reqRTime = LocalDateTime.now();
		String recTime = reqRTime.toString();
		String reqReceivedTime = recTime.replaceAll("[-T:.]", "");
		fileName = dto.getGstin()+"ElectronicLiabilityLedger" + "_" + reqReceivedTime + ".pdf";
		File tempDir = null;
		
		try {
			tempDir = createTempDir();
			String fullPath = tempDir.getAbsolutePath() + File.separator + fileName;
			jasperPrint = creditLiabPdf.generateLiabilityPdfReport(dto,GROUP_CODE);
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
			LOGGER.error("Exception while generating Liability PDF ", ex);
			response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
			throw new Exception("Exception while generating Liability PDF", ex);
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
