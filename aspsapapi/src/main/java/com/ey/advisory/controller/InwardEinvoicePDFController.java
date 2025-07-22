package com.ey.advisory.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.TemplateMappingRepository;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.docs.dto.InwardEinvoiceReqDto;
import com.ey.advisory.app.inward.einvoice.InwardEinvoiceServicePdf;
import com.ey.advisory.app.util.Anx1CsvDownloadUtil;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.client.domain.B2CQRAmtConfigEntity;
import com.google.common.base.Strings;
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
public class InwardEinvoicePDFController {

	@Autowired
	@Qualifier("InwardEinvoicePDFDaoImpl")
	InwardEinvoiceServicePdf inwardeEinvoiceServicePdf;

	@Autowired
	@Qualifier("TemplateMappingRepository")
	private TemplateMappingRepository templateMappingRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinDetailRepository;

	@Autowired
	CombineAndZipXlsxFiles combineAndZipXlsxFiles;

	@Autowired
	@Qualifier("Anx1CsvDownloadUtil")
	private Anx1CsvDownloadUtil anx1CsvDownloadUtil;

	private static final String AND = "&";
	private static final String PIPE = "|";

	@PostMapping(value = "/ui/InwardEinvoicePdfReports")
	public void generateEinvSummaryReport(@RequestBody String jsonString,
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
				fileName = "Inward E-invoice_" + docType + "_" + docNo + "_"
						+ reqReceivedTime + ".pdf";
				String fullPath = tempDir.getAbsolutePath() + File.separator
						+ fileName;

				try (OutputStream outStream = new BufferedOutputStream(
						new FileOutputStream(fullPath), 8192)) {
					jasperPrint = inwardeEinvoiceServicePdf.generatePdfReport(
							irn, irnStatus, supplyType, docType);
					JasperExportManager.exportReportToPdfStream(jasperPrint,
							outStream);
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

					fileName = "Inward E-invoice_" + docType + "_" + docNo + "_"
							+ reqReceivedTime;

					String fullPath = tempDir.getAbsolutePath() + File.separator
							+ fileName + ".pdf";
					try (OutputStream outStream = new BufferedOutputStream(
							new FileOutputStream(fullPath), 8192)) {

						jasperPrint = inwardeEinvoiceServicePdf
								.generatePdfReport(irn, irnStatus, supplyType,
										docType);

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

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("PdfReports").toFile();
	}

	private String zipEinvoicePdfFiles(File tempDir) throws Exception {

		LocalDateTime reqRTime = LocalDateTime.now();
		String recTime = reqRTime.toString();
		String reqReceivedTime = recTime.replaceAll("[-T:.]", "");
		List<String> filesToZip = getAllFilesToBeZipped(tempDir);
		String fileName = "Consolidated_Inward E-invoice_PDF_"
				+ reqReceivedTime;
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

	public static String signedQrCodeGeneration(OutwardTransDocument out,
			String a15, String a19, String payeAddress, String payeeName,
			String marchentCode, String a20, B2CQRAmtConfigEntity mamValue,
			String paymentInfo) {
		StringBuilder result = new StringBuilder();

		result.append("upi://pay?ver=01");
		if (!Strings.isNullOrEmpty(a15)) {
			result.append(AND);
			result.append("mode=");
			result.append(a15);
		}
		result.append(AND);
		result.append("tr=");
		result.append(out.getDocNo());
		result.append(AND);
		result.append("tn=00");
		if (!Strings.isNullOrEmpty(payeAddress)) {
			result.append(AND);
			result.append("pa=");
			result.append(payeAddress);
		}
		if (!Strings.isNullOrEmpty(payeeName)) {
			result.append(AND);
			result.append("pn=");
			result.append(payeeName);
		}
		if (!Strings.isNullOrEmpty(marchentCode)) {
			result.append(AND);
			result.append("mc=");
			result.append(marchentCode);
		}
		BigDecimal docamount = BigDecimal.ZERO;
		if ("B".equalsIgnoreCase(paymentInfo)) {

			docamount = out.getLineItems().stream().filter(Objects::nonNull)
					.map(OutwardTransDocLineItem::getBalanceAmount)
					.filter(Objects::nonNull) // If price can be null
					.reduce(BigDecimal.ZERO, BigDecimal::add)
					.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		} else {
			docamount = out.getDocAmount();
		}
		result.append(AND);
		result.append("am=");
		result.append(docamount);
		result.append(AND);
		result.append("gstBrkUp=");
		result.append(amount(out));
		if (!Strings.isNullOrEmpty(a19)) {
			result.append(AND);
			result.append("qrMedium=");
			result.append(a19);
		}
		result.append(AND);
		result.append("invoiceNo=");
		result.append(out.getDocNo());
		result.append(AND);
		result.append("InvoiceDate=");
		result.append(out.getDocDate().atStartOfDay() + ":00+5:30");
		if (!Strings.isNullOrEmpty(a20)) {
			result.append(AND);
			result.append("QRexpire=");
			LocalDateTime nowDate = EYDateUtil.toUTCDateTimeFromLocal(
					out.getCreatedDate().plusDays(Integer.parseInt(a20)));
			String qRexpire = LocalDateTime.of(nowDate.getYear(),
					nowDate.getMonth(), nowDate.getDayOfMonth(),
					nowDate.getHour(), nowDate.getMinute(), nowDate.getSecond())
					+ "+5:30";
			result.append(qRexpire);
		}
		result.append(AND);
		result.append("gstin=");
		result.append(out.getSgstin());

		if (mamValue != null) {
			String amtIdentifier = mamValue.getIdentifier();
			String value = mamValue.getValue();
			if (amtIdentifier.equalsIgnoreCase("percent")) {
				BigDecimal percentageAmount = out.getDocAmount();
				percentageAmount = percentageAmount.multiply(
						BigDecimal.valueOf((double) Double.valueOf(value) / 100));
				result.append(AND + "mam=" + percentageAmount.setScale(2,
						BigDecimal.ROUND_HALF_EVEN));

			} else {
				result.append(AND + "mam=" + value);
			}
		}
		return result.toString().replaceAll(" ", "%20");
	}

	private static String amount(OutwardTransDocument out) {
		StringBuilder amount = new StringBuilder();
		BigDecimal igstAmount = out.getIgstAmount();
		BigDecimal cgstAmount = out.getCgstAmount();
		BigDecimal sgstAmount = out.getSgstAmount();
		if (igstAmount == null) {
			igstAmount = BigDecimal.ZERO;
		}
		if (cgstAmount == null) {
			cgstAmount = BigDecimal.ZERO;
		}
		if (sgstAmount == null) {
			sgstAmount = BigDecimal.ZERO;
		}
		BigDecimal allAmount = igstAmount.add(cgstAmount).add(sgstAmount);
		if (allAmount.compareTo(BigDecimal.ZERO) == 0) {
			amount.append("IGST:" + igstAmount);
		} else {

			if (igstAmount.compareTo(BigDecimal.ZERO) == 0) {
				amount.append("CGST:" + cgstAmount).append(PIPE)
						.append("SGST:" + sgstAmount);
			} else {
				amount.append("IGST:" + igstAmount);
			}
		}
		BigDecimal cessAmountAdvalorem = out.getCessAmountAdvalorem();
		BigDecimal cessAmountSpecific = out.getCessAmountSpecific();
		BigDecimal stateCessSpecificAmt = out.getStateCessSpecificAmt();
		BigDecimal stateCessAmount = out.getStateCessAmount();
		if (cessAmountAdvalorem != null || cessAmountSpecific != null
				|| stateCessSpecificAmt != null || stateCessAmount == null) {
			if (cessAmountAdvalorem == null) {
				cessAmountAdvalorem = BigDecimal.ZERO;
			}

			if (cessAmountSpecific == null) {
				cessAmountSpecific = BigDecimal.ZERO;
			}

			if (stateCessAmount == null) {
				stateCessAmount = BigDecimal.ZERO;
			}

			if (stateCessSpecificAmt == null) {
				stateCessSpecificAmt = BigDecimal.ZERO;
			}
			BigDecimal cessAmount = cessAmountAdvalorem.add(cessAmountSpecific)
					.add(stateCessSpecificAmt).add(stateCessAmount);
			amount.append(PIPE).append("CESS:" + cessAmount);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("QR code Generation method Ended for docKey{}",
					out.getDocKey());
		}
		return amount.toString();

	}
}
