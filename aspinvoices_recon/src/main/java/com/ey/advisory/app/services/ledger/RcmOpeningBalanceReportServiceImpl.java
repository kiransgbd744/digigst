package com.ey.advisory.app.services.ledger;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.aspose.cells.Cells;
import com.aspose.cells.Font;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Style;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.docs.dto.ledger.CashDetailsReportRespDto;
import com.ey.advisory.app.docs.dto.ledger.CashLedgersDetailsDto;
import com.ey.advisory.app.docs.dto.ledger.CrReversalLedgerOpenCloseBalDto;
import com.ey.advisory.app.docs.dto.ledger.GetCashLedgerDetailsReqDto;
import com.ey.advisory.app.docs.dto.ledger.TransactionTypeBalDto;
import com.ey.advisory.app.util.Anx1CsvDownloadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReportConfigFactory;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.core.config.ConfigConstants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("RcmOpeningBalanceReportServiceImpl")
public class RcmOpeningBalanceReportServiceImpl
		implements CreditLedgerReportService {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Anx1CsvDownloadUtil")
	private Anx1CsvDownloadUtil anx1CsvDownloadUtil;

	@Autowired
	@Qualifier("iTCLedgerDetailsDataAtGstnImpl")
	private ITCLedgerDetailsDataAtGstn itcDataAtGstn;

	@Autowired
	@Qualifier("ProcessedReportConfigFactoryImpl")
	ReportConfigFactory reportConfigFactory;

	@Autowired
	@Qualifier("cashLedgerDetailsDataAtGstnImpl")
	private CashLedgerDetailsDataAtGstn cashDataAtGstn;

	private static final String UPLOADED_FILENAME_MSG = "Uploaded file Name :'%s'";

	public void getCreditCashAndCrRevReclaimReport(
			List<CreditLedgerBulkDetailsDto> reqDto, Long id, String fromDate,
			String toDate, String reportType) throws IOException

	{
		File tempDir = null;
		Workbook workbook = null;
		String filename1 = null;
		try {
			tempDir = createTempDir();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Inside RcmOpeningBalanceReportServiceImpl class and "
						+ "GetRcmOpeningBalanceReport method having "
						+ "reqDto as  {} ",
						reqDto.toString());
			}

			if (reportType.equalsIgnoreCase("ITC RCM Ledger (Opening Balance)")) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Inside RcmOpeningBalanceReportServiceImpl class "
							+ "and GetRcmOpeningBalanceReport method "
							+ "going inside getCreditData ",
							reqDto.toString());
				}
				filename1 = tempDir.getAbsolutePath() + File.separator
						+ "Opening Balance_RCM_Ledger" + "_" + id + ".xlsx";
				workbook = getRcmData(reqDto, id, fromDate, toDate);

			} else if (reportType.equalsIgnoreCase("ITC Reversal & Re-Claim Ledger (Opening Balance)")) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Inside CreditLedgerReportServiceImpl class and "
							+ "GetRcmOpeningBalanceReport method going "
							+ "inside getCashData ",
							reqDto.toString());
				}
				filename1 = tempDir.getAbsolutePath() + File.separator
						+ "Opening Balance_Reversal and Re-claim Ledger" + "_" + id + ".xlsx";
				workbook = getReclaimData(reqDto, id, fromDate, toDate);
			} /*else if (reportType
					.equalsIgnoreCase("Reversal & Reclaim Ledger")) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Inside CreditLedgerReportServiceImpl class and
							 getCreditCashAndCrRevReclaimReport method going 
							 inside getCreditReversalAndReclaim ",
							reqDto.toString());
				}
				filename1 = tempDir.getAbsolutePath() + File.separator
						+ "ReversalandReclaimLedgerReport" + "_" + fromDate
						+ "_" + toDate + ".xlsx";
				workbook = getCreditReversalAndReclaim(reqDto, id, fromDate,
						toDate);
			}
			if (reportType.equalsIgnoreCase("Reversal & Reclaim Ledger")) {
				reportType = "ReversalandReclaimLedger";
			}*/
			if (workbook != null) {
				workbook.save(filename1);
				// String reportname="ReversalandReclaimLedger";
				String zipFileName = zipRcmAndReclaimReports(tempDir,
						reportType, id);
				File zipFile = new File(tempDir, zipFileName);

				/*
				 * String uploadedFileName =
				 * DocumentUtility.uploadZipFile(zipFile,
				 * ConfigConstants.LIABILITY_lEDGER_REPORT_DOWNLOAD);
				 */

				Pair<String, String> uploadedFileName = DocumentUtility
						.uploadFile(zipFile, "LiabilityLedgerReport");
				String uploadedDocName = uploadedFileName.getValue0();
				String docId = uploadedFileName.getValue1();

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format(UPLOADED_FILENAME_MSG,
							uploadedFileName));
				}

				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.REPORT_GENERATED, uploadedDocName,
						LocalDateTime.now(), docId);
			}
		} catch (Exception e) {
			LOGGER.error("Exception occured in ReversalandReclaimLedger  " + e);
			throw new AppException(e);
		} finally {
			anx1CsvDownloadUtil.deleteTemporaryDirectory(tempDir);
		}
	}

	

	private String zipRcmAndReclaimReports(File tempDir, String reportType,
			Long id) {

		if (LOGGER.isDebugEnabled()) {
			String msg = " getting files to be zipped from temp directory";
			LOGGER.debug(msg);
		}
		String fileName = null;
		List<String> filesToZip = getAllFilesToBeZipped(tempDir);
		if (reportType.equalsIgnoreCase("ITC RCM Ledger (Opening Balance)")) {
			
			fileName = "Opening Balance_RCM_Ledger" + "_" + id;

		}else if (reportType.equalsIgnoreCase("ITC Reversal & Re-Claim Ledger (Opening Balance)")) {
			
			fileName = "Opening Balance_Reversal and Re-claim Ledger" + "_" + id;

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

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory(ConfigConstants.lEDGER_REPORT_DOWNLOAD)
				.toFile();
	}

	public Workbook getRcmData(List<CreditLedgerBulkDetailsDto> reqDto,
			Long id, String fromDate1, String toDate1) {

		Workbook workbook = null;
		Gson gson = new Gson();
		try {
			List<CashDetailsReportRespDto> allDetailsDto = new ArrayList<>();

			for (CreditLedgerBulkDetailsDto req : reqDto) {
				String gstin = req.getGstin();
				

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("gstin is ", gstin);
				}

				GetCashLedgerDetailsReqDto gGetCashLedgerDetailsDto = new GetCashLedgerDetailsReqDto();
				gGetCashLedgerDetailsDto.setGstin(gstin);

				String apiResp = cashDataAtGstn
						.fromRcmGstnTest(gGetCashLedgerDetailsDto);

				if (apiResp != null) {
					JsonObject requestObject = JsonParser.parseString(apiResp)
							.getAsJsonObject();
					CashLedgersDetailsDto responseDto = gson.fromJson(
							requestObject, CashLedgersDetailsDto.class);
					
					
					List<TransactionTypeBalDto> respTransDtos = responseDto
							.getTransTypeBalDto();
					
					if (respTransDtos != null) {
						respTransDtos.forEach(obj -> {
							CashDetailsReportRespDto transBal = new CashDetailsReportRespDto();
							transBal.setGstn(gstin);
							transBal.setTranDate(obj.getTransDate());
							transBal.setReferenceNo(obj.getRefNo());
							transBal.setAction(obj.getAction());
							
							CrReversalLedgerOpenCloseBalDto openBal = obj.getOpenBal();
							if (openBal != null) {
								transBal.setOpenBalIgst(openBal.getIgst().toString());
								transBal.setOpenBalCgst(openBal.getCgst().toString());
								transBal.setOpenBalSgst(openBal.getSgst().toString());
								transBal.setOpenBalCess(openBal.getCess().toString());
							}

							allDetailsDto.add(transBal);
						});
					}
				

				}
			}

			if (!allDetailsDto.isEmpty()) {
				workbook = writeToExcel(allDetailsDto);
			} else {
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.NO_DATA_FOUND, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			}

		} catch (Exception ex) {
			LOGGER.error(
					"Exception occured in credit ledger service impl" + ex);
			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			throw new AppException(ex);
		}

		return workbook;
	}

	public Workbook getReclaimData(
			List<CreditLedgerBulkDetailsDto> reqDto, Long id, String fromDate1,
			String toDate1) {

		Workbook workbook = null;
		Gson gson = new Gson();
		//final int[] srNoCounter = { 1 };
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Inside RcmOpeningBalanceReportServiceImpl class ",
						reqDto.toString());
			}
			//List<CashDetailsReportRespDto> allDetailsDto = new ArrayList<>();

			List<CashDetailsReportRespDto> allValDetailsDto = new ArrayList<>();

			for (CreditLedgerBulkDetailsDto req : reqDto) {
				String gstin = req.getGstin();
				

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("gstin is ", gstin);
				}

				GetCashLedgerDetailsReqDto gGetCashLedgerDetailsDto = new GetCashLedgerDetailsReqDto();
				gGetCashLedgerDetailsDto.setGstin(gstin);
				
				// String apiResp=null;
			/*	String apiResp = cashDataAtGstn
						.fromReclaimGstn(
								gGetCashLedgerDetailsDto);*/
				String apiResp = cashDataAtGstn
						.fromNegativeGstnTest(
								gGetCashLedgerDetailsDto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("API response from GSTIN is {}", apiResp);
				}

				if (apiResp != null) {
					JsonObject reqObj = JsonParser.parseString(apiResp)
							.getAsJsonObject();
					CashLedgersDetailsDto respDto = gson.fromJson(
							reqObj, CashLedgersDetailsDto.class);
					

					List<TransactionTypeBalDto> respTransDtos = respDto
							.getTransTypeBalDto();

					if (respTransDtos != null) {
						// AtomicInteger srNoCounter = new AtomicInteger(1);
						respTransDtos.forEach(obj -> {
							
							CashDetailsReportRespDto transBal = new CashDetailsReportRespDto();
							transBal.setGstn(gstin);
							transBal.setTranDate(obj.getTransDate());
							transBal.setReferenceNo(obj.getRefNo());
							transBal.setAction(obj.getAction());
							
							CrReversalLedgerOpenCloseBalDto openBal = obj.getOpenBal();
							if (openBal != null) {
								transBal.setOpenBalIgst(openBal.getIgst().toString());
								transBal.setOpenBalCgst(openBal.getCgst().toString());
								transBal.setOpenBalSgst(openBal.getSgst().toString());
								transBal.setOpenBalCess(openBal.getCess().toString());
							}

							allValDetailsDto.add(transBal);
						});
					}

				}
			}

			if (!allValDetailsDto.isEmpty()) {
				workbook = reclaimExcel(allValDetailsDto);
			} else {
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.NO_DATA_FOUND, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			}

		} catch (Exception ex) {
			LOGGER.error(
					"Exception occured in credit ledger service impl" + ex);
			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			throw new AppException(ex);
		}

		return workbook;
	}
	
	private Workbook writeToExcel(
			List<CashDetailsReportRespDto> allDetailsDto) {
		Workbook workbook = null;
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin .writeToExcel " + "List Size = "
					+ allDetailsDto.size();
			LOGGER.debug(msg);
		}
		try {
			if (allDetailsDto != null && !allDetailsDto.isEmpty()) {

				workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates", "RcmDetailsReport.xlsx");

				if (LOGGER.isDebugEnabled()) {
					String msg = "RcmOpeningBalanceReportServiceImpl writeToExcel "
							+ "workbook created writing data to the workbook";
					LOGGER.debug(msg);
				}

				for (int i = 0; i < 1; i++) {

					Cells reportCells = workbook.getWorksheets().get(i)
							.getCells();

					Worksheet sheet = workbook.getWorksheets().get(i);
					Style style = workbook.createStyle();
					Font font = style.getFont();
					font.setBold(true);
					font.setSize(12);

					String[] invoiceHeaders = null;

					if (!allDetailsDto.isEmpty()) {
						invoiceHeaders = commonUtility
								.getProp("ledger.rcm.reclaim.report.header")
								.split(",");
						reportCells.importCustomObjects(allDetailsDto,
								invoiceHeaders, isHeaderRequired, startRow,
								startcolumn, allDetailsDto.size(), true,
								"yyyy-mm-dd", false);

					}

					if (LOGGER.isDebugEnabled()) {
						String msg = "RcmOpeningBalanceReportServiceImpl.writeToExcel "
								+ "saving workbook";
						LOGGER.debug(msg);
					}
					int lastRowIndex = sheet.getCells().getMaxDataRow();
					sheet.getCells().deleteRow(lastRowIndex + 1);
					workbook.save(ConfigConstants.lEDGER_REPORT_DOWNLOAD,
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

	private Workbook reclaimExcel(
			List<CashDetailsReportRespDto> allDetailsDto) {
		Workbook workbook = null;
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin .writeToExcel " + "List Size = "
					+ allDetailsDto.size();
			LOGGER.debug(msg);
		}
		try {
			if (allDetailsDto != null && !allDetailsDto.isEmpty()) {

				workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates", "RcmDetailsReport.xlsx");

				if (LOGGER.isDebugEnabled()) {
					String msg = "reclaimExcel class crReclaimExcel method "
							+ "workbook created writing data to the workbook";
					LOGGER.debug(msg);
				}

				for (int i = 0; i < 1; i++) {

					Cells reportCells = workbook.getWorksheets().get(i)
							.getCells();

					Worksheet sheet = workbook.getWorksheets().get(i);
					Style style = workbook.createStyle();
					Font font = style.getFont();
					font.setBold(true);
					font.setSize(12);
					/*Cell cell2 = sheet.getCells().get("B3");
					Cell cell3 = sheet.getCells().get("B4");
					cell2.setValue(fromdate);
					cell3.setValue(toDate);*/
					String[] invoiceHeaders = null;

					if (!allDetailsDto.isEmpty()) {
						invoiceHeaders = commonUtility
								.getProp("ledger.rcm.reclaim.report.header")
								.split(",");
						reportCells.importCustomObjects(allDetailsDto,
								invoiceHeaders, isHeaderRequired, startRow,
								startcolumn, allDetailsDto.size(), true,
								"yyyy-mm-dd", false);

					}

					if (LOGGER.isDebugEnabled()) {
						String msg = "RcmOpeningBalanceReportServiceImpl.reclaimExcel "
								+ "saving workbook";
						LOGGER.debug(msg);
					}
					int lastRowIndex = sheet.getCells().getMaxDataRow();
					sheet.getCells().deleteRow(lastRowIndex + 1);
					workbook.save(ConfigConstants.lEDGER_REPORT_DOWNLOAD,
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
