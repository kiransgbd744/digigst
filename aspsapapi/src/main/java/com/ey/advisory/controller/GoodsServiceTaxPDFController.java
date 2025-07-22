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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.entities.client.TemplateMappingEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.TemplateMappingRepository;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.OutwardTransDocumentRepository;
import com.ey.advisory.app.data.services.ewb.GoodsCredDebCancelPDF;
import com.ey.advisory.app.data.services.ewb.GoodsCredDebPdf;
import com.ey.advisory.app.data.services.ewb.GoodsServicePdf;
import com.ey.advisory.app.data.services.ewb.GoodsServicesCancelPDF;
import com.ey.advisory.app.data.services.ewb.ServiceCredDebCancel;
import com.ey.advisory.app.data.services.ewb.ServiceCredDebPdf;
import com.ey.advisory.app.data.services.ewb.ServiceTaxCancelPDF;
import com.ey.advisory.app.data.services.ewb.ServiceTaxPdf;
import com.ey.advisory.app.docs.dto.PdfPrintReqDto;
import com.ey.advisory.app.util.Anx1CsvDownloadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AspInvoiceStatus;
import com.ey.advisory.common.B2COnBoardingCommonUtility;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenerateParamsForDeepLink;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.client.domain.B2COnBoardingConfigEntity;
import com.ey.advisory.common.client.domain.B2CQRAmtConfigEntity;
import com.ey.advisory.common.client.repositories.B2COnBoardingConfigRepo;
import com.ey.advisory.common.client.repositories.B2CQRAmtConfigRepo;
import com.ey.advisory.domain.client.B2CQRCodeRequestLogEntity;
import com.ey.advisory.repositories.client.B2CQRCodeLoggerRepository;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@Slf4j
@RestController
public class GoodsServiceTaxPDFController {

	@Autowired
	@Qualifier("GoodsServicesPDFDaoImpl")
	GoodsServicePdf goodsServicePdf;

	@Autowired
	@Qualifier("GoodsServicesCancelPDFDaoImpl")
	GoodsServicesCancelPDF goodsServicesCancelPDF;

	@Autowired
	@Qualifier("GoodsCredDebPDFDaoImpl")
	GoodsCredDebPdf goodsCredDebPdf;

	@Autowired
	@Qualifier("GoodsCredDebCancelPDFDaoImpl")
	GoodsCredDebCancelPDF goodsCredDebCancelPDF;

	@Autowired
	@Qualifier("ServiceTaxInvoicePDFDaoImpl")
	ServiceTaxPdf servicePdf;

	@Autowired
	@Qualifier("ServiceTaxInvoiceCancelPDFDaoImpl")
	ServiceTaxCancelPDF serviceTaxCancelPDF;

	@Autowired
	@Qualifier("ServiceCredDebPDFDaoImpl")
	ServiceCredDebPdf serviceCredDebPdf;

	@Autowired
	@Qualifier("ServiceCredDebCancelPDFDaoImpl")
	ServiceCredDebCancel serviceCredDebCancel;

	@Autowired
	@Qualifier("B2CQRAmtConfigRepo")
	B2CQRAmtConfigRepo b2CQRAmtConfigRepo;

	@Autowired
	private B2COnBoardingCommonUtility b2cOnBoardingCommonUtility;

	@Autowired
	@Qualifier("TemplateMappingRepository")
	private TemplateMappingRepository templateMappingRepository;

	@Autowired
	private OutwardTransDocumentRepository outwardTransDocumentRepo;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinDetailRepository;

	@Autowired
	CombineAndZipXlsxFiles combineAndZipXlsxFiles;
	@Autowired
	@Qualifier("B2CQRCodeLoggerRepository")
	B2CQRCodeLoggerRepository qrCodeRepo;
	@Autowired
	@Qualifier("Anx1CsvDownloadUtil")
	private Anx1CsvDownloadUtil anx1CsvDownloadUtil;

	@Autowired
	@Qualifier("B2COnBoardingConfigRepo")
	private B2COnBoardingConfigRepo b2COnBoardingConfigRepo;

	private static final List<String> CGSTIN_FORMAT = ImmutableList
			.of(GSTConstants.UN, GSTConstants.ON);

	private static final List<String> CRDR = ImmutableList.of(GSTConstants.CR,
			GSTConstants.DR, GSTConstants.RCR, GSTConstants.RDR);
	private static final String AND = "&";
	private static final String PIPE = "|";

	@GetMapping(value = "/ui/generateGoodsServicesPdfReport")
	public void generatePdfReport(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String docID = request.getParameter("id");
		String docNo = request.getParameter("docNo");
		String sgstin = request.getParameter("sgstin");

		if (docID == null || docID.isEmpty()) {
			String msg = "Doc Header ID is mandatory to generate EInvoice PDF Report";
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		if (docNo == null || docNo.isEmpty()) {
			String msg = "DocNo is mandatory to generate EInvoice PDF Report";
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		if (sgstin == null || sgstin.isEmpty()) {
			String msg = "sgstin is mandatory to generate EInvoice PDF Report";
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		try {
			Optional<OutwardTransDocument> currOutward = outwardTransDocumentRepo
					.findById(Long.parseLong(docID));
			if (!currOutward.isPresent()) {
				String errMsg = String.format(
						"Invoice %s is not available in the system", docID);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			JasperPrint jasperPrint = null;
			OutwardTransDocument out1 = currOutward.get();

			String deriveTemplateCode = deriveTemplateCode(out1, sgstin);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("template code for gstin{},deriveTemplateCode{}");
			}
			if(deriveTemplateCode!=null){
			
			switch (deriveTemplateCode) {

			case "G01": {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Goods-Tax Invoices template file download");
				}
				jasperPrint = goodsServicePdf.generatePdfReport(docID, docNo,
						sgstin);
				response.setContentType("application/x-download");
				response.addHeader("Content-disposition",
						String.format(
								"attachment; filename=GoodsTaxInvoice_%s_%s_%s.pdf",
								sgstin, docNo, deriveTemplateCode));

				break;
			}
			case "G02": {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Goods-Credit Debit Notes template file download");
				}
				jasperPrint = goodsCredDebPdf.generateCredDebPdfReport(docID,
						docNo, sgstin);
				response.setContentType("application/x-download");
				response.addHeader("Content-disposition",
						String.format(
								"attachment; filename=GoodsCreditDebit_%s_%s_%s.pdf",
								sgstin, docNo, deriveTemplateCode));

				break;
			}
			case "S01": {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Service-Tax Invoices template file download");
				}
				jasperPrint = servicePdf.generateServiceTaxPdfReport(docID,
						docNo, sgstin);

				response.setContentType("application/x-download");
				response.addHeader("Content-disposition",
						String.format(
								"attachment; filename=ServiceTaxInvoice_%s_%s_%s.pdf",
								sgstin, docNo, deriveTemplateCode));
				break;
			}
			case "S02": {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Service -Credit Debit Notes template file download");
				}
				jasperPrint = serviceCredDebPdf
						.generateServiceCredPdfReport(docID, docNo, sgstin);

				response.setContentType("application/x-download");
				response.addHeader("Content-disposition",
						String.format(
								"attachment; filename=ServiceCreditDebitnotes_%s_%s_%s.pdf",
								sgstin, docNo, deriveTemplateCode));
				break;
			}
			}
			}
			OutputStream out;

			out = response.getOutputStream();
			JasperExportManager.exportReportToPdfStream(jasperPrint, out);
		} catch (IOException | JRException ex) {
			LOGGER.error("Exception while generating GoodsService PDF ", ex);

			throw new Exception("user needs to onboard first");

		}
	}

	@PostMapping(value = "/ui/GoodsmultiplePdfReports")
	public void generateEinvSummaryReport(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		String fileName = null;
		InputStream inputStream = null;

		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		Type listType = new TypeToken<List<PdfPrintReqDto>>() {
		}.getType();

		JsonArray json = requestObject.get("req").getAsJsonArray();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Request Json {}", json);
		}
		List<PdfPrintReqDto> criteria = gsonEwb.fromJson(json, listType);
		File tempDir = null;
		String deriveTemplateCode = null;
		OutwardTransDocument out1 = null;
		try {
			String zipFileName = null;
			tempDir = createTempDir();
			Map<String, B2COnBoardingConfigEntity> preferenceMap = new HashMap<>();
			Map<String, B2CQRAmtConfigEntity> getMamValueMap = new HashMap<>();
			Map<String, GenerateParamsForDeepLink> onBoardMap = new HashMap<>();
			for (PdfPrintReqDto req : criteria) {
				String docID = req.getId();
				String sgstin = req.getSgstin();
				Optional<OutwardTransDocument> currOutward = outwardTransDocumentRepo
						.findById(Long.parseLong(docID));
				if (!currOutward.isPresent()) {
					String errMsg = String.format(
							"Invoice %s is not available in the system", docID);
					LOGGER.error(errMsg);
					throw new AppException(errMsg);
				}

				JasperPrint jasperPrint = null;

				out1 = currOutward.get();
				dynamicQrcodeSave(out1, preferenceMap, getMamValueMap,
						onBoardMap);
				deriveTemplateCode = deriveTemplateCode(out1, sgstin);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"template code for gstin{},deriveTemplateCode{}");
				}

				String cgstin = out1.getCgstin();
				if (GSTConstants.URP.equalsIgnoreCase(cgstin)) {
					cgstin = "";
				}
				switch (deriveTemplateCode) {

				case "G01": {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Goods-Tax Invoices template file download");
					}
					String id = req.getId();
					String docNo = req.getDocNo();
					docNo = docNo.replaceAll("/", "");
					sgstin = req.getSgstin();

					if (out1.getDocType().equalsIgnoreCase("BOS")) {
						fileName = "Bill Of Supply_" + docNo + "_" + sgstin
								+ "_" + id;

					} else {
						fileName = "Tax Invoice_" + docNo + "_" + sgstin + "_"
								+ id;
					}

					String fullPath = tempDir.getAbsolutePath() + File.separator
							+ fileName + ".pdf";
					try (OutputStream outStream = new BufferedOutputStream(
							new FileOutputStream(fullPath), 8192)) {

						if ((out1.getIrnStatus() != null
								&& out1.getIrnStatus() == 6
								&& out1.geteInvStatus() != null
								&& out1.geteInvStatus() == 9)
								|| (out1.getSupplyType().equalsIgnoreCase("CAN")
										&& Strings.isNullOrEmpty(cgstin))
								|| (out1.getSupplyType().equalsIgnoreCase("CAN")
										&& (!Strings.isNullOrEmpty(
												out1.getCgstin()))
										&& (out1.getCgstin().length() == 15)

										&& CGSTIN_FORMAT
												.contains(out1.getCgstin()
														.substring(12, 14)))) {
							jasperPrint = goodsServicesCancelPDF
									.generateCancelPdfReport(id, docNo, sgstin);
						} else {
							jasperPrint = goodsServicePdf.generatePdfReport(id,
									docNo, sgstin);
						}

						JasperExportManager.exportReportToPdfStream(jasperPrint,
								outStream);

						outStream.flush();
						outStream.close();

					} catch (Exception e) {
						LOGGER.error("Failed to close InputStream", e);
					}
					break;
				}
				case "G02": {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Goods-Credit Debit Notes template file download");
					}
					String id = req.getId();
					String docNo = req.getDocNo();
					docNo = docNo.replaceAll("/", "");
					sgstin = req.getSgstin();

					if (out1.getDocType().equalsIgnoreCase("CR")) {
						fileName = "Credit Note_" + docNo + "_" + sgstin + "_"
								+ id;
					}
					if (out1.getDocType().equalsIgnoreCase("DR")) {
						fileName = "Debit Note_" + docNo + "_" + sgstin + "_"
								+ id;
					}
					String fullPath = tempDir.getAbsolutePath() + File.separator
							+ fileName + ".pdf";
					try (OutputStream outStream = new BufferedOutputStream(
							new FileOutputStream(fullPath), 8192)) {
						if ((out1.getIrnStatus() != null
								&& out1.getIrnStatus() == 6
								&& out1.geteInvStatus() != null
								&& out1.geteInvStatus() == 9)
								|| (out1.getSupplyType().equalsIgnoreCase("CAN")
										&& Strings.isNullOrEmpty(cgstin))
								|| (out1.getSupplyType().equalsIgnoreCase("CAN")
										&& (!Strings.isNullOrEmpty(
												out1.getCgstin()))
										&& (out1.getCgstin().length() == 15)

										&& CGSTIN_FORMAT
												.contains(out1.getCgstin()
														.substring(12, 14)))) {
							jasperPrint = goodsCredDebCancelPDF
									.generateCredDebCancelPdfReport(id, docNo,
											sgstin);
						} else {
							jasperPrint = goodsCredDebPdf
									.generateCredDebPdfReport(id, docNo,
											sgstin);
						}
						JasperExportManager.exportReportToPdfStream(jasperPrint,
								outStream);

						outStream.flush();
						outStream.close();

					} catch (Exception e) {
						LOGGER.error("Failed to close InputStream", e);
					}

					break;
				}
				case "S01": {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Service-Tax Invoices template file download");
					}
					String id = req.getId();
					String docNo = req.getDocNo();
					docNo = docNo.replaceAll("/", "");
					sgstin = req.getSgstin();

					if (out1.getDocType().equalsIgnoreCase("BOS")) {
						fileName = "Bill Of Supply_" + docNo + "_" + sgstin
								+ "_" + id;

					} else {
						fileName = "Tax Invoice_" + docNo + "_" + sgstin + "_"
								+ id;
					}
					String fullPath = tempDir.getAbsolutePath() + File.separator
							+ fileName + ".pdf";
					try (OutputStream outStream = new BufferedOutputStream(
							new FileOutputStream(fullPath), 8192)) {

						if ((out1.getIrnStatus() != null
								&& out1.getIrnStatus() == 6
								&& out1.geteInvStatus() != null
								&& out1.geteInvStatus() == 9)
								|| (out1.getSupplyType().equalsIgnoreCase("CAN")
										&& Strings.isNullOrEmpty(cgstin))
								|| (out1.getSupplyType().equalsIgnoreCase("CAN")
										&& (!Strings.isNullOrEmpty(
												out1.getCgstin()))
										&& (out1.getCgstin().length() == 15)

										&& CGSTIN_FORMAT
												.contains(out1.getCgstin()
														.substring(12, 14)))) {
							jasperPrint = serviceTaxCancelPDF
									.generateServiceTaxCancelPdfReport(id,
											docNo, sgstin);
						} else {
							jasperPrint = servicePdf
									.generateServiceTaxPdfReport(id, docNo,
											sgstin);
						}
						JasperExportManager.exportReportToPdfStream(jasperPrint,
								outStream);

						outStream.flush();
						outStream.close();

					} catch (Exception e) {
						LOGGER.error("Failed to close InputStream", e);
					}

					break;
				}
				case "S02": {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Service -Credit Debit Notes template file download");
					}
					String id = req.getId();
					String docNo = req.getDocNo();
					docNo = docNo.replaceAll("/", "");
					sgstin = req.getSgstin();

					if (out1.getDocType().equalsIgnoreCase("CR")) {
						fileName = "Credit Note_" + docNo + "_" + sgstin + "_"
								+ id;
					}
					if (out1.getDocType().equalsIgnoreCase("DR")) {
						fileName = "Debit Note_" + docNo + "_" + sgstin + "_"
								+ id;
					}

					String fullPath = tempDir.getAbsolutePath() + File.separator
							+ fileName + ".pdf";
					try (OutputStream outStream = new BufferedOutputStream(
							new FileOutputStream(fullPath), 8192)) {

						if ((out1.getIrnStatus() != null
								&& out1.getIrnStatus() == 6
								&& out1.geteInvStatus() != null
								&& out1.geteInvStatus() == 9)
								|| (out1.getSupplyType().equalsIgnoreCase("CAN")
										&& Strings.isNullOrEmpty(cgstin))
								|| (out1.getSupplyType().equalsIgnoreCase("CAN")
										&& (!Strings.isNullOrEmpty(
												out1.getCgstin()))
										&& (out1.getCgstin().length() == 15)

										&& CGSTIN_FORMAT
												.contains(out1.getCgstin()
														.substring(12, 14)))) {
							jasperPrint = serviceCredDebCancel
									.generateServiceCredCancelPdfReport(id,
											docNo, sgstin);
						} else {
							jasperPrint = serviceCredDebPdf
									.generateServiceCredPdfReport(id, docNo,
											sgstin);
						}
						JasperExportManager.exportReportToPdfStream(jasperPrint,
								outStream);

						outStream.flush();
						outStream.close();

					} catch (Exception e) {
						LOGGER.error("Failed to close outputStream", e);
					}
					break;
				}
				}
			}
			zipFileName = zipEinvoicePdfFiles(tempDir);
			if (criteria.size() > 1) {
				fileName = "EInvoice";
			} else {
				if ("G01".equalsIgnoreCase(deriveTemplateCode)) {
					if (out1.getDocType().equalsIgnoreCase("BOS")) {
						fileName = "BillOfSupply";
					} else {
						fileName = "TaxInvoice";
					}
				} else if ("G02".equalsIgnoreCase(deriveTemplateCode)) {
					if (out1.getDocType().equalsIgnoreCase("CR")) {
						fileName = "CreditNote";
					}
					if (out1.getDocType().equalsIgnoreCase("DR")) {
						fileName = "DebitNote";
					}
				} else if ("S01".equalsIgnoreCase(deriveTemplateCode)) {
					if (out1.getDocType().equalsIgnoreCase("BOS")) {
						fileName = "BillOfSupply";
					} else {
						fileName = "TaxInvoice";
					}
				} else if ("S02".equalsIgnoreCase(deriveTemplateCode)) {
					if (out1.getDocType().equalsIgnoreCase("CR")) {
						fileName = "CreditNote";
					}
					if (out1.getDocType().equalsIgnoreCase("DR")) {
						fileName = "DebitNote";
					}
				}
			}

			LocalDateTime reqRTime = LocalDateTime.now();
			String recTime = reqRTime.toString();
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
				response.getOutputStream().close();
			}

			//inputStream.close();

		} catch (Exception ex) {

			LOGGER.error("Exception while generating Einvoice PDF ", ex);
			response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
			throw new Exception("user needs to onboard first");
		} finally {
			anx1CsvDownloadUtil.deleteTemporaryDirectory(tempDir);
			
			 if (inputStream != null) {
			        try {
			            inputStream.close();
			        } catch (IOException e) {
			            LOGGER.error("Error while closing InputStream: ", e);
			        }
			    } 
		}
	}

	private String deriveTemplateCode(OutwardTransDocument out, String sgstin) {
		String templateType = deriveTemplateType(out);
		if (Strings.isNullOrEmpty(templateType)){
			return null;
		}
		List<GSTNDetailEntity> findByGstin = gstinDetailRepository
				.findByGstin(sgstin);
		Long id = findByGstin.get(0).getEntityId();

		List<TemplateMappingEntity> templateCodeEntity = templateMappingRepository
				.findByEntityIdAndTemplateTypeAndIsDeleteFalse(id,
						templateType);

		return templateCodeEntity.get(0).getTemplateCode();

	}

	private String deriveTemplateType(OutwardTransDocument out) {
		List<OutwardTransDocLineItem> lineItems = out.getLineItems();
		if (isService(lineItems)) {
			if (CRDR.contains(out.getDocType())) {

				return "Service -Credit Debit Notes";
			} else {
				return "Service-Tax Invoices";
			}
		} else {
			if (CRDR.contains(out.getDocType())) {

				return "Goods-Credit Debit Notes";
			} else {
				return "Goods-Tax Invoices";
			}
		}

	}

	private static boolean isService(List<OutwardTransDocLineItem> lineItems) {
		for (OutwardTransDocLineItem list : lineItems) {
			if (!GSTConstants.SERVICES_CODE
					.equalsIgnoreCase(list.getHsnSac().substring(0, 2))) {
				return false;
			}
		}
		return true;

	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("PdfReports").toFile();
	}

	private String zipEinvoicePdfFiles(File tempDir) throws Exception {

		List<String> filesToZip = getAllFilesToBeZipped(tempDir);
		String fileName = "TaxInvoice";
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

	private void dynamicQrcodeSave(OutwardTransDocument doc,
			Map<String, B2COnBoardingConfigEntity> preferenceMap,
			Map<String, B2CQRAmtConfigEntity> getMamValueMap,
			Map<String, GenerateParamsForDeepLink> onBoardMap) {
		try {
			if (doc.getAspInvoiceStatus() != null
					&& (doc.getAspInvoiceStatus() == AspInvoiceStatus.ASP_PROCESSED
							.getAspInvoiceStatusCode())
					&& !GSTConstants.CAN.equalsIgnoreCase(doc.getSupplyType())
					&& GSTConstants.O
							.equalsIgnoreCase(doc.getTransactionType())) {
				B2CQRCodeRequestLogEntity dQcentity = new B2CQRCodeRequestLogEntity();
				String url = getUrl(doc, preferenceMap, onBoardMap,
						getMamValueMap);
				if (url == null) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("url null for docKey{}", doc.getDocKey());
					}
					return;
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("url{} for docKey{}", url, doc.getDocKey());
				}
				dQcentity.setCreatedOn(LocalDateTime.now());
				dQcentity.setDocHeaderId(doc.getId());
				dQcentity.setPan(doc.getSgstin().substring(2, 12));
				dQcentity.setUrlCreatedOn(LocalDateTime.now());
				dQcentity.setRespPayload(url);
				dQcentity.setDockey(doc.getDocKey());
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("url{} for docKey{} Saving ", url, doc.getDocKey());
				}
				qrCodeRepo.save(dQcentity);
			} else if (doc.getAspInvoiceStatus() != null
					&& (doc.getAspInvoiceStatus() == AspInvoiceStatus.ASP_PROCESSED
							.getAspInvoiceStatusCode())
					&& GSTConstants.CAN.equalsIgnoreCase(doc.getSupplyType())
					&& GSTConstants.O
							.equalsIgnoreCase(doc.getTransactionType())) {
				List<OutwardTransDocument> findInactiveRecordsByDocKey = outwardTransDocumentRepo
						.findInactiveRecordsByDocKey(doc.getDocKey());

				OutwardTransDocument outwardTransDocument = findInactiveRecordsByDocKey
						.get(0);
				B2CQRCodeRequestLogEntity dQcentity = new B2CQRCodeRequestLogEntity();
				String url = getUrl(outwardTransDocument, preferenceMap,
						onBoardMap, getMamValueMap);
				if (url == null) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("url null for docKey{}", doc.getDocKey());
					}
					return;
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("url{} for docKey{}", url, doc.getDocKey());
				}
				dQcentity.setCreatedOn(LocalDateTime.now());
				dQcentity.setDocHeaderId(doc.getId());
				dQcentity.setPan(doc.getSgstin().substring(2, 12));
				dQcentity.setUrlCreatedOn(LocalDateTime.now());
				dQcentity.setRespPayload(url);
				dQcentity.setDockey(doc.getDocKey());
				qrCodeRepo.save(dQcentity);
			}

		} catch (Exception e) {
			LOGGER.error("Exception Occured:{} ", e);
		}
	}

	private String getUrl(OutwardTransDocument document,
			Map<String, B2COnBoardingConfigEntity> preferenceMap,
			Map<String, GenerateParamsForDeepLink> map,
			Map<String, B2CQRAmtConfigEntity> getMamValueMap) {
		String pan = document.getSgstin().substring(2, 12);
		B2COnBoardingConfigEntity data = getOnboardingQuestion(preferenceMap,
				pan);

		if (data == null)
			
			return null;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("got B2COnBoardingConfigEntity for docKey{}",
					document.getDocKey());
		}
		String optionSelected = data.getOptionSelected();
		GenerateParamsForDeepLink getValuesfromEntity = getOnBoardingdata(
				optionSelected, document.getPlantCode(),
				document.getProfitCentre(), document.getSgstin(), map);
		if (getValuesfromEntity == null)
			return null;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("got GenerateParamsForDeepLink for docKey{}",
					document.getDocKey());
		}
		B2CQRAmtConfigEntity mamValue = getMamValue(getMamValueMap, pan);
		String payeeAddress = getValuesfromEntity.getPayeeAddress();
		String payeeMerCode = getValuesfromEntity.getPayeeMerCode();
		String payeeName = getValuesfromEntity.getPayeeName();
		String transMode = getValuesfromEntity.getTransMode();
		String qrExpiryTime = getValuesfromEntity.getQrExpireTime();
		String transQRMed = getValuesfromEntity.getTransQRMed();
		String paymentInfo = getValuesfromEntity.getPaymentInfo();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("QR code Generation method started for docKey{}",
					document.getDocKey());
		}
		return signedQrCodeGeneration(document, transMode, transQRMed,
				payeeAddress, payeeName, payeeMerCode, qrExpiryTime, mamValue,
				paymentInfo);

	}

	private GenerateParamsForDeepLink getOnBoardingdata(String optionSelected,
			String plantCode, String profitCentre, String sgstin,
			Map<String, GenerateParamsForDeepLink> map) {
		String pan = sgstin.substring(2, 12);
		GenerateParamsForDeepLink getValuesfromEntity = null;
		// Sextet<String, String, String, String, String, String>
		// getValuesfromEntity = null;
		String allComb = new StringJoiner(PIPE).add(optionSelected)
				.add(plantCode).add(profitCentre).add(sgstin).add(pan)
				.toString();
		if (map.containsKey(allComb))
			return map.get(allComb);
		getValuesfromEntity = b2cOnBoardingCommonUtility
				.getValuesfromEntityBasedOnOption(optionSelected, plantCode,
						profitCentre, sgstin, pan);
		if (getValuesfromEntity != null) {
			map.put(allComb, getValuesfromEntity);
		}
		return getValuesfromEntity;
	}

	private B2CQRAmtConfigEntity getMamValue(
			Map<String, B2CQRAmtConfigEntity> getMamValueMap, String pan) {

		if (getMamValueMap.containsKey(pan)) {
			return getMamValueMap.get(pan);
		}
		B2CQRAmtConfigEntity amtOnboardingEntity = b2CQRAmtConfigRepo
				.findByPanAndIsActiveTrue(pan);
		if (amtOnboardingEntity == null) {
			String errMsg = String.format(
					"No Data Available for Minimum Amount for the Pan %s Hence Generating 14 attribute URL",
					pan);
			LOGGER.error(errMsg);

		}
		getMamValueMap.put(pan, amtOnboardingEntity);

		return amtOnboardingEntity;
	}

	private B2COnBoardingConfigEntity getOnboardingQuestion(
			Map<String, B2COnBoardingConfigEntity> preferenceMap, String pan) {
		if (preferenceMap.containsKey(pan)) {
			return preferenceMap.get(pan);
		}
		List<B2COnBoardingConfigEntity> bcOnboardingEntity = b2COnBoardingConfigRepo
				.findByPanAndIsActiveTrue(pan);
		if (bcOnboardingEntity.isEmpty())
			return null;
		preferenceMap.put(pan, bcOnboardingEntity.get(0));
		return bcOnboardingEntity.get(0);
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
