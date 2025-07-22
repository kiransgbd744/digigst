package com.ey.advisory.app.data.services.qrcodevalidator;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.HttpClient;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cells;
import com.aspose.cells.Font;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Style;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.entities.master.PdfAuthInfoEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.GroupConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.master.PdfAuthInfoEntityRepository;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRCodeFileDetailsEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRInwardEinvoiceTaggingEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRPDFJSONResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRPDFResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRResponseLogEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRUploadFileStatusDTO;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRUploadFileStatusEntity;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRInwardEinvoiceTaggingRepository;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRPDFJSONResponseSummaryRepo;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRPDFResponseSummaryRepo;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRResponseLogRepo;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRResponseSummaryRepo;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRUploadFileStatusRepo;
import com.ey.advisory.app.data.services.qrvspdf.QRvsPdfCommonUtility;
import com.ey.advisory.app.data.services.qrvspdf.QrvsPdfValidatorFinalRespDto;
import com.ey.advisory.app.inward.einvoice.JsonPdfValidatorFinalRespDto;
import com.ey.advisory.app.inward.einvoice.ReconJsonVsPdfUtility;
import com.ey.advisory.app.sftp.service.SFTPFileTransferService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.QRCodeValidatorConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Component("QRCodeValidatorServiceImpl")
@Slf4j
public class QRCodeValidatorServiceImpl implements QRCodeValidatorService {

	private final static String Available = "Available";
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	@Autowired
	@Qualifier("QRUploadFileStatusRepo")
	QRUploadFileStatusRepo qrUploadFileStatusRepo;

	@Autowired
	@Qualifier("QRResponseSummaryRepo")
	QRResponseSummaryRepo qrRespSummRepo;

	@Autowired
	@Qualifier("QRPDFResponseSummaryRepo")
	QRPDFResponseSummaryRepo qrPdfRespSummRepo;

	@Autowired
	@Qualifier("QRPDFJSONResponseSummaryRepo")
	QRPDFJSONResponseSummaryRepo qrPdfJSONRespSummRepo;

	@Autowired
	@Qualifier("DefaultQRFileUploadExtract")
	QRFileUploadExtractor qrFileUploadExtrac;

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

	@Autowired
	private QRInwardEinvoiceTaggingRepository qrInwardEinvoiceTaggingRepository;

	@Autowired
	@Qualifier("GroupConfigPrmtRepository")
	private GroupConfigPrmtRepository groupConfigPemtRepo;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailsRepo;

	@Autowired
	private QRCodeValidatorService qrCodeValidatorService;

	@Autowired
	@Qualifier("QRCodeValidatorServiceImpl")
	QRCodeValidatorService qrCodeService;

	@Autowired
	@Qualifier("SFTPFileTransferServiceImpl")
	private SFTPFileTransferService sftpService;

	@Autowired
	private QRResponseLogRepo respLogRep;

	@Override
	public void saveAndPersistQRReports(Long id, String fileType,
			String optedAns, String uploadType, String entityId) {
		boolean isEligibleToExtract = false;
		String accessToken = null;
		String accessTokenPdf = null;
		List<QRResponseSummaryEntity> qrListofSumm = new ArrayList<>();
		List<QRPDFResponseSummaryEntity> qrPDFlistofSumm = new ArrayList<>();
		List<QRPDFJSONResponseSummaryEntity> qrPDFJsonlistofSumm = new ArrayList<>();
		LocalDateTime startTime = LocalDateTime.now();
		PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
				"QRCODEAPI_PopulateFileDtls_START",
				"QRCodeValidatorServiceImpl", "saveAndPersistQRReports",
				String.valueOf(id));

		try {
			Map<String, Config> configMap = configManager
					.getConfigs("QRVALIDATOR", "qr.details", "DEFAULT");

			String username = configMap.get("qr.details.username") == null ? ""
					: configMap.get("qr.details.username").getValue();

			String password = configMap.get("qr.details.password") == null ? ""
					: configMap.get("qr.details.password").getValue();

			String apiAccesskey = configMap
					.get("qr.details.apiAccessKey") == null ? ""
							: configMap.get("qr.details.apiAccessKey")
									.getValue();

			String getAuthRespBody = qrcommonUtility.getAccessToken(username,
					password, apiAccesskey);

			String qrValidUrl = configMap.get("qr.details.validatorUrl") == null
					? "" : configMap.get("qr.details.validatorUrl").getValue();

			PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
					"QRCODEAPI_PopulateAccessToken_START",
					"QRCodeValidatorServiceImpl", "saveAndPersistQRReports",
					String.valueOf(id));

			PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
					"QRCODEAPI_PopulateAccessToken_END",
					"QRCodeValidatorServiceImpl", "saveAndPersistQRReports",
					String.valueOf(id));

			JsonObject requestObject = JsonParser.parseString(getAuthRespBody)
					.getAsJsonObject();
			int statusCode = requestObject.get("status").getAsInt();

			LOGGER.debug("Option Opted {} ", optedAns);
			if (statusCode == 1) {
				isEligibleToExtract = true;
				accessToken = requestObject.get("accessToken").getAsString();
			} else {
				String msg = "Access Token is not Available";
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			if (!optedAns.equalsIgnoreCase("A"))
			// pdf access token api
			{
				List<PdfAuthInfoEntity> pdfEntityList = pdfAuthRepo.findAll();

				if (!pdfEntityList.isEmpty()) {

					LocalDateTime expiryTime = pdfEntityList.get(0)
							.getPdfTokenExpTime();
					LocalDateTime currentTime = LocalDateTime.now();
					Duration duration = Duration.between(currentTime,
							expiryTime);
					long hoursDifference = duration.toHours();

					LOGGER.debug("hoursDifference {} ", hoursDifference);

					LOGGER.debug("expiryTime {} ", expiryTime, currentTime);

					/*
					 * if (hoursDifference <= 1) {
					 */
					Map<String, Config> configMapPdf = configManager.getConfigs(
							"QRVALIDATOR", "pdf.details", "DEFAULT");

					String usernamePdf = configMapPdf
							.get("pdf.details.username") == null ? ""
									: configMapPdf.get("pdf.details.username")
											.getValue();

					String passwordPdf = configMapPdf
							.get("pdf.details.password") == null ? ""
									: configMapPdf.get("pdf.details.password")
											.getValue();

					String apiAccesskeyPdf = configMapPdf
							.get("pdf.details.apiAccessKey") == null
									? ""
									: configMapPdf
											.get("pdf.details.apiAccessKey")
											.getValue();

					String getAuthRespBodyPdf = qrcommonUtility
							.getPdfAccessToken(usernamePdf, passwordPdf,
									apiAccesskeyPdf);

					JsonObject requestObjectPdf = JsonParser
							.parseString(getAuthRespBodyPdf).getAsJsonObject();
					int statusCodePdf = requestObjectPdf.get("status")
							.getAsInt();

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
							LocalDateTime.now().plusHours(6),
							LocalDateTime.now());
					/*
					 * }else { accessTokenPdf =
					 * pdfEntityList.get(0).getIdToken(); }
					 */
				}
			}
			qrUploadFileStatusRepo
					.updateFileStatus(QRCodeValidatorConstants.INPROGRESS, id);
			Optional<QRUploadFileStatusEntity> qrFileStatus = qrUploadFileStatusRepo
					.findById(id);

			String filePath = qrFileStatus.get().getFilePath();
			QRCountDto countDto = new QRCountDto();
			if (optedAns.equalsIgnoreCase("A")) {
				if (isEligibleToExtract) {
					LOGGER.debug(
							"File is Eligible for Processing and File Type is {} ",
							fileType);
					qrFileUploadExtrac.extractAndPopulateQRData(filePath,
							fileType, qrListofSumm, accessToken, apiAccesskey,
							id, countDto, qrFileStatus.get().getDocId(),
							qrValidUrl, uploadType, entityId);
				}
				if (!qrListofSumm.isEmpty()) {
					setMatchandMisMatchCount(qrListofSumm);
					qrRespSummRepo.saveAll(qrListofSumm);
					List<String> irnList = new ArrayList<>();
					List<QRInwardEinvoiceTaggingEntity> qrInwrdTag = new ArrayList<>();

					for (QRResponseSummaryEntity qrList : qrListofSumm) {
						irnList.add(qrList.getIrn());
						qrInwardEinvoiceTaggingRepository.updateIsDeleteStatus(
								Arrays.asList(qrList.getIrn()));
						qrInwrdTag.add(convertToTaggEntity(qrList, "Basic"));
						qrInwardEinvoiceTaggingRepository
								.save(convertToTaggEntity(qrList, "Basic"));
						if (!uploadType.equalsIgnoreCase("webUpload")) {
							qrUploadFileStatusRepo
									.updateEntityId(qrList.getEntityId(), id);
							LOGGER.debug(
									"In QRResponseSummaryEntity, Uploaded By {}",
									uploadType);
						}
					}

					qrUploadFileStatusRepo.updateFileStatus(
							QRCodeValidatorConstants.COMPLETED, id);
				} else {
					qrUploadFileStatusRepo.updateFileStatus(
							QRCodeValidatorConstants.FAILED, id);
				}
			} else if (optedAns.equalsIgnoreCase("B")) {
				if (isEligibleToExtract) {
					LOGGER.debug(
							"File is Eligible for Processing and File Type is {} ",
							fileType);
					qrFileUploadExtrac.extractAndPopulateQRPDFData(fileType,
							qrPDFlistofSumm, accessToken, apiAccesskey, id,
							countDto, qrValidUrl, accessTokenPdf, uploadType,
							entityId);
				}
				if (!qrPDFlistofSumm.isEmpty()) {
					for (QRPDFResponseSummaryEntity qrPdfList : qrPDFlistofSumm) {
						if (!uploadType.equalsIgnoreCase("webUpload")) {
							qrUploadFileStatusRepo.updateEntityId(
									qrPdfList.getEntityId(), id);
							LOGGER.debug(
									"In QRPDFResponseSummaryEntity, Uploaded By {}",
									uploadType);
						}
					}
					qrPdfRespSummRepo.saveAll(qrPDFlistofSumm);
					qrUploadFileStatusRepo.updateFileStatus(
							QRCodeValidatorConstants.COMPLETED, id);

				} else {
					qrUploadFileStatusRepo.updateFileStatus(
							QRCodeValidatorConstants.FAILED, id);
				}
			} else if (optedAns.equalsIgnoreCase("C")) {
				if (isEligibleToExtract) {
					LOGGER.debug(
							"File is Eligible for Processing and File Type is {} and Option Opted is {} ",
							fileType, optedAns);
					qrFileUploadExtrac.extractAndPopulateQRPDFJsonData(fileType,
							qrPDFJsonlistofSumm, accessToken, apiAccesskey, id,
							countDto, qrValidUrl, accessTokenPdf, uploadType,
							entityId);
				}
				if (!qrPDFJsonlistofSumm.isEmpty()) {
					for (QRPDFJSONResponseSummaryEntity qrPdfJsonList : qrPDFJsonlistofSumm) {
						if (!uploadType.equalsIgnoreCase("webUpload")) {
							qrUploadFileStatusRepo.updateEntityId(
									qrPdfJsonList.getEntityId(), id);
							LOGGER.debug(
									"In QRPDFJSONResponseSummaryEntity, Uploaded By {}",
									uploadType);
						}
					}
					qrPdfJSONRespSummRepo.saveAll(qrPDFJsonlistofSumm);
					qrUploadFileStatusRepo.updateFileStatus(
							QRCodeValidatorConstants.COMPLETED, id);

				} else {
					qrUploadFileStatusRepo.updateFileStatus(
							QRCodeValidatorConstants.FAILED, id);
				}
			} else {
				String errMsg = String.format(
						"Invalid Option Selected %s for File ID %s", optedAns,
						id);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			LocalDateTime endTime = LocalDateTime.now();
			qrUploadFileStatusRepo.updateCount(countDto.getTotalDocCnt(),
					countDto.getQrCnt(), countDto.getSigMisCnt(),
					countDto.getFullMatCnt(), countDto.getErrCnt(), startTime,
					endTime, id);
		} catch (Exception ex) {
			qrUploadFileStatusRepo
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

	private QRInwardEinvoiceTaggingEntity convertToTaggEntity(
			QRResponseSummaryEntity qrList, String mode) {

		QRInwardEinvoiceTaggingEntity taggEntity = new QRInwardEinvoiceTaggingEntity();
		taggEntity.setIrn(qrList.getIrn());
		taggEntity.setQrCodeValidated(
				!Strings.isNullOrEmpty(qrList.getIrn()) ? "Yes" : "No");
		taggEntity.setQrCodeMatchCount(qrList.getMatchCount());
		taggEntity.setQrCodeMismatchCount(qrList.getMismatchCount());
		taggEntity.setIsTagged(false);
		if (qrList.getMismatchCount() != null) {
			taggEntity.setQrCodeValidationResult(
					(qrList.getMismatchCount() == 0) ? "Match" : "Mismatch");
		}
		taggEntity.setMode(mode);
		taggEntity.setQrCodeMismatchAttributes(qrList.getMisMatAtt());
		taggEntity.setIsDelete(false);
		taggEntity.setCreatedOn(LocalDateTime.now());

		return taggEntity;

	}

	@Override
	public Workbook generateQrReport(List<QRResponseSummaryEntity> retList,
			List<QRPDFResponseSummaryEntity> qrPdfSummEntity,
			List<QRPDFJSONResponseSummaryEntity> qrPdfJsonRespSummList) {

		Workbook workbook = null;
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		try {
			if (retList != null && !retList.isEmpty()) {
				String[] invoiceHeaders = commonUtility
						.getProp("qr.validate.summary.report.header")
						.split(",");

				for (int i = 0; i < retList.size(); i++) {
					retList.get(i).setSINo(i + 1);
				}

				String fileName = "QRValidate_Report_Summary_Template.xlsx";
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
				setMatchandMisMatchCount(retList);

				reportCells.importCustomObjects(retList, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn, retList.size(),
						true, "dd/MM/yyyy", false);
				if (LOGGER.isDebugEnabled()) {
					String msg = "QRValidate_Report_Summary_Template.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}
				int lastRowIndex = sheet.getCells().getMaxDataRow();
				sheet.getCells().deleteRow(lastRowIndex + 1);
				workbook.save(ConfigConstants.QRVALIDATESUMMARY,
						SaveFormat.XLSX);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Workbook has been generated successfully with the"
									+ " error response list in the directory : %s",
							workbook.getAbsolutePath());
				}
			} else if (qrPdfSummEntity != null && !qrPdfSummEntity.isEmpty()) {
				startRow = 1;
				String[] invoiceHeaders = commonUtility
						.getProp("qr.pdf.validate.summary.report.header")
						.split(",");

				for (int i = 0; i < qrPdfSummEntity.size(); i++) {
					qrPdfSummEntity.get(i).setSINo(i + 1);
					qrPdfSummEntity.get(i)
							.setIrnDatePDF(EYDateUtil.fmtDateOnly(
									qrPdfSummEntity.get(i).getIrnDatePDF(),
									DateUtil.SUPPORTED_DATE_FORMAT1,
									DateUtil.SUPPORTED_DATE_FORMAT4));
					qrPdfSummEntity.get(i)
							.setIrnDateQR(EYDateUtil.fmtDateOnly(
									qrPdfSummEntity.get(i).getIrnDateQR(),
									DateUtil.SUPPORTED_DATE_FORMAT1,
									DateUtil.SUPPORTED_DATE_FORMAT4));
				}

				String fileName = "QRPDFValidate_Report_Summary_Template.xlsx";
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
				reportCells.importCustomObjects(qrPdfSummEntity, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn,
						qrPdfSummEntity.size(), true, "dd/MM/yyyy", false);

				if (LOGGER.isDebugEnabled()) {
					String msg = "QRValidate_Report_Summary_Template.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}
				int lastRowIndex = sheet.getCells().getMaxDataRow();
				sheet.getCells().deleteRow(lastRowIndex + 1);
				workbook.save(ConfigConstants.QRVALIDATESUMMARY,
						SaveFormat.XLSX);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Workbook has been generated successfully with the"
									+ " error response list in the directory : %s",
							workbook.getAbsolutePath());
				}

			} else if (qrPdfJsonRespSummList != null
					&& !qrPdfJsonRespSummList.isEmpty()) {
				startRow = 1;
				String[] invoiceHeaders = commonUtility
						.getProp("qr.pdf.json.validate.summary.report.header")
						.split(",");

				for (int i = 0; i < qrPdfJsonRespSummList.size(); i++) {
					qrPdfJsonRespSummList.get(i).setSINo(i + 1);
					qrPdfJsonRespSummList.get(i)
							.setIrnDateJson(EYDateUtil.fmtDateOnly(
									qrPdfJsonRespSummList.get(i)
											.getIrnDateJson(),
									DateUtil.SUPPORTED_DATE_FORMAT1,
									DateUtil.SUPPORTED_DATE_FORMAT4));

				}

				String fileName = "QRPDFJSONValidate_Report_Summary_Template.xlsx";
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
				LOGGER.debug("qrPdfJsonRespSummList {} ",
						qrPdfJsonRespSummList);
				reportCells.importCustomObjects(qrPdfJsonRespSummList,
						invoiceHeaders, isHeaderRequired, startRow, startcolumn,
						qrPdfJsonRespSummList.size(), true, "dd/MM/yyyy",
						false);

				if (LOGGER.isDebugEnabled()) {
					String msg = "QRValidate_Report_Summary_Template.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}
				int lastRowIndex = sheet.getCells().getMaxDataRow();
				sheet.getCells().deleteRow(lastRowIndex + 1);
				workbook.save(ConfigConstants.QRVALIDATESUMMARY,
						SaveFormat.XLSX);
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

	@Override
	public Workbook generateErroredQrReport(
			List<QRCodeFileDetailsEntity> retList) {

		Workbook workbook = null;
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		try {
			if (retList != null && !retList.isEmpty()) {
				String[] invoiceHeaders = commonUtility
						.getProp("qr.validate.summary.error.report.header")
						.split(",");

				for (int i = 0; i < retList.size(); i++) {
					retList.get(i).setSINo(i + 1);
				}

				String fileName = "QRValidator_Error_FileName.xlsx";
				workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates", fileName);
				if (LOGGER.isDebugEnabled()) {
					String msg = "ErroredOutInv_Error_FileName.writeToExcel "
							+ "workbook created writing data to the workbook";
					LOGGER.debug(msg);
				}

				Worksheet worksheet = workbook.getWorksheets().get(0);

				Cells reportCells = workbook.getWorksheets().get(0).getCells();

				Style style = workbook.createStyle();

				Font font = style.getFont();
				font.setBold(true);
				font.setSize(12);

				reportCells.importCustomObjects(retList, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn, retList.size(),
						true, "dd/MM/yyyy", false);

				if (LOGGER.isDebugEnabled()) {
					String msg = "ErroredOutInv_Error_FileName.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}
				int lastRowIndex = worksheet.getCells().getMaxDataRow();
				worksheet.getCells().deleteRow(lastRowIndex + 1);
				workbook.save(ConfigConstants.QRVALIDATESUMMARY,
						SaveFormat.XLSX);
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

	@Override
	public Pair<List<QRUploadFileStatusDTO>, Integer> fetchQrUploadData(
			Long entityId, Pageable pageReq) {

		int totalCount = 0;

		try {
			// long entityId = Long.parseLong(entityIdApi);
			List<QRUploadFileStatusEntity> fileStatusEntities = qrUploadFileStatusRepo
					.findByEntityIdOrEntityIdIsNull(entityId, pageReq);

			totalCount = qrUploadFileStatusRepo
					.countByEntityIdOrEntityIdIsNull(entityId);

			List<QRUploadFileStatusDTO> fileStatusData = fileStatusEntities
					.stream().map(entity -> {
						QRUploadFileStatusDTO dto = new QRUploadFileStatusDTO();
						dto.setId(entity.getId());
						dto.setUploadedBy(entity.getUploadedBy());
						dto.setDateOfUpload(entity.getDateOfUpload());
						dto.setFileName(entity.getFileName());
						dto.setFileStatus(entity.getFileStatus());
						dto.setFilePath(entity.getFilePath());
						dto.setTotUplDoc(entity.getTotUplDoc());
						dto.setProcQr(entity.getProcQr());
						dto.setSigMisMat(entity.getSigMisMat());
						dto.setFullMatch(entity.getFullMatch());
						dto.setErrMatch(entity.getErrMatch());
						dto.setCountOfMisMat(entity.getCountOfMisMat());
						dto.setCreatedBy(entity.getCreatedBy());

						// Convert LocalDateTime to String
						dto.setCreatedOn(QRCommonUtility
								.formattedDateOfUpload(entity.getCreatedOn()));
						dto.setUpdatedOn(entity.getUpdatedOn());
						dto.setStartTime(entity.getStartTime());
						dto.setEndTime(entity.getEndTime());

						dto.setUpdatedBy(entity.getUpdatedBy());
						dto.setSource(entity.getSource());
						dto.setIsReverseInt(entity.getIsReverseInt());
						dto.setDocId(entity.getDocId());
						dto.setOptionOpted(entity.getOptionOpted());
						dto.setEntityId(entity.getEntityId());

						String groupCode = TenantContext.getTenantId();
						if (!Strings.isNullOrEmpty(groupCode)) {
							String answerDesc = groupConfigPemtRepo
									.findAnswerForQRCodeValidator(groupCode);
							dto.setFlagAnswer(QRCommonUtility.optionDescMap()
									.get(answerDesc));
						}
						dto.setFeature(QRCommonUtility.optionDescMap()
								.get(entity.getOptionOpted()));

						return dto;
					}).collect(Collectors.toList());
			if (fileStatusData.isEmpty()) {
				String errMsg = String
						.format("No Data Available in QRFileStatusData");
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}

			return new Pair<>(fileStatusData, totalCount);
			// return fileStatusData;
		} catch (Exception e) {
			String errMsg = String.format(
					"Exception occurred while fetching the data from QRFileStatusData");
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg);
		}
	}

	@Override
	public QrUploadStatsDto getStatsDetails(
			List<QRResponseSummaryEntity> summList,
			List<QRPDFResponseSummaryEntity> qrRespSummList,
			List<QRPDFJSONResponseSummaryEntity> qrPdfJsonRespSummList) {
		QrUploadStatsDto uploadStats = new QrUploadStatsDto();
		List<QRMisMatchCount> misMatchDtoList = new ArrayList<>();
		try {

			if (summList != null && !summList.isEmpty()) {
				uploadStats.setUploadCounts(
						qrCodeValidHelper.extractCountDetails(summList, null));
				countDetails(summList, null, null, misMatchDtoList,
						uploadStats);
			} else if (qrRespSummList != null && !qrRespSummList.isEmpty()) {
				uploadStats.setUploadCounts(qrCodeValidHelper
						.extractCountDetails(qrRespSummList, null));
				countDetails(null, qrRespSummList, null, misMatchDtoList,
						uploadStats);
			} else if (qrPdfJsonRespSummList != null
					&& !qrPdfJsonRespSummList.isEmpty()) {
				uploadStats.setUploadCounts(qrCodeValidHelper
						.extractCountDetails(qrPdfJsonRespSummList, null));
				countDetails(null, null, qrPdfJsonRespSummList, misMatchDtoList,
						uploadStats);
			} else {
				LOGGER.error("Invalid Option Opted.");
			}
			return uploadStats;
		} catch (Exception e) {
			String errMsg = String
					.format("Exception occurred while fetching the data");
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg);
		}
	}

	@Override
	public QrUploadStatsDto getConsStatsDetails(
			List<QRResponseSummaryEntity> summList,
			List<QRPDFResponseSummaryEntity> qrPdfSummList,
			List<QRPDFJSONResponseSummaryEntity> qrPdfJsonRespSummList,
			QRSearchParams qrSearchParams) {
		QrUploadStatsDto uploadStats = new QrUploadStatsDto();
		List<QRMisMatchCount> misMatchDtoList = new ArrayList<>();
		try {
			if (summList != null && !summList.isEmpty()) {
				uploadStats.setUploadCounts(qrCodeValidHelper
						.extractCountDetails(summList, qrSearchParams));
				countDetails(summList, null, null, misMatchDtoList,
						uploadStats);
				return uploadStats;
			} else if (qrPdfSummList != null && !qrPdfSummList.isEmpty()) {
				uploadStats.setUploadCounts(qrCodeValidHelper
						.extractCountDetails(qrPdfSummList, qrSearchParams));
				countDetails(null, qrPdfSummList, null, misMatchDtoList,
						uploadStats);
				return uploadStats;
			} else if (qrPdfJsonRespSummList != null
					&& !qrPdfJsonRespSummList.isEmpty()) {
				uploadStats
						.setUploadCounts(qrCodeValidHelper.extractCountDetails(
								qrPdfJsonRespSummList, qrSearchParams));
				countDetails(null, null, qrPdfJsonRespSummList, misMatchDtoList,
						uploadStats);
				return uploadStats;
			} else {
				LOGGER.error("Invalid Option Opted.");

				return null;

			}
		} catch (Exception e) {
			String errMsg = String
					.format("Exception occurred while fetching the data");
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg);
		}
	}

	private void countDetails(List<QRResponseSummaryEntity> summList,
			List<QRPDFResponseSummaryEntity> qrRespSummList,
			List<QRPDFJSONResponseSummaryEntity> qrPdfJsonRespSummList,
			List<QRMisMatchCount> misMatchDtoList,
			QrUploadStatsDto uploadStats) {

		if (summList != null && !summList.isEmpty()) {
			setMatchandMisMatchCount(summList);
			// uploadStats.setQrSummaryData(summList);
			uploadStats.setGstnStats(
					qrCodeValidHelper.extractGstnPanWiseStats(summList, true));
			uploadStats.setPanStats(
					qrCodeValidHelper.extractGstnPanWiseStats(summList, false));
			qrCodeValidHelper.extractMisMatchDetails(misMatchDtoList, summList);
			uploadStats.setMisMatCounts(misMatchDtoList);

		} else if (qrRespSummList != null && !qrRespSummList.isEmpty()) {

			uploadStats.setGstnStats(qrCodeValidHelper
					.extractGstnPanWiseStatsQrPDF(qrRespSummList, true));
			uploadStats.setPanStats(qrCodeValidHelper
					.extractGstnPanWiseStatsQrPDF(qrRespSummList, false));
			qrCodeValidHelper.extractMisMatchDetailsQrPdf(misMatchDtoList,
					qrRespSummList);
			uploadStats.setMisMatCounts(misMatchDtoList);

		}

		else if (qrPdfJsonRespSummList != null
				&& !qrPdfJsonRespSummList.isEmpty()) {

			uploadStats.setGstnStats(
					qrCodeValidHelper.extractGstnPanWiseStatsQrPDFJson(
							qrPdfJsonRespSummList, true));
			uploadStats.setPanStats(
					qrCodeValidHelper.extractGstnPanWiseStatsQrPDFJson(
							qrPdfJsonRespSummList, false));
			qrCodeValidHelper.extractMisMatchDetailsQrPdfJson(misMatchDtoList,
					qrPdfJsonRespSummList);
			uploadStats.setMisMatCounts(misMatchDtoList);

		} else {
			LOGGER.error("Invalid Option Opted.");
		}
	}

	@Override
	public List<QRResponseSummaryEntity> getConsFilterList(
			QRSearchParams qrSearchParams) {

		List<QRResponseSummaryEntity> respEntity = qrRespSummRepo
				.findConsolidatedDataByParams(qrSearchParams);

		return respEntity;

	}

	@Override
	public List<QRPDFResponseSummaryEntity> getConsFilterListqrPDF(
			QRSearchParams qrSearchParams) {

		List<QRPDFResponseSummaryEntity> respEntity = qrPdfRespSummRepo
				.findConsolidatedDataByParams(qrSearchParams);
		return respEntity;
	}

	@Override
	public List<QRPDFJSONResponseSummaryEntity> getConsFilterListqrPDFJson(
			QRSearchParams qrSearchParams) {

		List<QRPDFJSONResponseSummaryEntity> respEntity = qrPdfJSONRespSummRepo
				.findConsolidatedDataByParams(qrSearchParams);
		return respEntity;
	}

	@Override
	public Pair<List<QRResponseSummaryEntity>, Integer> getConsFilterTableList(
			QRSearchParams qrSearchParams, int pageSize, int pageNum) {

		int recordsToStart = pageNum;

		int noOfRowstoFetch = pageSize;

		Pageable pageReq = PageRequest.of(recordsToStart, noOfRowstoFetch,
				Direction.DESC, "id");

		Page<QRResponseSummaryEntity> pageResult = qrRespSummRepo
				.findConsolidatedDataByParams(qrSearchParams, pageReq);

		List<QRResponseSummaryEntity> totalSize = qrRespSummRepo
				.findConsolidatedDataByParams(qrSearchParams);

		List<QRResponseSummaryEntity> finalTableList = pageResult.getContent();

		if (!finalTableList.isEmpty())
			for (int i = 0; i < finalTableList.size(); i++) {
				finalTableList.get(i)
						.setTempFileName(finalTableList.get(i).getFileName());
			}

		return new Pair<List<QRResponseSummaryEntity>, Integer>(finalTableList,
				totalSize.size());
	}

	@Override
	public Pair<List<QRPDFResponseSummaryEntity>, Integer> getConsFilterTableListqrPdf(
			QRSearchParams qrSearchParams, int pageSize, int pageNum) {

		int recordsToStart = pageNum;

		int noOfRowstoFetch = pageSize;

		Pageable pageReq = PageRequest.of(recordsToStart, noOfRowstoFetch,
				Direction.DESC, "id");

		Page<QRPDFResponseSummaryEntity> pageResult = qrPdfRespSummRepo
				.findConsolidatedDataByParams(qrSearchParams, pageReq);

		List<QRPDFResponseSummaryEntity> totalSize = qrPdfRespSummRepo
				.findConsolidatedDataByParams(qrSearchParams);

		List<QRPDFResponseSummaryEntity> finalTableList = pageResult
				.getContent();

		if (!finalTableList.isEmpty())
			for (int i = 0; i < finalTableList.size(); i++) {
				finalTableList.get(i)
						.setTempFileName(finalTableList.get(i).getFileName());
			}

		return new Pair<List<QRPDFResponseSummaryEntity>, Integer>(
				finalTableList, totalSize.size());
	}

	@Override
	public Pair<List<QRPDFJSONResponseSummaryEntity>, Integer> getConsFilterTableListqrPdfJson(
			QRSearchParams qrSearchParams, int pageSize, int pageNum) {

		int recordsToStart = pageNum;

		int noOfRowstoFetch = pageSize;

		Pageable pageReq = PageRequest.of(recordsToStart, noOfRowstoFetch,
				Direction.DESC, "id");

		Page<QRPDFJSONResponseSummaryEntity> pageResult = qrPdfJSONRespSummRepo
				.findConsolidatedDataByParams(qrSearchParams, pageReq);

		List<QRPDFJSONResponseSummaryEntity> totalSize = qrPdfJSONRespSummRepo
				.findConsolidatedDataByParams(qrSearchParams);

		List<QRPDFJSONResponseSummaryEntity> finalTableList = pageResult
				.getContent();

		if (!finalTableList.isEmpty())
			for (int i = 0; i < finalTableList.size(); i++) {
				finalTableList.get(i)
						.setTempFileName(finalTableList.get(i).getFileName());
			}

		return new Pair<List<QRPDFJSONResponseSummaryEntity>, Integer>(
				finalTableList, totalSize.size());
	}

	@Override
	public List<QRGstinDto> getListOfRecipientGstin(String entityId) {
		String optedAns = qrcommonUtility.optedOption(Long.valueOf(entityId));
		List<String> recipientGstinString = new ArrayList<>();
		if (optedAns.equalsIgnoreCase("A")) {
			recipientGstinString = qrRespSummRepo.findAllBuyerGstins();
		} else if (optedAns.equalsIgnoreCase("B")) {
			recipientGstinString = qrPdfRespSummRepo.findAllBuyerGstins();
		} else if (optedAns.equalsIgnoreCase("C")) {
			recipientGstinString = qrPdfJSONRespSummRepo.findAllBuyerGstins();
		} else {
			LOGGER.error("Invalid Option Opted.");
		}

		recipientGstinString.removeIf(Objects::isNull);
		Collections.sort(recipientGstinString);
		List<QRGstinDto> recipientGstinList = recipientGstinString.stream()
				.map(e -> convertToDto(e))
				.collect(Collectors.toCollection(ArrayList::new));
		return recipientGstinList;
	}

	@Override
	public List<QRGstinDto> getListOfQRVendorPan(String entityId) {
		String optedAns = qrcommonUtility.optedOption(Long.valueOf(entityId));
		List<String> vendorStr = new ArrayList<>();
		if (optedAns.equalsIgnoreCase("A")) {
			vendorStr = qrRespSummRepo.findAllSellerPans();
		} else if (optedAns.equalsIgnoreCase("B")) {
			vendorStr = qrPdfRespSummRepo.findAllSellerPans();
		} else if (optedAns.equalsIgnoreCase("C")) {
			vendorStr = qrPdfJSONRespSummRepo.findAllSellerPans();
		} else {
			LOGGER.error("Invalid Option Opted.");
		}
		vendorStr.removeIf(Objects::isNull);
		Collections.sort(vendorStr);
		List<QRGstinDto> vendorPanList = vendorStr.stream()
				.map(e -> convertToDto(e))
				.collect(Collectors.toCollection(ArrayList::new));
		return vendorPanList;
	}

	@Override
	public List<QRGstinDto> getListOfVendorGstin(List<String> sellerPan,
			String entityId) {
		String optedAns = qrcommonUtility.optedOption(Long.valueOf(entityId));
		List<String> vendorGstinStr = new ArrayList<>();
		if (optedAns.equalsIgnoreCase("A")) {
			vendorGstinStr = qrRespSummRepo
					.findAllVendorGstinByVendorPans(sellerPan);
		} else if (optedAns.equalsIgnoreCase("B")) {
			vendorGstinStr = qrPdfRespSummRepo
					.findAllVendorGstinByVendorPans(sellerPan);
		} else if (optedAns.equalsIgnoreCase("C")) {
			vendorGstinStr = qrPdfJSONRespSummRepo
					.findAllVendorGstinByVendorPans(sellerPan);
		} else {

		}

		vendorGstinStr.removeIf(Objects::isNull);
		Collections.sort(vendorGstinStr);
		List<QRGstinDto> sellerGstinList = vendorGstinStr.stream()
				.map(e -> convertToDto(e))
				.collect(Collectors.toCollection(ArrayList::new));
		return sellerGstinList;
	}

	private QRGstinDto convertToDto(String e) {
		QRGstinDto dto = new QRGstinDto();
		dto.setGstin(e);
		return dto;
	}

	@Override
	public Pair<List<QRResponseSummaryEntity>, Integer> getViewFilterLisDtls(
			Long fileId, int pageSize, int pageNum) {

		int recordsToStart = pageNum;

		int noOfRowstoFetch = pageSize;

		Pageable pageReq = PageRequest.of(recordsToStart, noOfRowstoFetch,
				Direction.DESC, "id");

		List<QRResponseSummaryEntity> tableList = qrRespSummRepo
				.findByFileId(fileId, pageReq);
		for (int i = 0; i < tableList.size(); i++) {
			tableList.get(i).setTempFileName(tableList.get(i).getFileName());
		}

		List<QRResponseSummaryEntity> retList = qrRespSummRepo
				.findByFileId(fileId);
		return new Pair<List<QRResponseSummaryEntity>, Integer>(tableList,
				retList.size());
	}

	@Override
	public Pair<List<QRPDFResponseSummaryEntity>, Integer> getqrPdfViewFilterLisDtls(
			Long fileId, int pageSize, int pageNum) {

		int recordsToStart = pageNum;

		int noOfRowstoFetch = pageSize;

		Pageable pageReq = PageRequest.of(recordsToStart, noOfRowstoFetch,
				Direction.DESC, "id");

		List<QRPDFResponseSummaryEntity> tableList = qrPdfRespSummRepo
				.findByFileId(fileId, pageReq);
		for (int i = 0; i < tableList.size(); i++) {
			tableList.get(i).setTempFileName(tableList.get(i).getFileName());
		}

		List<QRPDFResponseSummaryEntity> retList = qrPdfRespSummRepo
				.findByFileId(fileId);
		return new Pair<List<QRPDFResponseSummaryEntity>, Integer>(tableList,
				retList.size());
	}

	@Override
	public Pair<List<QRPDFJSONResponseSummaryEntity>, Integer> getqrPdfJsonViewFilterLisDtls(
			Long fileId, int pageSize, int pageNum) {

		int recordsToStart = pageNum;

		int noOfRowstoFetch = pageSize;

		Pageable pageReq = PageRequest.of(recordsToStart, noOfRowstoFetch,
				Direction.DESC, "id");

		List<QRPDFJSONResponseSummaryEntity> tableList = qrPdfJSONRespSummRepo
				.findByFileId(fileId, pageReq);
		for (int i = 0; i < tableList.size(); i++) {
			tableList.get(i).setTempFileName(tableList.get(i).getFileName());
		}

		List<QRPDFJSONResponseSummaryEntity> retList = qrPdfJSONRespSummRepo
				.findByFileId(fileId);
		return new Pair<List<QRPDFJSONResponseSummaryEntity>, Integer>(
				tableList, retList.size());
	}

	@Override
	public void setMatchandMisMatchCount(
			List<QRResponseSummaryEntity> summList) {
		for (QRResponseSummaryEntity qrSummEntity : summList) {
			int matchCount = 0;
			int mismatchCount = 0;
			List<String> attStr = new ArrayList<>();
			if (Available
					.equalsIgnoreCase(qrSummEntity.getSellerGstinMatch())) {
				matchCount++;
			} else {
				attStr.add(QRCodeValidatorConstants.SUPP_GSTIN);
				mismatchCount++;
			}
			if (Available.equalsIgnoreCase(qrSummEntity.getBuyerGstinMatch())) {
				matchCount++;
			} else {
				attStr.add(QRCodeValidatorConstants.REC_GSTIN);
				mismatchCount++;
			}

			if (Available.equalsIgnoreCase(qrSummEntity.getIrnMatch())) {
				matchCount++;
			} else {
				attStr.add(QRCodeValidatorConstants.IRN_NUM);
				mismatchCount++;
			}

			if (Available
					.equalsIgnoreCase(qrSummEntity.getMainHsnCodeMatch())) {
				matchCount++;
			} else {
				attStr.add(QRCodeValidatorConstants.HSN_NUM);
				mismatchCount++;
			}

			if (Available.equalsIgnoreCase(qrSummEntity.getDocDtMatch())) {
				matchCount++;
			} else {
				attStr.add(QRCodeValidatorConstants.DOC_DATE);
				mismatchCount++;
			}
			if (Available.equalsIgnoreCase(qrSummEntity.getDocNoMatch())) {
				matchCount++;
			} else {
				attStr.add(QRCodeValidatorConstants.DOC_NUM);
				mismatchCount++;
			}
			if (Available.equalsIgnoreCase(qrSummEntity.getDocTypeMatch())) {
				matchCount++;
			} else {
				attStr.add(QRCodeValidatorConstants.DOC_TY);
				mismatchCount++;
			}
			if (Available.equalsIgnoreCase(qrSummEntity.getTotInvValMatch())) {
				matchCount++;
			} else {
				attStr.add(QRCodeValidatorConstants.VALUE);
				mismatchCount++;
			}
			if ("Valid".equalsIgnoreCase(qrSummEntity.getSignature())) {
				matchCount++;
			} else {
				attStr.add(QRCodeValidatorConstants.SIGN);
				mismatchCount++;
			}
			qrSummEntity.setDocKey(QRCommonUtility.generateQRCodeKey(
					qrSummEntity.getSellerGstin(), qrSummEntity.getDocDt(),
					qrSummEntity.getDocType(), qrSummEntity.getDocNo(),
					qrSummEntity.getBuyerGstin()));
			qrSummEntity.setMatchCount(matchCount);
			qrSummEntity.setMisMatAtt(String.join(", ", attStr));
			qrSummEntity.setMismatchCount(mismatchCount);

		}
	}

	@Override
	public JsonObject reconQrAndPdf(File pdfFile, String identifier, Long id,
			String uploadType, String entityId) {
		String accessToken = null;
		String accessTokenPdf = null;
		QrvsPdfValidatorFinalRespDto finalResp = new QrvsPdfValidatorFinalRespDto();
		JsonPdfValidatorFinalRespDto finalJsonvsPdfResp = new JsonPdfValidatorFinalRespDto();
		PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
				"QRCODEAPI_PopulateFileDtls_START",
				"QRCodeValidatorServiceImpl", "saveAndPersistQRReports",
				pdfFile.getName());
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		String errMsg = null;
		ExecutorService executor = Executors.newFixedThreadPool(2);
		List<QRPDFResponseSummaryEntity> qrPDFlistofSumm = new ArrayList<>();
		List<QRPDFJSONResponseSummaryEntity> qrPDFJsonlistofSumm = new ArrayList<>();
		LocalDateTime startTime = LocalDateTime.now();
		QRCountDto countDto = new QRCountDto();

		try {
			Map<String, Config> configMap = configManager
					.getConfigs("QRVALIDATOR", "qr.details", "DEFAULT");

			String username = configMap.get("qr.details.username") == null ? ""
					: configMap.get("qr.details.username").getValue();

			String password = configMap.get("qr.details.password") == null ? ""
					: configMap.get("qr.details.password").getValue();

			String apiAccesskey = configMap
					.get("qr.details.apiAccessKey") == null ? ""
							: configMap.get("qr.details.apiAccessKey")
									.getValue();

			String qrValidUrl = configMap.get("qr.details.validatorUrl") == null
					? "" : configMap.get("qr.details.validatorUrl").getValue();

			qrUploadFileStatusRepo
					.updateFileStatus(QRCodeValidatorConstants.INPROGRESS, id);

			PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
					"QRCODEAPI_PopulateAccessToken_START",
					"QRCodeValidatorServiceImpl", "saveAndPersistQRReports",
					pdfFile.getName());

			String getAuthRespBody = qrcommonUtility.getAccessToken(username,
					password, apiAccesskey);

			PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
					"QRCODEAPI_PopulateAccessToken_END",
					"QRCodeValidatorServiceImpl", "saveAndPersistQRReports",
					pdfFile.getName());

			JsonObject requestObject = JsonParser.parseString(getAuthRespBody)
					.getAsJsonObject();
			int statusCode = requestObject.get("status").getAsInt();

			if (statusCode == 1) {
				accessToken = requestObject.get("accessToken").getAsString();
			} else {
				errMsg = "Access Token is not Available from QR.";
				LOGGER.error(errMsg);
			}
			countDto.incrementTotalCnt();

			List<PdfAuthInfoEntity> pdfEntityList = pdfAuthRepo.findAll();

			if (!pdfEntityList.isEmpty()) {

				LocalDateTime expiryTime = pdfEntityList.get(0)
						.getPdfTokenExpTime();
				LocalDateTime currentTime = LocalDateTime.now();
				Duration duration = Duration.between(currentTime, expiryTime);
				long hoursDifference = duration.toHours();

				/* if (hoursDifference <= 1) { */

				// pdf access token api
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
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.QRCODEVALIDATOR,
						"QRCODEAPI_PopulateAccessToken_START",
						"QRCodeValidatorServiceImpl", "saveAndPersistQRReports",
						pdfFile.getName());

				String getAuthRespBodyPdf = qrcommonUtility.getPdfAccessToken(
						usernamePdf, passwordPdf, apiAccesskeyPdf);

				PerfUtil.logEventToFile(
						PerfamanceEventConstants.QRCODEVALIDATOR,
						"QRCODEAPI_PopulateAccessToken_END",
						"QRCodeValidatorServiceImpl", "saveAndPersistQRReports",
						pdfFile.getName());

				JsonObject requestObjectPdf = JsonParser
						.parseString(getAuthRespBodyPdf).getAsJsonObject();
				int statusCodePdf = requestObjectPdf.get("status").getAsInt();

				if (statusCodePdf == 1) {
					accessTokenPdf = requestObjectPdf.get("accessToken")
							.getAsString();
				} else {
					errMsg = "Access Token is not Available from PDF.";
					LOGGER.error(errMsg);
				}
				pdfAuthRepo.updateIdToken(accessTokenPdf,
						pdfEntityList.get(0).getId(),
						LocalDateTime.now().plusHours(6), LocalDateTime.now());
				/*
				 * }else { accessTokenPdf = pdfEntityList.get(0).getIdToken(); }
				 */
			}

			LOGGER.debug(" accessTokenPdf {} ", accessTokenPdf);
			Pair<String, String> qrPdfReconCall = qrcommonUtility
					.getQRandPDFResponse(pdfFile, accessToken, apiAccesskey,
							qrValidUrl, accessTokenPdf);

			if ("QRJSONVSPDF".equalsIgnoreCase(identifier)) {
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.QRCODEVALIDATOR,
						"QRCODEAPI_QRJsonvsPDFReconAPI_START",
						"QRCodeValidatorServiceImpl", "reconQrAndPdf",
						pdfFile.getName());

				// TO-DO

				if (qrPdfReconCall.getValue1()
						.equalsIgnoreCase(QRCommonUtility.EXCEP_ERROR)
						&& qrPdfReconCall.getValue0().equalsIgnoreCase(
								QRCommonUtility.EXCEP_ERROR)) {

					countDto.incrementErrCnt();
					LOGGER.error(" Exception in both QR and PDF ");
					String msg = String.format(
							"Exception while doing the Recon for File ID %s",
							id);
					finalResp.setErrMsg(msg);
					resp.add("hdr",
							gson.toJsonTree(APIRespDto.creatErrorResp()));
					resp.add("resp", gson.toJsonTree(finalResp));
					createQRResponseLog(id, pdfFile.getName(),
							qrPdfReconCall.getValue0(),
							qrPdfReconCall.getValue1(),
							null, gson.toJson(finalResp), null);
					qrUploadFileStatusRepo.updateFileStatus(
							QRCodeValidatorConstants.FAILED, id);
					LocalDateTime endTime = LocalDateTime.now();


					qrUploadFileStatusRepo.updateCount(countDto.getTotalDocCnt(),
							countDto.getQrCnt(), countDto.getSigMisCnt(),
							countDto.getFullMatCnt(), countDto.getErrCnt(), startTime,
							endTime, id);


					return resp;
				}

				if (qrPdfReconCall.getValue1()
						.equalsIgnoreCase(QRCommonUtility.EXCEP_ERROR)) {
					countDto.incrementErrCnt();
				} else {
					finalJsonvsPdfResp = qrcommonUtility
							.parseAndPopulateQRPDFJSONResponse(
									qrPDFJsonlistofSumm,
									qrPdfReconCall.getValue0(),
									qrPdfReconCall.getValue1(), id, countDto,
									pdfFile.getName(), uploadType, entityId);
					if (!qrPDFJsonlistofSumm.isEmpty()) {
						qrPdfJSONRespSummRepo.saveAll(qrPDFJsonlistofSumm);
						qrUploadFileStatusRepo.updateFileStatus(
								QRCodeValidatorConstants.COMPLETED, id);
					} else {
						qrUploadFileStatusRepo.updateFileStatus(
								QRCodeValidatorConstants.FAILED, id);
					}
					PerfUtil.logEventToFile(
							PerfamanceEventConstants.QRCODEVALIDATOR,
							"QRCODEAPI_QRvsPDFReconAPI_END",
							"QRCodeValidatorServiceImpl", "reconQrAndPdf",
							pdfFile.getName());
					resp.add("hdr",
							gson.toJsonTree(APIRespDto.createSuccessResp()));
					resp.add("resp", gson.toJsonTree(finalResp));
				}
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.QRCODEVALIDATOR,
						"QRCODEAPI_QRJsonvsPDFReconAPI_END",
						"QRCodeValidatorServiceImpl", "reconQrAndPdf",
						pdfFile.getName());
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(finalJsonvsPdfResp));

			} else {
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.QRCODEVALIDATOR,
						"QRCODEAPI_QRvsPDFReconAPI_START",
						"QRCodeValidatorServiceImpl", "reconQrAndPdf",
						pdfFile.getName());

				if (qrPdfReconCall.getValue1()
						.equalsIgnoreCase(QRCommonUtility.EXCEP_ERROR)
						&& qrPdfReconCall.getValue0().equalsIgnoreCase(
								QRCommonUtility.EXCEP_ERROR)) {
					LOGGER.error(" Exception in both QR and PDF ");
					String msg = String.format(
							"Exception while doing the Recon for File ID %s",
							id);
					countDto.incrementErrCnt();

					finalResp.setErrMsg(msg);
					
					resp.add("hdr",
							gson.toJsonTree(APIRespDto.creatErrorResp()));
					resp.add("resp", gson.toJsonTree(finalResp));
					
					createQRResponseLog(id, pdfFile.getName(),
							qrPdfReconCall.getValue0(),
							qrPdfReconCall.getValue1(),
							gson.toJson(finalResp), null, null);
					
					qrUploadFileStatusRepo.updateFileStatus(
							QRCodeValidatorConstants.FAILED, id);
					LocalDateTime endTime = LocalDateTime.now();


					qrUploadFileStatusRepo.updateCount(countDto.getTotalDocCnt(),
							countDto.getQrCnt(), countDto.getSigMisCnt(),
							countDto.getFullMatCnt(), countDto.getErrCnt(), startTime,
							endTime, id);

					
					return resp;

				}

				if (qrPdfReconCall.getValue1()
						.equalsIgnoreCase(QRCommonUtility.EXCEP_ERROR)) {
					countDto.incrementErrCnt();

				}
				finalResp = qrcommonUtility.parseAndPopulateQRPDFResponse(
						qrPDFlistofSumm, qrPdfReconCall.getValue0(),
						qrPdfReconCall.getValue1(), id, countDto,
						pdfFile.getName(), uploadType, entityId);
				if (!qrPDFlistofSumm.isEmpty()) {
					qrPdfRespSummRepo.saveAll(qrPDFlistofSumm);
					qrUploadFileStatusRepo.updateFileStatus(
							QRCodeValidatorConstants.COMPLETED, id);
					if (!uploadType.equalsIgnoreCase("webUpload")) {
						qrUploadFileStatusRepo
								.updateEntityId(finalResp.getEntityId(), id);
						LOGGER.debug(
								"In QRPDFResponseSummaryEntity, Uploaded By {}",
								uploadType);
					}
				} else {
					qrUploadFileStatusRepo.updateFileStatus(
							QRCodeValidatorConstants.FAILED, id);
				}
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.QRCODEVALIDATOR,
						"QRCODEAPI_QRvsPDFReconAPI_END",
						"QRCodeValidatorServiceImpl", "reconQrAndPdf",
						pdfFile.getName());
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(finalResp));

			}
			LocalDateTime endTime = LocalDateTime.now();

			qrUploadFileStatusRepo.updateCount(countDto.getTotalDocCnt(),
					countDto.getQrCnt(), countDto.getSigMisCnt(),
					countDto.getFullMatCnt(), countDto.getErrCnt(), startTime,
					endTime, id);

			return resp;
		} catch (Exception ex) {
			qrUploadFileStatusRepo
					.updateFileStatus(QRCodeValidatorConstants.FAILED, id);
			String msg = ex.getMessage();
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex.getMessage());
		} finally {
			PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
					"QRCODEAPI_PopulateFileDtls_END",
					"QRCodeValidatorServiceImpl", "saveAndPersistQRReports",
					pdfFile.getName());
			executor.shutdown();
		}
	}

	@Override
	public Long setEntityId(Object entity, String buyerGstin, Long fileId) {
		Long entityId = null;
		if (buyerGstin != null) {
			LOGGER.debug("Inside setentityId BuyerGstIN {}", buyerGstin);
			GSTNDetailEntity gstnDetailEntity = gstnDetailsRepo
					.findByGstinAndIsDeleteFalse(buyerGstin);
			entityId = (gstnDetailEntity != null
					&& gstnDetailEntity.getEntityId() != null)
							? gstnDetailEntity.getEntityId() : null;
			if (gstnDetailEntity != null) {
				LOGGER.debug("entityId {}", gstnDetailEntity.getEntityId());
			} else {
				LOGGER.debug(
						"gstnDetailEntity is null or entityId is null, setting entityId to null");
			}
			if (entity instanceof QRResponseSummaryEntity) {
				((QRResponseSummaryEntity) entity).setEntityId(entityId);
				LOGGER.debug("QRResponseSummaryEntity extracted entityId {}",
						entityId);
			} else if (entity instanceof QRPDFResponseSummaryEntity) {
				((QRPDFResponseSummaryEntity) entity).setEntityId(entityId);
				LOGGER.debug("QRPDFResponseSummaryEntity extracted entityId {}",
						entityId);
				// qrPdfRespSummRepo.updateEntityId(entityId, fileId);
			} else if (entity instanceof QRPDFJSONResponseSummaryEntity) {
				((QRPDFJSONResponseSummaryEntity) entity).setEntityId(entityId);
				LOGGER.debug(
						"QRPDFJSONResponseSummaryEntity extracted entityId {}",
						entityId);
				// qrPdfJSONRespSummRepo.updateEntityId(entityId, fileId);
			} else {
				LOGGER.warn("Unexpected entity type: {}",
						entity.getClass().getName());
			}
		}
		return entityId;

	}

	@Override
	public void revIntegrateQRData(List<Long> activeIds,
			Map<String, Config> configMap, File tempDir) {

		try {
			List<QRResponseSummaryEntity> retList = qrRespSummRepo
					.findByFileIdIn(activeIds);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Option A retList size : {}", retList.size());

			Map<Long, List<QRResponseSummaryEntity>> groupedMap = retList
					.stream().collect(Collectors
							.groupingBy(QRResponseSummaryEntity::getFileId));

			List<String> filePaths = new ArrayList<>();
			for (Entry<Long, List<QRResponseSummaryEntity>> pair : groupedMap
					.entrySet()) {
				try {
					Optional<QRUploadFileStatusEntity> fileRecord = qrUploadFileStatusRepo
							.findById(pair.getKey());
					Workbook workBook = qrCodeService
							.generateQrReport(pair.getValue(), null, null);
					String uploadedFileName = FilenameUtils
							.removeExtension(fileRecord.get().getFileName());
					// String fileName = DocumentUtility
					// .getUniqueFileName(uploadedFileName);
					String excelFilePath = tempDir.getAbsolutePath()
							+ File.separator + uploadedFileName + ".xlsx";
					workBook.save(excelFilePath, SaveFormat.XLSX);
					filePaths.add(excelFilePath);
				} catch (Exception ee) {
					LOGGER.error(
							"Error while generating QRValidator summary "
									+ "report for option A fileId: {}",
							pair.getKey(), ee);
				}
			}

			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Option A Excel File Paths are : {}",
						filePaths.toString());

			boolean isReverseInt = sftpService.uploadFiles(filePaths,
					configMap
							.get(ConfigConstants.QR_VALIDATOR_SFTP_RESPONSE_DESTINATION)
							.getValue());
			if (isReverseInt) {
				// UPDATE isReverseIntg flag to true
				qrUploadFileStatusRepo
						.updateReverseIntegrated(groupedMap.keySet());
			} else {
				LOGGER.error(
						"Reverse Integration failed for option A "
								+ "QR Validator with fileIds : {} ",
						groupedMap.keySet());
			}

		} catch (Exception ee) {
			String msg = "Exception occured while reverse integrating option A reports"
					+ " to SFTP Destination from QR Validator";
			LOGGER.error(msg, ee);
			throw new AppException(msg, ee);
		}

	}

	private QRResponseLogEntity createQRResponseLog(Long fileId,
			String fileName, String qrResponseBody, String pdfResponseBody,
			String qrPdfReconResponse, String jsonPdfReconResponse,
			JsonPdfValidatorFinalRespDto jsonPdfresp) {

		LOGGER.debug(" inside log 1");
		QRResponseLogEntity responseLog = new QRResponseLogEntity();
		responseLog.setFileId(fileId);
		responseLog.setFileName(fileName);
		responseLog.setQrResponse(qrResponseBody);
		responseLog.setPdfResponse(pdfResponseBody);
		responseLog.setCreatedBy("SYSTEM");
		responseLog.setCreatedOn(LocalDateTime.now());
		responseLog.setQrPdfReconResponse(qrPdfReconResponse);
		responseLog.setJsonPdfReconResponse(jsonPdfReconResponse);
		if (jsonPdfresp != null) {
			responseLog.setIrnJsonResponse(jsonPdfresp.getJsonResponse());
		}
		respLogRep.save(responseLog);
		return responseLog;
	}

}
