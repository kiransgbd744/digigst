package com.ey.advisory.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.docs.dto.InwardEinvoiceReqDto;
import com.ey.advisory.app.inward.einvoice.InwardEinvoiceServicePdf;
import com.ey.advisory.app.util.Anx1CsvDownloadUtil;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@Slf4j
@RestController
public class InwardEinvoiceSummaryPDFController {

	@Autowired
	@Qualifier("InwardEinvoiceSummaryPDFDaoImpl")
	InwardEinvoiceServicePdf inwardeEinvoiceServicePdf;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinDetailRepository;

	@Autowired
	CombineAndZipXlsxFiles combineAndZipXlsxFiles;

	@Autowired
	@Qualifier("Anx1CsvDownloadUtil")
	private Anx1CsvDownloadUtil anx1CsvDownloadUtil;

	@GetMapping(value = "/ui/InwardEinvoiceSummaryPdfReport")
	public void generatePdfReport(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		String fileName = null;
		InputStream inputStream = null;

		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();

		Type listType = new TypeToken<List<InwardEinvoiceReqDto>>() {
		}.getType();

		JsonArray json = requestObject.get("req").getAsJsonArray();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Request Json {}", json);
		}
		List<InwardEinvoiceReqDto> criteria = gsonEwb.fromJson(json, listType);
		File tempDir = null;
		try {
			tempDir = createTempDir();
			for (InwardEinvoiceReqDto req : criteria) {
				String irn = req.getIrn();
				String supplyType = req.getSupplyType();
				String irnStatus = req.getIrnStatus();
				String docType = req.getDocType();
				String docNo = req.getDocNum();
				JasperPrint jasperPrint = null;
				LocalDateTime reqRTime = LocalDateTime.now();
				String recTime = reqRTime.toString();
				String reqReceivedTime = recTime.replaceAll("[-T:.]", "");
			
				fileName = "Inward E-invoice_"+docType+"_"+docNo+"_"+reqReceivedTime;
				

				String fullPath = tempDir.getAbsolutePath() + File.separator
						+ fileName + ".pdf";
				try (OutputStream outStream = new BufferedOutputStream(
						new FileOutputStream(fullPath), 8192)) {

						jasperPrint = inwardeEinvoiceServicePdf.generatePdfReport(irn,irnStatus,supplyType, docType);

					JasperExportManager.exportReportToPdfStream(jasperPrint,
							outStream);

					outStream.flush();
					outStream.close();

				} catch (Exception e) {
					LOGGER.error(
							"Error occurred while generating and exporting the PDF report for IRN: {}, IRN Status: {}",
							irn, irnStatus, e);
				}

			}
			fileName = zipEinvoicePdfFiles(tempDir);
			if (tempDir.list().length > 0) {
				File zipFile = new File(tempDir, fileName);
				int read = 0;
				byte[] bytes = new byte[1024];
				inputStream = FileUtils.openInputStream(zipFile);
				response.setHeader("Content-Disposition",
						String.format("attachment; filename=" + fileName));

				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
				response.getOutputStream().flush();
				response.getOutputStream().close();
			}

			//inputStream.close();

		} catch (Exception ex) {

			LOGGER.error("Exception while generating Einvoice PDF ", ex);
			response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
			throw new Exception("Exception while generating Einvoice PDF", ex);
		} finally {
			anx1CsvDownloadUtil.deleteTemporaryDirectory(tempDir);
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// ADD LOGGER TODO
				}
			}
		}
		
	}
	
	@PostMapping(value = "/ui/InwardEinvoiceSummaryPdfReports")
	public void generateEinvSummaryReport(@RequestBody String jsonString, HttpServletResponse response) throws Exception {
	    String fileName = null;
	    InputStream inputStream = null;
	    Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
	    JsonObject requestObject = JsonParser.parseString(jsonString).getAsJsonObject();
	    Type listType = new TypeToken<List<InwardEinvoiceReqDto>>() {}.getType();
	    JsonArray json = requestObject.get("req").getAsJsonArray();

	    if (LOGGER.isDebugEnabled()) {
	        LOGGER.debug("Request Json {}", json);
	    }

	    List<InwardEinvoiceReqDto> criteria = gsonEwb.fromJson(json, listType);
	    File tempDir = null;

	    try {
	        tempDir = createTempDir();

	        if (criteria.size() == 1) {
	            // Single criterion, download a single PDF
	            InwardEinvoiceReqDto req = criteria.get(0);
	            String irn = req.getIrn();
	            String supplyType = req.getSupplyType();
	            String irnStatus = req.getIrnStatus();
	            String docType = req.getDocType();
	            String docNo = req.getDocNum();
	            JasperPrint jasperPrint = null;
	            LocalDateTime reqRTime = LocalDateTime.now();
	            String recTime = reqRTime.toString();
	            String reqReceivedTime = recTime.replaceAll("[-T:.]", "");
	            fileName = "Inward E-invoice_" + docType + "_" + docNo + "_" + reqReceivedTime + ".pdf";
	            String fullPath = tempDir.getAbsolutePath() + File.separator + fileName;

	            try (OutputStream outStream = new BufferedOutputStream(new FileOutputStream(fullPath), 8192)) {
	                jasperPrint = inwardeEinvoiceServicePdf.generatePdfReport(irn, irnStatus, supplyType, docType);
	                JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
	                outStream.flush();
	            }
	        } else {
	            // Multiple criteria, download a zip file
	        	for (InwardEinvoiceReqDto req : criteria) {
					String irn = req.getIrn();
					String supplyType = req.getSupplyType();
					String irnStatus = req.getIrnStatus();
					String docType = req.getDocType();
					String docNo = req.getDocNum();
					JasperPrint jasperPrint = null;
					LocalDateTime reqRTime = LocalDateTime.now();
					String recTime = reqRTime.toString();
					String reqReceivedTime = recTime.replaceAll("[-T:.]", "");
				
					fileName = "Inward E-invoice_"+docType+"_"+docNo+"_"+reqReceivedTime;
					

					String fullPath = tempDir.getAbsolutePath() + File.separator
							+ fileName + ".pdf";
					try (OutputStream outStream = new BufferedOutputStream(
							new FileOutputStream(fullPath), 8192)) {

							jasperPrint = inwardeEinvoiceServicePdf.generatePdfReport(irn,irnStatus,supplyType, docType);

						JasperExportManager.exportReportToPdfStream(jasperPrint,
								outStream);

						outStream.flush();
						outStream.close();

					} catch (Exception e) {
						LOGGER.error(
								"Error occurred while generating and exporting the PDF report for IRN: {}, IRN Status: {}",
								irn, irnStatus, e);
					}

				}
	            fileName = zipEinvoicePdfFiles(tempDir);
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

	private String zipEinvoicePdfFiles(File tempDir) throws Exception {

		LocalDateTime reqRTime = LocalDateTime.now();
		String recTime = reqRTime.toString();
		String reqReceivedTime = recTime.replaceAll("[-T:.]", "");
		List<String> filesToZip = getAllFilesToBeZipped(tempDir);
		String fileName = "Consolidated_Inward E-invoice_PDF_"+reqReceivedTime;
		String compressedFileName = fileName;

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("compressedFileName : %s",
					compressedFileName);
			LOGGER.debug(msg);
		}

		CombineAndZipXlsxFiles.compressFiles(tempDir.getAbsolutePath(),
				compressedFileName + ".zip", filesToZip);
		String zipFileName = compressedFileName + ".zip";

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("zipFileName : %s", zipFileName);
			LOGGER.debug(msg);
		}
		return zipFileName;

	}

	private static List<String> getAllFilesToBeZipped(File tmpDir)
			throws Exception {

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Temporary directory containing files to "
							+ "be zipped: %s", tmpDir.getAbsolutePath());
			LOGGER.debug(msg);
		}
		FilenameFilter pdfFilter = new FilenameFilter() {
			public boolean accept(File tmpDir, String name) {
				return name.toLowerCase().endsWith(".pdf");
			}
		};
		File[] files = tmpDir.listFiles(pdfFilter);
		List<String> retFileNames = Arrays.stream(files)
				.map(f -> f.getAbsolutePath())
				.collect(Collectors.toCollection(ArrayList::new));

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("List of files to be zipped %s",
					retFileNames);
			LOGGER.debug(msg);
		}
		// Return the list of files.
		return retFileNames;
	}
	
	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("PdfReports").toFile();
	}
}
