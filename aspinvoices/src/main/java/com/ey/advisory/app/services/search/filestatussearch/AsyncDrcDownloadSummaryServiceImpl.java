package com.ey.advisory.app.services.search.filestatussearch;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.Font;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Style;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.entities.drc.TblDrcDetails;
import com.ey.advisory.app.data.entities.drc.TblDrcGetRetCompListDetails;
import com.ey.advisory.app.data.entities.drc.TblDrcPaymentDetails;
import com.ey.advisory.app.data.entities.drc.TblDrcReasonDetails;
import com.ey.advisory.app.data.entities.drc.TblDrcSaveStatus;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.repositories.client.SignAndFileRepository;
import com.ey.advisory.app.data.repositories.client.drc.TblDrcDetailsRepo;
import com.ey.advisory.app.data.repositories.client.drc.TblDrcGetRetCompListDetailsRepo;
import com.ey.advisory.app.data.repositories.client.drc.TblDrcPaymentDetailsRepo;
import com.ey.advisory.app.data.repositories.client.drc.TblDrcReasonDetailsRepo;
import com.ey.advisory.app.data.repositories.client.drc.TblDrcSaveStatusRepo;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BDrc01DownloadDto;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BDrc03PaymentDownloadDto;
import com.ey.advisory.app.search.reports.BasicCommonSecParamRSReports;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Harsh
 *
 */
@Component("AsyncDrcDownloadSummaryServiceImpl")
@Slf4j
public class AsyncDrcDownloadSummaryServiceImpl
		implements AsyncReportDownloadService {

	@Autowired
	private FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("BasicCommonSecParamRSReports")
	BasicCommonSecParamRSReports basicCommonSecParamRSReports;

	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository repo;

	@Autowired
	@Qualifier("TblDrcDetailsRepo")
	TblDrcDetailsRepo tblDrcRepo;

	@Autowired
	@Qualifier("TblDrcPaymentDetailsRepo")
	TblDrcPaymentDetailsRepo tblDrcPayDtlRepo;

	@Autowired
	@Qualifier("SignAndFileRepository")
	SignAndFileRepository signAndFileRepo;

	@Autowired
	@Qualifier("TblDrcSaveStatusRepo")
	TblDrcSaveStatusRepo tblDrcSaveStatsRepo;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository returnstatusRepo;

	@Autowired
	TblDrcGetRetCompListDetailsRepo drcCompListDetailsRepo;

	@Autowired
	TblDrcReasonDetailsRepo tblDrcReasonDetailsRepo;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	private static final String UPLOADED_FILENAME_MSG = "Uploaded file Name :'%s'";

	@Transactional(value = "clientTransactionManager")
	@Override
	public void generateReports(Long id) {
		File tempDir = null;
		try {
			tempDir = createTempDir();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Created temporary directory");
			}

			Optional<FileStatusDownloadReportEntity> optEntity = fileStatusDownloadReportRepo
					.findById(id);
			String reqPayload = optEntity.get().getReqPayload();

			FileStatusDownloadReportEntity entity = optEntity.get();
			String gstinString = GenUtil
					.convertClobtoString(entity.getGstins());
			List<String> gstinList = Arrays.asList(gstinString.split(","));

			String reportType = entity.getReportType();

			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			Gstr1ReviwSummReportsReqDto criteria = gson.fromJson(reqPayload,
					Gstr1ReviwSummReportsReqDto.class);

			LocalDateTime timeOfGeneration = LocalDateTime.now();
			LocalDateTime convertISDDate = EYDateUtil
					.toISTDateTimeFromUTC(timeOfGeneration);
			DateTimeFormatter format = DateTimeFormatter
					.ofPattern("yyyyMMddHHmmss");
			String fileName = null;
			if (gstinList.size() > 1) {
				fileName = tempDir.getAbsolutePath() + File.separator
						+ "DetailedReport_" + reportType + "_"
						+ format.format(convertISDDate) + ".xlsx";
			} else {
				fileName = tempDir.getAbsolutePath() + File.separator
						+ "DetailedReport_" + reportType + "_" + gstinString
						+ "_" + format.format(convertISDDate) + ".xlsx";
			}
			String taxPeriod = criteria.getTaxperiod();

			List<TblDrcDetails> tblDrcDetails = tblDrcRepo
					.findByGstinInAndTaxPeriodAndIsActiveTrue(gstinList,
							taxPeriod);

			if (tblDrcDetails.isEmpty()) {
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.NO_DATA_FOUND, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
						null);

				return;
			}

			Workbook workbook = getDrcData(gstinList, taxPeriod, id,
					tblDrcDetails);

			workbook.save(fileName);

			String zipFileName = zipEinvoicePdfFiles(tempDir, reportType,
					gstinList, convertISDDate, gstinString);
			File zipFile = new File(tempDir, zipFileName);

			/*
			 * String uploadedFileName = DocumentUtility.uploadZipFile(zipFile,
			 * ConfigConstants.DRC_REPORT_DOWNLOAD);
			 */

			Pair<String, String> uploadedDocName = DocumentUtility
					.uploadFile(zipFile, ConfigConstants.DRC_REPORT_DOWNLOAD);
			String uploadedFileName = uploadedDocName.getValue0();
			String docId = uploadedDocName.getValue1();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						String.format(UPLOADED_FILENAME_MSG, uploadedFileName));
			}

			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATED, uploadedFileName,
					LocalDateTime.now(), docId);
		} catch (Exception e) {
			handleReportGenerationFailure(id, e);
		} finally {
			GenUtil.deleteTempDir(tempDir);
		}
	}

	public Workbook getDrcData(List<String> gstin, String taxPeriod, Long id,
			List<TblDrcDetails> tblDrcDetails) {
		List<Gstr3BDrc01DownloadDto> respDrc01List = new ArrayList<>();
		List<Gstr3BDrc03PaymentDownloadDto> respDrc03PayList = new ArrayList<>();
		Workbook workbook = null;
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			Optional<FileStatusDownloadReportEntity> optEntity = fileStatusDownloadReportRepo
					.findById(id);
			String reqPayload = optEntity.get().getReqPayload();
			Gstr1ReviwSummReportsReqDto criteria = gson.fromJson(reqPayload,
					Gstr1ReviwSummReportsReqDto.class);
			String entityName = repo
					.findEntityNameByEntityId(criteria.getEntityId().get(0));
			for (TblDrcDetails dto : tblDrcDetails) {
				Gstr3BDrc01DownloadDto obj = new Gstr3BDrc01DownloadDto();
				obj.setGstr1Igst(dto.getGstr1IgstAmt());
				obj.setGstr1Cgst(dto.getGstr1CgstAmt());
				obj.setGstr1Sgst(dto.getGstr1SgstAmt());
				obj.setGstr1Cess(dto.getGstr1CessAmt());
				obj.setGstr1Total(dto.getGstr1TtlAmt());
				obj.setGstr3bIgst(dto.getGstr3BIgstAmt());
				obj.setGstr3bCgst(dto.getGstr3BCgstAmt());
				obj.setGstr3bSgst(dto.getGstr3BSgstAmt());
				obj.setGstr3bCess(dto.getGstr3BCessAmt());
				obj.setGstr3bTotal(dto.getGstr3BTtlAmt());
				obj.setDiffIgst(dto.getIgstDiffAmt());
				obj.setDiffCgst(dto.getCgstDiffAmt());
				obj.setDiffSgst(dto.getSgstDiffAmt());
				obj.setDiffCess(dto.getCessDiffAmt());
				obj.setDiffTotal(dto.getTtlDiffAmt());
				obj.setGstin(dto.getGstin());
				obj.setTaxPeriod(dto.getTaxPeriod());
				obj.setRefId(dto.getRefId());
				obj.setProfile(dto.getReturnFilingFreq());
				obj.setCreatedBy(dto.getCreatedBy());

				Optional<TblDrcGetRetCompListDetails> compList = drcCompListDetailsRepo
						.findByGstinAndTaxPeriodAndIsActiveTrue(dto.getGstin(),
								dto.getTaxPeriod());
				if (compList.isPresent()) {
					if (compList.get().getGetStatus()
							.equalsIgnoreCase(APIConstants.SUCCESS)) {
						Optional<GetAnx1BatchEntity> batchEntity = batchRepo
								.findByTypeAndApiSectionAndSgstinAndTaxPeriodAndIsDeleteFalse(
										APIIdentifiers.DRC_GETRETCOMP_SUMMARY,
										APIIdentifiers.DRC.toUpperCase(),
										dto.getGstin(), dto.getTaxPeriod());
						if (batchEntity.isPresent()) {
							obj.setStatusDrc01b(batchEntity.get().getStatus());
							obj.setGetCallDateTime(EYDateUtil
									.fmtDate3(EYDateUtil.toISTDateTimeFromUTC(
											batchEntity.get().getCreatedOn())));
						} else {
							obj.setStatusDrc01b(compList.get().getGetStatus());
							obj.setGetCallDateTime(EYDateUtil
									.fmtDate3(EYDateUtil.toISTDateTimeFromUTC(
											compList.get().getCreatedOn())));
						}
					} else {
						obj.setStatusDrc01b(compList.get().getGetStatus());
						obj.setGetCallDateTime(EYDateUtil
								.fmtDate3(EYDateUtil.toISTDateTimeFromUTC(
										compList.get().getCreatedOn())));
					}
				} else {
					obj.setStatusDrc01b(APIConstants.NOT_INITIATED);
				}
				List<TblDrcReasonDetails> reasonDetails = tblDrcReasonDetailsRepo
						.findByDrcDetailsId(dto);
				String resnCodeList = reasonDetails.stream()
						.map(o -> o.getReasonCode())
						.collect(Collectors.joining(","));
				obj.setReasonCode(resnCodeList);
				String resnDesList = reasonDetails.stream()
						.map(o -> o.getReasonDesc())
						.collect(Collectors.joining(","));
				obj.setReasonDesc(resnDesList);
				GstrReturnStatusEntity fileStatus = returnstatusRepo
						.findByGstinAndTaxPeriodAndReturnTypeAndIsCounterPartyGstinFalse(
								dto.getGstin(), taxPeriod, APIIdentifiers.DRC);
				TblDrcSaveStatus saveStatus = tblDrcSaveStatsRepo
						.findByGstinAndTaxPeriodAndIsActiveTrue(dto.getGstin(),
								taxPeriod);
				if (fileStatus != null) {
					obj.setFilingStatusDrc01Reply(fileStatus.getStatus());
				} else if (saveStatus != null) {
					obj.setFilingStatusDrc01Reply(saveStatus.getStatus());
				} else if (fileStatus == null && saveStatus == null) {
					obj.setFilingStatusDrc01Reply(APIConstants.NOT_INITIATED);
					obj.setCreatedBy(APIConstants.NOT_INITIATED);
				}
				respDrc01List.add(obj);

				List<TblDrcPaymentDetails> tblPayDetls = tblDrcPayDtlRepo
						.findByDrcDetailsId(dto);

				for (TblDrcPaymentDetails dtoo : tblPayDetls) {
					Gstr3BDrc03PaymentDownloadDto obje = new Gstr3BDrc03PaymentDownloadDto();
					obje.setGstin(dto.getGstin());
					obje.setTaxPeriod(dto.getTaxPeriod());
					obje.setFeeIgst(dtoo.getFeeIgstAmt());
					obje.setFeeCgst(dtoo.getFeeCgstAmt());
					obje.setFeeCess(dtoo.getFeeCessAmt());
					obje.setFeeSgst(dtoo.getFeeSgstAmt());
					obje.setFeeTotal(dtoo.getFeeTtlAmt());
					obje.setIntrCess(dtoo.getInterestCessAmt());
					obje.setIntrCgst(dtoo.getInterestCgstAmt());
					obje.setIntrSgst(dtoo.getInterestSgstAmt());
					obje.setIntrIgst(dtoo.getInterestIgstAmt());
					obje.setIntrTotal(dtoo.getInterestTtlAmt());
					obje.setPenCess(dtoo.getPenaltyCessAmt());
					obje.setPenCgst(dtoo.getPenaltyCgstAmt());
					obje.setPenIgst(dtoo.getPenaltyIgstAmt());
					obje.setPenSgst(dtoo.getPenaltySgstAmt());
					obje.setPenTotal(dtoo.getPenaltyTtlAmt());
					obje.setOthCess(dtoo.getOthersCessAmt());
					obje.setOthCgst(dtoo.getOthersCgstAmt());
					obje.setOthIgst(dtoo.getOthersIgstAmt());
					obje.setOthSgst(dtoo.getOthersSgstAmt());
					obje.setOthTotal(dtoo.getOthersTtlAmt());
					obje.setTaxCess(dtoo.getTaxCessAmt());
					obje.setTaxCgst(dtoo.getTaxCgstAmt());
					obje.setTaxIgst(dtoo.getTaxIgstAmt());
					obje.setTaxSgst(dtoo.getTaxSgstAmt());
					obje.setTaxTotal(dtoo.getTaxTtlAmt());
					obje.setDrc03Arn(dtoo.getDrcArnNo());
					obje.setGetCallStatus(obj.getStatusDrc01b());
					obje.setCreatedOn(obj.getGetCallDateTime());
					respDrc03PayList.add(obje);
				}
			}
			workbook = writeToExcel(respDrc01List, respDrc03PayList,
					entityName);
		} catch (Exception e) {
			String msg = String.format(
					"Error occurred in AsyncDrcDownloadSummaryServiceImpl.getDrcData()",
					e);
			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
					null);
			LOGGER.error(msg, e);
			throw new AppException(msg, e);

		}
		return workbook;

	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory(ConfigConstants.DRC_REPORT_DOWNLOAD)
				.toFile();
	}

	private void handleReportGenerationFailure(Long id, Exception e) {
		fileStatusDownloadReportRepo.updateStatus(id,
				ReportStatusConstants.REPORT_GENERATION_FAILED, null,
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()), null);
		String msg = "Exception occurred while generating Download file";
		LOGGER.error(msg, e);
		throw new AppException(msg, e);
	}

	private String zipEinvoicePdfFiles(File tempDir, String reportType,
			List<String> gstinList, LocalDateTime convertISDDate,
			String gstinString) {

		if (LOGGER.isDebugEnabled()) {
			String msg = " getting files to be zipped from temp directory";
			LOGGER.debug(msg);
		}
		String fileName = null;
		List<String> filesToZip = getAllFilesToBeZipped(tempDir);
		DateTimeFormatter format = DateTimeFormatter
				.ofPattern("yyyyMMddHHmmss");
		if (gstinList.size() > 1) {
			fileName = "DetailedReport_" + reportType;

		} else {
			fileName = "DetailedReport_" + reportType + "_" + gstinString;

		}
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

	private static List<String> getAllFilesToBeZipped(File tmpDir) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Temporary directory containing files to "
							+ "be zipped: %s", tmpDir.getAbsolutePath());
			LOGGER.debug(msg);
		}
		FilenameFilter pdfFilter = new FilenameFilter() {
			public boolean accept(File tmpDir, String name) {
				return name.toLowerCase().endsWith(".xlsx");
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
		return retFileNames;
	}

	private Workbook writeToExcel(List<Gstr3BDrc01DownloadDto> respDrc01List,
			List<Gstr3BDrc03PaymentDownloadDto> respDrc03PayList,
			String entityName) {
		Workbook workbook = null;
		int startRow = 7;
		int startcolumn = 2;
		boolean isHeaderRequired = false;
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin .writeToExcel " + "List Size = "
					+ respDrc01List.size();
			LOGGER.debug(msg);
		}
		try {
			if (respDrc01List != null && !respDrc01List.isEmpty()) {

				workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates", "DrcReport_Summary.xlsx");

				if (LOGGER.isDebugEnabled()) {
					String msg = "AsyncDrcDownloadSummaryServiceImpl.writeToExcel "
							+ "workbook created writing data to the workbook";
					LOGGER.debug(msg);
				}

				for (int i = 0; i < 2; i++) {

					Cells reportCells = workbook.getWorksheets().get(i)
							.getCells();

					Worksheet sheet = workbook.getWorksheets().get(i);

					Cell cellC3 = sheet.getCells().get("C3");

					cellC3.setValue(entityName);
					Style style = workbook.createStyle();
					Font font = style.getFont();
					font.setBold(true);
					font.setSize(12);

					String[] invoiceHeaders = null;

					if (i == 0 && !respDrc01List.isEmpty()) {
						invoiceHeaders = commonUtility
								.getProp("gstr3b.drc01b.report.header")
								.split(",");
						reportCells.importCustomObjects(respDrc01List,
								invoiceHeaders, isHeaderRequired, startRow,
								startcolumn, respDrc01List.size(), true,
								"yyyy-mm-dd", false);

					}
					if (i == 1 && !respDrc03PayList.isEmpty()) {
						invoiceHeaders = commonUtility
								.getProp("gstr3b.drc03.payment.report.header")
								.split(",");
						reportCells.importCustomObjects(respDrc03PayList,
								invoiceHeaders, isHeaderRequired, startRow,
								startcolumn, respDrc03PayList.size(), true,
								"yyyy-mm-dd", false);

					}

					if (LOGGER.isDebugEnabled()) {
						String msg = "AsyncDrcDownloadSummaryServiceImpl.writeToExcel "
								+ "saving workbook";
						LOGGER.debug(msg);
					}
					int lastRowIndex = sheet.getCells().getMaxDataRow();
					sheet.getCells().deleteRow(lastRowIndex + 1);
					workbook.save(ConfigConstants.DRC_REPORT_DOWNLOAD,
							SaveFormat.XLSX);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Workbook has been generated successfully with the"
										+ " error response list in the directory : %s",
								workbook.getAbsolutePath());
					}
				}

			}
		} catch (Exception e) {
			String msg = String.format(
					"Exception occured while "
							+ "saving excel sheet into folder, %s ",
					e.getMessage());
			LOGGER.error(msg);
			throw new AppException(e.getMessage(), e);
		}

		return workbook;

	}

}
