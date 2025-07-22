package com.ey.advisory.app.data.services.pdfreader;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cells;
import com.aspose.cells.Font;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Style;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.admin.data.entities.master.PdfAuthInfoEntity;
import com.ey.advisory.admin.data.repositories.master.PdfAuthInfoEntityRepository;
import com.ey.advisory.app.data.entities.pdfreader.PDFResponseLineItemEntity;
import com.ey.advisory.app.data.entities.pdfreader.PDFResponseSummaryEntity;
import com.ey.advisory.app.data.entities.pdfreader.PDFUploadFileStatusEntity;
import com.ey.advisory.app.data.repositories.client.pdfreader.PDFResponseLineItemRepo;
import com.ey.advisory.app.data.repositories.client.pdfreader.PDFResponseSummaryRepo;
import com.ey.advisory.app.data.repositories.client.pdfreader.PDFUploadFileStatusRepo;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRPDFJSONResponseSummaryRepo;
import com.ey.advisory.app.data.services.pdfreader.dto.PDFCountDto;
import com.ey.advisory.app.data.services.pdfreader.dto.PDFReaderSummaryReportDto;
import com.ey.advisory.app.data.services.pdfreader.dto.PDFUploadFileStatusDto;
import com.ey.advisory.app.data.services.qrcodevalidator.QRCodeValidatorHelper;
import com.ey.advisory.app.data.services.qrcodevalidator.QRCommonUtility;
import com.ey.advisory.app.data.services.qrvspdf.QRvsPdfCommonUtility;
import com.ey.advisory.app.inward.einvoice.ReconJsonVsPdfUtility;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.QRCodeValidatorConstants;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.config.ConfigManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("PDFReaderServiceImpl")
public class PDFReaderServiceImpl implements PDFReaderService {

	private final static String Available = "Available";
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	@Autowired
	@Qualifier("PDFUploadFileStatusRepo")
	PDFUploadFileStatusRepo pdfUploadFileStatusRepo;

	@Autowired
	@Qualifier("PDFResponseSummaryRepo")
	PDFResponseSummaryRepo pdfRespSummRepo;

	@Autowired
	@Qualifier("PDFResponseLineItemRepo")
	PDFResponseLineItemRepo pdfRespLineItemRepo;

	@Autowired
	@Qualifier("QRPDFJSONResponseSummaryRepo")
	QRPDFJSONResponseSummaryRepo qrPdfJSONRespSummRepo;

	@Autowired
	@Qualifier("DefaultPdfFileUploadExtract")
	FileUploadExtractor fileUploadExtrac;

	@Autowired
	@Qualifier("GSTNHttpClient")
	private HttpClient httpClient;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	QRCodeValidatorHelper qrCodeValidHelper;

	@Autowired
	QRCommonUtility qrcommonUtility;

	@Autowired
	QRvsPdfCommonUtility qrvsPdfCommonUtility;

	@Autowired
	ReconJsonVsPdfUtility rconJsonPdfUtility;

	@Autowired
	PdfAuthInfoEntityRepository pdfAuthRepo;

	@Override
	public void saveAndPersistPdfSummary(Long id, String fileType) {
		String accessTokenPdf = null;
		List<PDFResponseSummaryEntity> pdflistofSumm = new ArrayList<>();
		List<PDFResponseLineItemEntity> pdflistItems = new ArrayList<>();
		LocalDateTime startTime = LocalDateTime.now();
		PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
				"QRCODEAPI_PopulateFileDtls_START",
				"QRCodeValidatorServiceImpl", "saveAndPersistQRReports",
				String.valueOf(id));

		try {

			List<PdfAuthInfoEntity> pdfEntityList = pdfAuthRepo.findAll();

			if (!pdfEntityList.isEmpty()) {

				LocalDateTime expiryTime = pdfEntityList.get(0)
						.getPdfTokenExpTime();
				LocalDateTime currentTime = LocalDateTime.now();
				Duration duration = Duration.between(currentTime, expiryTime);
				long hoursDifference = duration.toHours();

				LOGGER.debug("hoursDifference {} ", hoursDifference);

				LOGGER.debug("expiryTime {} ", expiryTime, currentTime);

				/*
				 * if (hoursDifference <= 1) {
				 */
				Map<String, Config> configMapPdf = configManager
						.getConfigs("QRVALIDATOR", "pdf.details", "DEFAULT");

				String usernamePdf = configMapPdf
						.get("pdf.details.username") == null ? ""
								: configMapPdf.get("pdf.details.username")
										.getValue();

				String passwordPdf = configMapPdf
						.get("pdf.details.password") == null ? ""
								: configMapPdf.get("pdf.details.password")
										.getValue();

				String apiAccesskeyPdf = configMapPdf
						.get("pdf.details.apiAccessKey") == null ? ""
								: configMapPdf.get("pdf.details.apiAccessKey")
										.getValue();

				String getAuthRespBodyPdf = qrcommonUtility.getPdfAccessToken(
						usernamePdf, passwordPdf, apiAccesskeyPdf);

				JsonObject requestObjectPdf = JsonParser
						.parseString(getAuthRespBodyPdf).getAsJsonObject();
				int statusCodePdf = requestObjectPdf.get("status").getAsInt();

				if (statusCodePdf == 1) {
					accessTokenPdf = requestObjectPdf.get("accessToken")
							.getAsString();
				} else {
					String errMsg = "Access Token is not Available from PDF.";
					LOGGER.error(errMsg);
					throw new AppException(errMsg);
				}

				pdfAuthRepo.updateIdToken(accessTokenPdf,
						pdfEntityList.get(0).getId(),
						LocalDateTime.now().plusHours(6), LocalDateTime.now());
				/*
				 * }else { accessTokenPdf = pdfEntityList.get(0).getIdToken(); }
				 */
			}

			//
			pdfUploadFileStatusRepo
					.updateFileStatus(QRCodeValidatorConstants.INPROGRESS, id);

			PDFCountDto countDto = new PDFCountDto();

			LOGGER.debug("File is Eligible for Processing and File Type is {} ",
					fileType);
			fileUploadExtrac.extractAndPopulatePDFData(fileType, pdflistofSumm,
					pdflistItems, id, countDto, accessTokenPdf);

			LOGGER.debug("pdflistofSumm {} ", pdflistofSumm);

			LOGGER.debug("pdflistofSumm size {} ", pdflistofSumm.size());

			if (!pdflistofSumm.isEmpty()) {

				List<PDFResponseLineItemEntity> lineItems = new ArrayList<>();

				List<PDFResponseSummaryEntity> savedEntities = pdfRespSummRepo
						.saveAll(pdflistofSumm);

				List<PDFResponseLineItemEntity> entityListItems = null;

				for (PDFResponseSummaryEntity entity : savedEntities) {
					LOGGER.debug("For loop start-- PDFResponseSummaryEntity");
					Long pdfID = entity.getId();
					LOGGER.debug("pdfID {} ", pdfID);
					entityListItems = entity.getPdflistItems();
					LOGGER.debug("entityListItems size {} ",
							entityListItems.size());
					if (entityListItems != null && !entityListItems.isEmpty()) {
						// Update each line item with the saved entity's ID and
						// collect them
						entityListItems.forEach(
								lineItem -> lineItem.setPdfResTblId(pdfID));
					}

					LOGGER.debug("entityListItems {} ", entityListItems);

					lineItems.addAll(entityListItems);

					LOGGER.debug("For loop end-- PDFResponseSummaryEntity");
				}
				if (lineItems != null && !lineItems.isEmpty()) {
					pdfRespLineItemRepo.saveAll(lineItems);
				}

				pdfUploadFileStatusRepo.updateFileStatus(
						QRCodeValidatorConstants.COMPLETED, id);

			} else {
				pdfUploadFileStatusRepo
						.updateFileStatus(QRCodeValidatorConstants.FAILED, id);
			}

			LocalDateTime endTime = LocalDateTime.now();
			pdfUploadFileStatusRepo.updateCount(countDto.getTotalDocCnt(),
					startTime, endTime, id);
		} catch (Exception ex) {
			pdfUploadFileStatusRepo
					.updateFileStatus(QRCodeValidatorConstants.FAILED, id);
			String msg = "Exception occured while validating QR File";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex.getMessage());
		} finally {
			PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
					"QRCODEAPI_PopulateFileDtls_END",
					"QRCodeValidatorServiceImpl", "saveAndPersistQRReports",
					String.valueOf(id));
		}

	}

	@Override
	public Workbook generatePdfReaderReport(
			List<PDFResponseSummaryEntity> pdfReaderList) {

		Workbook workbook = null;
		int startRow = 0;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		try {

			if (pdfReaderList != null && !pdfReaderList.isEmpty()) {
				startRow = 1;
				String[] invoiceHeaders = commonUtility
						.getProp("pdf.reader.summary.report.header").split(",");

				List<PDFReaderSummaryReportDto> finalList = new ArrayList<>();

				//PDFCountDto count = new PDFCountDto();

				for (int i = 0; i < pdfReaderList.size(); i++) {

					PDFResponseSummaryEntity pdfResponseSummaryEntity = pdfReaderList
							.get(i);

					getExcelRow(pdfResponseSummaryEntity, finalList, (i+1));

				}

				String fileName = "PDF_Reader_Report_Summary_Template.xlsx";
				workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates", fileName);
				if (LOGGER.isDebugEnabled()) {
					String msg = "QRValidate_Report_Summary_Template_.writeToExcel "
							+ "workbook created writing data to the workbook";
					LOGGER.debug(msg);
				}

				Worksheet sheet = workbook.getWorksheets().get(0);

				Cells reportCells = workbook.getWorksheets().get(0).getCells();

				sheet.setGridlinesVisible(false);

				Style style = workbook.createStyle();
				Font font = style.getFont();
				font.setBold(true);
				font.setSize(12);
				reportCells.importCustomObjects(finalList, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn,
						finalList.size(), true, "dd/MM/yyyy", false);

				if (LOGGER.isDebugEnabled()) {
					String msg = "PDF_Reader_Report_Summary_Template.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}
				int lastRowIndex = sheet.getCells().getMaxDataRow();
				sheet.getCells().deleteRow(lastRowIndex + 1);
				workbook.save(ConfigConstants.PDF_READER_SUMMARY,
						SaveFormat.XLSX);
				// QR Validate Summary Report_20240817T124414638.xlsx
				// QR Validate Summary Report.xlsx
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Workbook has been generated successfully with the"
									+ " error response list in the directory : %s",
							workbook.getAbsolutePath());
				}

			} else {
				throw new AppException(
						"No records found, cannot generate report");
			}
			return workbook;
		} catch (Exception e) {
			String msg = String.format(
					"Exception occured while "
							+ "saving excel sheet into folder, %s ",
					e.getMessage());
			LOGGER.error(msg, e);
			throw new AppException(e.getMessage(), e);
		}
	}

	private void getExcelRow(PDFResponseSummaryEntity entity,
			List<PDFReaderSummaryReportDto> finalList, int count) {
		Long id = entity.getId();

		List<PDFResponseLineItemEntity> lineItems = pdfRespLineItemRepo
				.findAllByPdfResTblId(id);
		PDFReaderSummaryReportDto dto = null;
		if (lineItems != null && lineItems.size() > 0) {
			for (PDFResponseLineItemEntity lineItemEntity : lineItems) {
				//count.incrementExcelRowCnt();
				dto = getPDFReaderSummaryReportDto(entity, lineItemEntity);
				//dto.setSINo(count.getExcelRowCnt());
				dto.setSINo(count);
				finalList.add(dto);
			}
		} else {
			//count.incrementExcelRowCnt();
			dto = getPDFReaderSummaryReportDto(entity, null);
			//dto.setSINo(count.getExcelRowCnt());
			dto.setSINo(count);
			finalList.add(dto);
		}
	}

	private PDFReaderSummaryReportDto getPDFReaderSummaryReportDto(
			PDFResponseSummaryEntity entity,
			PDFResponseLineItemEntity lineItemEntity) {

		PDFReaderSummaryReportDto dto = new PDFReaderSummaryReportDto();

		// dto.setSINo(count++);

		dto.setFileId(entity.getFileId());
		dto.setFileName(entity.getFileName());
		// Convert LocalDateTime to String
		dto.setValidatedDateTime(PDFCommonUtility.formattedDateOfUpload(entity.getValidatedDateTime()));
		dto.setIrnNumber(entity.getIrnNumber());
		dto.setIrnDate(entity.getIrnDate());
		dto.setDocType(entity.getDocType());
		dto.setDocNumber(entity.getDocNumber());
		dto.setDocDate(entity.getDocDate());
		dto.setRevChrgFlag(entity.getRevChrgFlag());

		dto.setSuppGstin(entity.getSuppGstin());
		dto.setSuppName(entity.getSuppName());
		dto.setSuppAddress(entity.getSuppAddress());
		dto.setSuppPincode(entity.getSuppPincode());
		dto.setSuppState(entity.getSuppState());
		dto.setSuppStateCode(entity.getSuppStateCode());

		dto.setCustGstin(entity.getCustGstin());
		dto.setCustName(entity.getCustName());
		dto.setCustAddress(entity.getCustAddress());
		dto.setCustPincode(entity.getCustPincode());
		dto.setCustState(entity.getCustState());
		dto.setCustStateCode(entity.getCustStateCode());

		dto.setBillingAddress(entity.getBillingAddress());

		dto.setShippingAddress(entity.getShippingAddress());
		dto.setBillingPos(entity.getBillingPos());

		dto.setInvTaxableAmt(entity.getInvTaxableAmt());

		dto.setIgstRate(entity.getIgstRate());
		dto.setIgstAmount(entity.getIgstAmount());

		dto.setCgstRate(entity.getCgstRate());
		dto.setCgstAmount(entity.getCgstAmount());

		dto.setSgstRate(entity.getSgstRate());
		dto.setSgstAmount(entity.getSgstAmount());

		dto.setUtgstRate(entity.getUtgstRate());
		dto.setUtgstAmount(entity.getUtgstAmount());

		dto.setCessRate(entity.getCessRate());
		dto.setCessAmount(entity.getCessAmount());
		dto.setTtlTax(entity.getTtlTax());

		dto.setInvValue(entity.getInvValue());

		dto.setPurchaseOrdNum(entity.getPurchaseOrdNum());

		// line item
		if (lineItemEntity != null) {
			dto.setItemNumber(lineItemEntity.getItemNumber());
			dto.setDescription(lineItemEntity.getDescription());
			dto.setHsnNumber(lineItemEntity.getHsnNumber());
			dto.setQuantity(lineItemEntity.getQuantity());
			dto.setUnitPrice(lineItemEntity.getUnitPrice());
			dto.setTaxableAmount(lineItemEntity.getTaxableAmount());
			dto.setUnit(lineItemEntity.getUnit());
			dto.setTaxRate(lineItemEntity.getTaxRate());
			dto.setTaxAmount(lineItemEntity.getTaxAmount());
			dto.setTtlAmount(lineItemEntity.getTtlAmount());
		}

		return dto;

	}

	@Override
	public List<PDFUploadFileStatusDto> fetchPdfUploadData(String entityIdApi) {
		try {

			Long entityId = Long.parseLong(entityIdApi);
			List<PDFUploadFileStatusEntity> fileStatusEntities = pdfUploadFileStatusRepo
					.findAllByEntityId(entityId);

			List<PDFUploadFileStatusDto> fileStatusData = fileStatusEntities
					.stream().map(entity -> {
						PDFUploadFileStatusDto dto = new PDFUploadFileStatusDto();
						dto.setId(entity.getId());
						dto.setUploadedBy(entity.getUploadedBy());
						dto.setDateOfUpload(entity.getDateOfUpload());
						dto.setFileName(entity.getFileName());
						dto.setFileStatus(entity.getFileStatus());
						dto.setFilePath(entity.getFilePath());
						dto.setTotUplDoc(entity.getTotUplDoc());
						dto.setCreatedBy(entity.getCreatedBy());

						// Convert LocalDateTime to String
						dto.setCreatedOn(QRCommonUtility
								.formattedDateOfUpload(entity.getCreatedOn()));
						dto.setUpdatedOn(entity.getUpdatedOn());
						dto.setStartTime(entity.getStartTime());
						dto.setEndTime(entity.getEndTime());

						dto.setUpdatedBy(entity.getUpdatedBy());
						dto.setSource(entity.getSource());
						dto.setDocId(entity.getDocId());
						dto.setEntityId(entity.getEntityId());

						return dto;
					}).collect(Collectors.toList());

			if (fileStatusData.isEmpty()) {
				String errMsg = String
						.format("No Data Available in PDFFileStatusData");
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}

			return fileStatusData;
		} catch (Exception e) {
			String errMsg = String.format(
					"Exception occurred while fetching the data from PDFFileStatusData");
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg);
		}
	}

}
