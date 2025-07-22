/**
 * kiran 
 */
package com.ey.advisory.app.services.ledger;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.Font;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Style;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.docs.dto.ledger.CashDetailsReportRespDto;
import com.ey.advisory.app.docs.dto.ledger.CashLedgerOpenCloseBalDto;
import com.ey.advisory.app.docs.dto.ledger.CashLedgersDetailsDto;
import com.ey.advisory.app.docs.dto.ledger.CrReversalLedgerOpenCloseBalDto;
import com.ey.advisory.app.docs.dto.ledger.CreditRevAndReclaimTransactionDto;
import com.ey.advisory.app.docs.dto.ledger.CreditReverseAndReclaimLedgerDto;
import com.ey.advisory.app.docs.dto.ledger.GetCashLedgerDetailsReqDto;
import com.ey.advisory.app.docs.dto.ledger.ItcCrReversalAndReclaimDto;
import com.ey.advisory.app.docs.dto.ledger.ItcLedgerDetailsDto;
import com.ey.advisory.app.docs.dto.ledger.ItcLedgerOpenCloseBalDto;
import com.ey.advisory.app.docs.dto.ledger.ItcTransactionTypeBalDto;
import com.ey.advisory.app.docs.dto.ledger.LedgerItcReclaimBalanceAmts;
import com.ey.advisory.app.docs.dto.ledger.NegativeDetailedLedgerDetailsDto;
import com.ey.advisory.app.docs.dto.ledger.NegativeDetailedLedgerTransactionDto;
import com.ey.advisory.app.docs.dto.ledger.NegativeDetailsRespDto;
import com.ey.advisory.app.docs.dto.ledger.RcmDetailedLedgerDetailsDto;
import com.ey.advisory.app.docs.dto.ledger.RcmDetailedLedgerTransactionDto;
import com.ey.advisory.app.docs.dto.ledger.RcmDetailsRespDto;
import com.ey.advisory.app.docs.dto.ledger.TransactionTypeBalDto;
import com.ey.advisory.app.util.Anx1CsvDownloadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ReportConfigFactory;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.core.config.ConfigConstants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("CreditLedgerReportServiceImpl")
public class CreditLedgerReportServiceImpl
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
						"Inside CreditLedgerReportServiceImpl class and getCreditCashAndCrRevReclaimReport method having reqDto as  {} for credit reversal and reclaim ledger report",
						reqDto.toString());
			}

			if (reportType.equalsIgnoreCase("Credit Ledger")) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Inside CreditLedgerReportServiceImpl class and getCreditCashAndCrRevReclaimReport method going inside getCreditData ",
							reqDto.toString());
				}
				filename1 = tempDir.getAbsolutePath() + File.separator
						+ reportType + "_" + fromDate + "_" + toDate + ".xlsx";
				workbook = getCreditData(reqDto, id, fromDate, toDate);

			} else if (reportType.equalsIgnoreCase("Cash Ledger")) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Inside CreditLedgerReportServiceImpl class and getCreditCashAndCrRevReclaimReport method going inside getCashData ",
							reqDto.toString());
				}
				filename1 = tempDir.getAbsolutePath() + File.separator
						+ reportType + "_" + fromDate + "_" + toDate + ".xlsx";
				workbook = getCashData(reqDto, id, fromDate, toDate);
			} else if (reportType
					.equalsIgnoreCase("Reversal & Reclaim Ledger")) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Inside CreditLedgerReportServiceImpl class and getCreditCashAndCrRevReclaimReport method going inside getCreditReversalAndReclaim ",
							reqDto.toString());
				}
				filename1 = tempDir.getAbsolutePath() + File.separator
						+ "ReversalandReclaimLedgerReport" + "_" + fromDate
						+ "_" + toDate + ".xlsx";
				workbook = getCreditReversalAndReclaim(reqDto, id, fromDate,
						toDate);
			} else if (reportType
					.equalsIgnoreCase("Reversal & Reclaim Ledger")) {
				reportType = "ReversalandReclaimLedger";
			}

			// RCM ledger
			else if (reportType.equalsIgnoreCase("ITC RCM Ledger")) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Inside CreditLedgerReportServiceImpl class and getCreditCashAndCrRevReclaimReport method going inside RCM Entity level ledger code ",
							reqDto.toString());
				}
				filename1 = tempDir.getAbsolutePath() + File.separator
						+ "RCMLedger_Report" + "_" + id + "_" + fromDate + "_"
						+ toDate + ".xlsx";
				workbook = getRcmLedgerDetails(reqDto, id, fromDate, toDate);
			} else if (reportType.equalsIgnoreCase("ITC RCM Ledger")) {
				reportType = "RCMLedger";
			}

			// negative ledger
			else if (reportType.equalsIgnoreCase("Negative Liability Ledger")) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Inside CreditLedgerReportServiceImpl class and getCreditCashAndCrRevReclaimReport method going inside Negative Liability Ledger code ",
							reqDto.toString());
				}
				filename1 = tempDir.getAbsolutePath() + File.separator
						+ "NegativeLiabilityLedger_Report" + "_" + fromDate
						+ "_" + toDate + ".xlsx";
				workbook = getNegativeLedgerDetails(reqDto, id, fromDate,
						toDate);
			} else if (reportType
					.equalsIgnoreCase("Negative Liability Ledger")) {
				reportType = "NegativeLedger";
			}
			if (workbook != null) {
				workbook.save(filename1);
				String zipFileName = zipCreditAndCashReports(tempDir,
						reportType, id);
				File zipFile = new File(tempDir, zipFileName);

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

	public Workbook getCreditData(List<CreditLedgerBulkDetailsDto> reqDto,
			Long id, String fromDate1, String toDate1) {

		Workbook workbook = null;
		Gson gson = new Gson();
		try {
			List<ItcDetailsRespDto> allDetailsDto = new ArrayList<>();

			for (CreditLedgerBulkDetailsDto req : reqDto) {
				String gstin = req.getGstin();
				String fromDate = fromDate1;
				String toDate = toDate1;

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("gstn and fromdate and todate is %s%s%s",
							gstin, fromDate, toDate);
				}

				GetCashLedgerDetailsReqDto gGetCreditLedgerDetailsDto = new GetCashLedgerDetailsReqDto();
				gGetCreditLedgerDetailsDto.setGstin(gstin);
				gGetCreditLedgerDetailsDto.setFromDate(fromDate);
				gGetCreditLedgerDetailsDto.setToDate(toDate);

				String apiResp = itcDataAtGstn
						.fromGstnTest(gGetCreditLedgerDetailsDto);

				/*
				 * if (apiResp == null) {
				 * fileStatusDownloadReportRepo.updateStatus(id,
				 * ReportStatusConstants.NO_DATA_FOUND, null,
				 * EYDateUtil.toUTCDateTimeFromLocal( LocalDateTime.now())); }
				 */

				if (apiResp != null) {
					JsonObject requestObject = JsonParser.parseString(apiResp)
							.getAsJsonObject();
					JsonObject reqObject = requestObject.get("itcLdgDtls")
							.getAsJsonObject();
					ItcLedgerDetailsDto responseDto = gson.fromJson(reqObject,
							ItcLedgerDetailsDto.class);

					ItcLedgerOpenCloseBalDto closeBal = responseDto
							.getClosenBal();
					ItcLedgerOpenCloseBalDto openBal = responseDto.getOpenBal();
					List<ItcTransactionTypeBalDto> respTransDtos = responseDto
							.getItctransTypeBalDto();

					if (openBal != null) {
						ItcDetailsRespDto respOpenBal = new ItcDetailsRespDto();
						respOpenBal.setDesc(openBal.getDescription());
						respOpenBal.setBalIgst(GenUtil
								.formatCurrency(openBal.getIgstTaxBal() != null
										? openBal.getIgstTaxBal()
										: "0"));
						respOpenBal.setBalCgst(GenUtil
								.formatCurrency(openBal.getCgstTaxBal() != null
										? openBal.getCgstTaxBal()
										: "0"));
						respOpenBal.setBalSgst(GenUtil
								.formatCurrency(openBal.getSgstTaxBal() != null
										? openBal.getSgstTaxBal()
										: "0"));
						respOpenBal.setBalCess(GenUtil
								.formatCurrency(openBal.getCessTaxBal() != null
										? openBal.getCessTaxBal()
										: "0"));
						respOpenBal.setBalTotal(GenUtil
								.formatCurrency(openBal.getTotRngBal() != null
										? openBal.getTotRngBal()
										: "0"));

						respOpenBal.setGstn(gstin);
						allDetailsDto.add(respOpenBal);
					}
					if (respTransDtos != null) {
						respTransDtos.forEach(obj -> {
							ItcDetailsRespDto transBal = new ItcDetailsRespDto();
							transBal.setItcTransDate(obj.getItcTransDate());
							transBal.setRefNo(obj.getReferenceNo());
							transBal.setTaxPeriod(obj.getRetPeriod());
							transBal.setDesc(obj.getDescription());
							transBal.setTransType(obj.getTransType());
							transBal.setBalIgst(GenUtil
									.formatCurrency(obj.getIgstTaxBal() != null
											? obj.getIgstTaxBal()
											: "0"));
							transBal.setBalSgst(GenUtil
									.formatCurrency(obj.getSgstTaxBal() != null
											? obj.getSgstTaxBal()
											: "0"));
							transBal.setBalCgst(GenUtil
									.formatCurrency(obj.getCgstTaxBal() != null
											? obj.getCgstTaxBal()
											: "0"));
							transBal.setBalCess(GenUtil
									.formatCurrency(obj.getCessTaxBal() != null
											? obj.getCessTaxBal()
											: "0"));
							transBal.setBalTotal(GenUtil
									.formatCurrency(obj.getTotRngBal() != null
											? obj.getTotRngBal()
											: "0"));
							transBal.setCrDrIgst(GenUtil
									.formatCurrency(obj.getIgstTaxAmt() != null
											? obj.getIgstTaxAmt()
											: "0"));
							transBal.setCrDrCgst(GenUtil
									.formatCurrency(obj.getCgstTaxAmt() != null
											? obj.getCgstTaxAmt()
											: "0"));
							transBal.setCrDrSgst(GenUtil
									.formatCurrency(obj.getSgstTaxAmt() != null
											? obj.getSgstTaxAmt()
											: "0"));
							transBal.setCrDrCess(GenUtil
									.formatCurrency(obj.getCessTaxAmt() != null
											? obj.getCessTaxAmt()
											: "0"));
							transBal.setCrDrTotal(GenUtil
									.formatCurrency(obj.getTotTrAmt() != null
											? obj.getTotTrAmt()
											: "0"));

							transBal.setGstn(gstin);
							allDetailsDto.add(transBal);
						});
					}

					if (closeBal != null) {
						ItcDetailsRespDto respCloseBal = new ItcDetailsRespDto();
						respCloseBal.setDesc(closeBal.getDescription());
						respCloseBal.setBalIgst(GenUtil
								.formatCurrency(closeBal.getIgstTaxBal() != null
										? closeBal.getIgstTaxBal()
										: "0"));
						respCloseBal.setBalCgst(GenUtil
								.formatCurrency(closeBal.getCgstTaxBal() != null
										? closeBal.getCgstTaxBal()
										: "0"));
						respCloseBal.setBalSgst(GenUtil
								.formatCurrency(closeBal.getSgstTaxBal() != null
										? closeBal.getSgstTaxBal()
										: "0"));
						respCloseBal.setBalCess(GenUtil
								.formatCurrency(closeBal.getCessTaxBal() != null
										? closeBal.getCessTaxBal()
										: "0"));
						respCloseBal.setBalTotal(GenUtil
								.formatCurrency(closeBal.getTotRngBal() != null
										? closeBal.getTotRngBal()
										: "0"));

						respCloseBal.setGstn(gstin);
						allDetailsDto.add(respCloseBal);
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

	private Workbook writeToExcel(List<ItcDetailsRespDto> allDetailsDto) {
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
						"ReportTemplates", "CreditLedgerReport.xlsx");

				if (LOGGER.isDebugEnabled()) {
					String msg = "CreditLedgerReportServiceImpl writeToExcel "
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
								.getProp("ledger.credit.report.header")
								.split(",");
						reportCells.importCustomObjects(allDetailsDto,
								invoiceHeaders, isHeaderRequired, startRow,
								startcolumn, allDetailsDto.size(), true,
								"yyyy-mm-dd", false);

					}

					if (LOGGER.isDebugEnabled()) {
						String msg = "CreditLedgerReportServiceImpl.writeToExcel "
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

	private Workbook writeToExcel1(
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
						"ReportTemplates", "CashLedgerReport.xlsx");

				if (LOGGER.isDebugEnabled()) {
					String msg = "CreditLedgerReportServiceImpl writeToExcel1 "
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
								.getProp("ledger.Cash.report.header")
								.split(",");
						reportCells.importCustomObjects(allDetailsDto,
								invoiceHeaders, isHeaderRequired, startRow,
								startcolumn, allDetailsDto.size(), true,
								"yyyy-mm-dd", false);

					}

					if (LOGGER.isDebugEnabled()) {
						String msg = "CreditLedgerReportServiceImpl.writeToExcel "
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

	private String zipCreditAndCashReports(File tempDir, String reportType,
			Long id) {

		if (LOGGER.isDebugEnabled()) {
			String msg = " getting files to be zipped from temp directory";
			LOGGER.debug(msg);
		}
		String fileName = null;
		List<String> filesToZip = getAllFilesToBeZipped(tempDir);

		fileName = reportType + "_" + "Report" + "_" + id;

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

	private String zipReversalAndReclaimReports(File tempDir, String reportType,
			Long id) {

		if (LOGGER.isDebugEnabled()) {
			String msg = " getting files to be zipped from temp directory";
			LOGGER.debug(msg);
		}
		String fileName = null;
		List<String> filesToZip = getAllFilesToBeZipped(tempDir);

		fileName = reportType + "_" + id;

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

	public Workbook getCashData(List<CreditLedgerBulkDetailsDto> reqDto,
			Long id, String fromDate1, String toDate1) {

		Workbook workbook = null;
		Gson gson = new Gson();
		try {
			List<CashDetailsReportRespDto> allDetailsDto = new ArrayList<>();

			for (CreditLedgerBulkDetailsDto req : reqDto) {
				String gstin = req.getGstin();
				String fromDate = fromDate1;
				String toDate = toDate1;

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("gstn and fromdate and todate is ", gstin,
							fromDate, toDate);
				}

				GetCashLedgerDetailsReqDto gGetCashLedgerDetailsDto = new GetCashLedgerDetailsReqDto();
				gGetCashLedgerDetailsDto.setGstin(gstin);
				gGetCashLedgerDetailsDto.setFromDate(fromDate);
				gGetCashLedgerDetailsDto.setToDate(toDate);

				String apiResp = cashDataAtGstn
						.fromGstn(gGetCashLedgerDetailsDto);

				if (apiResp != null) {
					JsonObject requestObject = JsonParser.parseString(apiResp)
							.getAsJsonObject();
					CashLedgersDetailsDto responseDto = gson.fromJson(
							requestObject, CashLedgersDetailsDto.class);
					CashLedgerOpenCloseBalDto closeBal = responseDto
							.getClosenBal();
					CashLedgerOpenCloseBalDto openBal = responseDto
							.getOpenBal();
					List<TransactionTypeBalDto> respTransDtos = responseDto
							.getTransTypeBalDto();
					if (openBal != null) {
						CashDetailsReportRespDto respOpenBal = new CashDetailsReportRespDto();

						respOpenBal.setGstn(gstin);
						respOpenBal.setDescription(openBal.getDescription());
						// cess bal
						respOpenBal.setCessBalTaxValue(GenUtil.formatCurrency(
								openBal.getCessbal().getTaxValue() != null
										? openBal.getCessbal().getTaxValue()
										: "0"));
						respOpenBal.setCessBalInterestValue(
								GenUtil.formatCurrency(openBal.getCessbal()
										.getInterestValue() != null
												? openBal.getCessbal()
														.getInterestValue()
												: "0"));
						respOpenBal.setCessBalPenalty(GenUtil.formatCurrency(
								openBal.getCessbal().getPenalty() != null
										? openBal.getCessbal().getPenalty()
										: "0"));
						respOpenBal.setCessBalFees(GenUtil.formatCurrency(
								openBal.getCessbal().getFees() != null
										? openBal.getCessbal().getFees()
										: "0"));
						respOpenBal.setCessBalOther(GenUtil.formatCurrency(
								openBal.getCessbal().getOther() != null
										? openBal.getCessbal().getOther()
										: "0"));
						respOpenBal.setCessBalTotal(GenUtil.formatCurrency(
								openBal.getCessbal().getTotal() != null
										? openBal.getCessbal().getTotal()
										: "0"));

						// igst bal
						respOpenBal.setIgstBalTaxValue(GenUtil.formatCurrency(
								openBal.getIgstbal().getTaxValue() != null
										? openBal.getIgstbal().getTaxValue()
										: "0"));
						respOpenBal.setIgstBalInterestValue(
								GenUtil.formatCurrency(openBal.getIgstbal()
										.getInterestValue() != null
												? openBal.getIgstbal()
														.getInterestValue()
												: "0"));
						respOpenBal.setIgstBalPenalty(GenUtil.formatCurrency(
								openBal.getIgstbal().getPenalty() != null
										? openBal.getIgstbal().getPenalty()
										: "0"));
						respOpenBal.setIgstBalFees(GenUtil.formatCurrency(
								openBal.getIgstbal().getFees() != null
										? openBal.getIgstbal().getFees()
										: "0"));
						respOpenBal.setIgstBalOther(GenUtil.formatCurrency(
								openBal.getIgstbal().getOther() != null
										? openBal.getIgstbal().getOther()
										: "0"));
						respOpenBal.setIgstBalTotal(GenUtil.formatCurrency(
								openBal.getIgstbal().getTotal() != null
										? openBal.getIgstbal().getTotal()
										: "0"));

						// cgst bal
						respOpenBal.setCgstBalTaxValue(GenUtil.formatCurrency(
								openBal.getCgstbal().getTaxValue() != null
										? openBal.getCgstbal().getTaxValue()
										: "0"));
						respOpenBal.setCgstBalInterestValue(
								GenUtil.formatCurrency(openBal.getCgstbal()
										.getInterestValue() != null
												? openBal.getCgstbal()
														.getInterestValue()
												: "0"));
						respOpenBal.setCgstBalPenalty(GenUtil.formatCurrency(
								openBal.getCgstbal().getPenalty() != null
										? openBal.getCgstbal().getPenalty()
										: "0"));
						respOpenBal.setCgstBalFees(GenUtil.formatCurrency(
								openBal.getCgstbal().getFees() != null
										? openBal.getCgstbal().getFees()
										: "0"));
						respOpenBal.setCgstBalOther(GenUtil.formatCurrency(
								openBal.getCgstbal().getOther() != null
										? openBal.getCgstbal().getOther()
										: "0"));
						respOpenBal.setCgstBalTotal(GenUtil.formatCurrency(
								openBal.getCgstbal().getTotal() != null
										? openBal.getCgstbal().getTotal()
										: "0"));

						// sgst
						respOpenBal.setSgstBalTaxValue(GenUtil.formatCurrency(
								openBal.getSgstbal().getTaxValue() != null
										? openBal.getSgstbal().getTaxValue()
										: "0"));
						respOpenBal.setSgstBalInterestValue(
								GenUtil.formatCurrency(openBal.getSgstbal()
										.getInterestValue() != null
												? openBal.getSgstbal()
														.getInterestValue()
												: "0"));
						respOpenBal.setSgstBalPenalty(GenUtil.formatCurrency(
								openBal.getSgstbal().getPenalty() != null
										? openBal.getSgstbal().getPenalty()
										: "0"));
						respOpenBal.setSgstBalFees(GenUtil.formatCurrency(
								openBal.getSgstbal().getFees() != null
										? openBal.getSgstbal().getFees()
										: "0"));
						respOpenBal.setSgstBalOther(GenUtil.formatCurrency(
								openBal.getSgstbal().getOther() != null
										? openBal.getSgstbal().getOther()
										: "0"));
						respOpenBal.setSgstBalTotal(GenUtil.formatCurrency(
								openBal.getSgstbal().getTotal() != null
										? openBal.getSgstbal().getTotal()
										: "0"));

						allDetailsDto.add(respOpenBal);
					}
					if (respTransDtos != null) {
						respTransDtos.forEach(obj -> {
							CashDetailsReportRespDto transBal = new CashDetailsReportRespDto();
							transBal.setGstn(gstin);
							transBal.setDptDate(obj.getDptDate());
							transBal.setDptTime(obj.getDptTime());
							transBal.setRptDate(obj.getRptDate());
							transBal.setReferenceNo(obj.getReferenceNo());
							transBal.setRetPeriod(obj.getRetPeriod());
							transBal.setDescription(obj.getDescription());
							transBal.setTransType(obj.getTransType());
							// igst
							transBal.setIgstTaxValue(GenUtil.formatCurrency(
									obj.getIgst().getTaxValue() != null
											? obj.getIgst().getTaxValue()
											: "0"));
							transBal.setIgstInterestValue(
									GenUtil.formatCurrency(obj.getIgst()
											.getInterestValue() != null
													? obj.getIgst()
															.getInterestValue()
													: "0"));
							transBal.setIgstPenalty(GenUtil.formatCurrency(
									obj.getIgst().getPenalty() != null
											? obj.getIgst().getPenalty()
											: "0"));
							transBal.setIgstFees(GenUtil.formatCurrency(
									obj.getIgst().getFees() != null
											? obj.getIgst().getFees()
											: "0"));
							transBal.setIgstOther(GenUtil.formatCurrency(
									obj.getIgst().getOther() != null
											? obj.getIgst().getOther()
											: "0"));
							transBal.setIgstTotal(GenUtil.formatCurrency(
									obj.getIgst().getTotal() != null
											? obj.getIgst().getTotal()
											: "0"));

							// IGST Balance
							transBal.setIgstBalTaxValue(GenUtil.formatCurrency(
									obj.getIgstBal().getTaxValue() != null
											? obj.getIgstBal().getTaxValue()
											: "0"));
							transBal.setIgstBalInterestValue(
									GenUtil.formatCurrency(obj.getIgstBal()
											.getInterestValue() != null
													? obj.getIgstBal()
															.getInterestValue()
													: "0"));
							transBal.setIgstBalPenalty(GenUtil.formatCurrency(
									obj.getIgstBal().getPenalty() != null
											? obj.getIgstBal().getPenalty()
											: "0"));
							transBal.setIgstBalFees(GenUtil.formatCurrency(
									obj.getIgstBal().getFees() != null
											? obj.getIgstBal().getFees()
											: "0"));
							transBal.setIgstBalOther(GenUtil.formatCurrency(
									obj.getIgstBal().getOther() != null
											? obj.getIgstBal().getOther()
											: "0"));
							transBal.setIgstBalTotal(GenUtil.formatCurrency(
									obj.getIgstBal().getTotal() != null
											? obj.getIgstBal().getTotal()
											: "0"));

							// CGST
							transBal.setCgstTaxValue(GenUtil.formatCurrency(
									obj.getCgst().getTaxValue() != null
											? obj.getCgst().getTaxValue()
											: "0"));
							transBal.setCgstInterestValue(
									GenUtil.formatCurrency(obj.getCgst()
											.getInterestValue() != null
													? obj.getCgst()
															.getInterestValue()
													: "0"));
							transBal.setCgstPenalty(GenUtil.formatCurrency(
									obj.getCgst().getPenalty() != null
											? obj.getCgst().getPenalty()
											: "0"));
							transBal.setCgstFees(GenUtil.formatCurrency(
									obj.getCgst().getFees() != null
											? obj.getCgst().getFees()
											: "0"));
							transBal.setCgstOther(GenUtil.formatCurrency(
									obj.getCgst().getOther() != null
											? obj.getCgst().getOther()
											: "0"));
							transBal.setCgstTotal(GenUtil.formatCurrency(
									obj.getCgst().getTotal() != null
											? obj.getCgst().getTotal()
											: "0"));

							// CGST Balance
							transBal.setCgstBalTaxValue(GenUtil.formatCurrency(
									obj.getCgstBal().getTaxValue() != null
											? obj.getCgstBal().getTaxValue()
											: "0"));
							transBal.setCgstBalInterestValue(
									GenUtil.formatCurrency(obj.getCgstBal()
											.getInterestValue() != null
													? obj.getCgstBal()
															.getInterestValue()
													: "0"));
							transBal.setCgstBalPenalty(GenUtil.formatCurrency(
									obj.getCgstBal().getPenalty() != null
											? obj.getCgstBal().getPenalty()
											: "0"));
							transBal.setCgstBalFees(GenUtil.formatCurrency(
									obj.getCgstBal().getFees() != null
											? obj.getCgstBal().getFees()
											: "0"));
							transBal.setCgstBalOther(GenUtil.formatCurrency(
									obj.getCgstBal().getOther() != null
											? obj.getCgstBal().getOther()
											: "0"));
							transBal.setCgstBalTotal(GenUtil.formatCurrency(
									obj.getCgstBal().getTotal() != null
											? obj.getCgstBal().getTotal()
											: "0"));

							// SGST
							transBal.setSgstTaxValue(GenUtil.formatCurrency(
									obj.getSgst().getTaxValue() != null
											? obj.getSgst().getTaxValue()
											: "0"));
							transBal.setSgstInterestValue(
									GenUtil.formatCurrency(obj.getSgst()
											.getInterestValue() != null
													? obj.getSgst()
															.getInterestValue()
													: "0"));
							transBal.setSgstPenalty(GenUtil.formatCurrency(
									obj.getSgst().getPenalty() != null
											? obj.getSgst().getPenalty()
											: "0"));
							transBal.setSgstFees(GenUtil.formatCurrency(
									obj.getSgst().getFees() != null
											? obj.getSgst().getFees()
											: "0"));
							transBal.setSgstOther(GenUtil.formatCurrency(
									obj.getSgst().getOther() != null
											? obj.getSgst().getOther()
											: "0"));
							transBal.setSgstTotal(GenUtil.formatCurrency(
									obj.getSgst().getTotal() != null
											? obj.getSgst().getTotal()
											: "0"));
							// sgst

							// sgst bal
							// SGST Balance
							transBal.setSgstBalTaxValue(GenUtil.formatCurrency(
									obj.getSgstBal().getTaxValue() != null
											? obj.getSgstBal().getTaxValue()
											: "0"));
							transBal.setSgstBalInterestValue(
									GenUtil.formatCurrency(obj.getSgstBal()
											.getInterestValue() != null
													? obj.getSgstBal()
															.getInterestValue()
													: "0"));
							transBal.setSgstBalPenalty(GenUtil.formatCurrency(
									obj.getSgstBal().getPenalty() != null
											? obj.getSgstBal().getPenalty()
											: "0"));
							transBal.setSgstBalFees(GenUtil.formatCurrency(
									obj.getSgstBal().getFees() != null
											? obj.getSgstBal().getFees()
											: "0"));
							transBal.setSgstBalOther(GenUtil.formatCurrency(
									obj.getSgstBal().getOther() != null
											? obj.getSgstBal().getOther()
											: "0"));
							transBal.setSgstBalTotal(GenUtil.formatCurrency(
									obj.getSgstBal().getTotal() != null
											? obj.getSgstBal().getTotal()
											: "0"));

							// Cess
							transBal.setCessTaxValue(GenUtil.formatCurrency(
									obj.getCess().getTaxValue() != null
											? obj.getCess().getTaxValue()
											: "0"));
							transBal.setCessInterestValue(
									GenUtil.formatCurrency(obj.getCess()
											.getInterestValue() != null
													? obj.getCess()
															.getInterestValue()
													: "0"));
							transBal.setCessPenalty(GenUtil.formatCurrency(
									obj.getCess().getPenalty() != null
											? obj.getCess().getPenalty()
											: "0"));
							transBal.setCessFees(GenUtil.formatCurrency(
									obj.getCess().getFees() != null
											? obj.getCess().getFees()
											: "0"));
							transBal.setCessOther(GenUtil.formatCurrency(
									obj.getCess().getOther() != null
											? obj.getCess().getOther()
											: "0"));
							transBal.setCessTotal(GenUtil.formatCurrency(
									obj.getCess().getTotal() != null
											? obj.getCess().getTotal()
											: "0"));

							// Cess Balance
							transBal.setCessBalTaxValue(GenUtil.formatCurrency(
									obj.getCessBal().getTaxValue() != null
											? obj.getCessBal().getTaxValue()
											: "0"));
							transBal.setCessBalInterestValue(
									GenUtil.formatCurrency(obj.getCessBal()
											.getInterestValue() != null
													? obj.getCessBal()
															.getInterestValue()
													: "0"));
							transBal.setCessBalPenalty(GenUtil.formatCurrency(
									obj.getCessBal().getPenalty() != null
											? obj.getCessBal().getPenalty()
											: "0"));
							transBal.setCessBalFees(GenUtil.formatCurrency(
									obj.getCessBal().getFees() != null
											? obj.getCessBal().getFees()
											: "0"));
							transBal.setCessBalOther(GenUtil.formatCurrency(
									obj.getCessBal().getOther() != null
											? obj.getCessBal().getOther()
											: "0"));
							transBal.setCessBalTotal(GenUtil.formatCurrency(
									obj.getCessBal().getTotal() != null
											? obj.getCessBal().getTotal()
											: "0"));

							allDetailsDto.add(transBal);
						});
					}
					if (closeBal != null) {
						CashDetailsReportRespDto respCloseBal = new CashDetailsReportRespDto();
						respCloseBal.setDescription(closeBal.getDescription());

						// Cess Balance
						respCloseBal.setCessBalTaxValue(GenUtil.formatCurrency(
								closeBal.getCessbal().getTaxValue() != null
										? closeBal.getCessbal().getTaxValue()
										: "0"));
						respCloseBal.setCessBalInterestValue(
								GenUtil.formatCurrency(closeBal.getCessbal()
										.getInterestValue() != null
												? closeBal.getCessbal()
														.getInterestValue()
												: "0"));
						respCloseBal.setCessBalPenalty(GenUtil.formatCurrency(
								closeBal.getCessbal().getPenalty() != null
										? closeBal.getCessbal().getPenalty()
										: "0"));
						respCloseBal.setCessBalFees(GenUtil.formatCurrency(
								closeBal.getCessbal().getFees() != null
										? closeBal.getCessbal().getFees()
										: "0"));
						respCloseBal.setCessBalOther(GenUtil.formatCurrency(
								closeBal.getCessbal().getOther() != null
										? closeBal.getCessbal().getOther()
										: "0"));
						respCloseBal.setCessBalTotal(GenUtil.formatCurrency(
								closeBal.getCessbal().getTotal() != null
										? closeBal.getCessbal().getTotal()
										: "0"));

						// IGST Balance
						respCloseBal.setIgstBalTaxValue(GenUtil.formatCurrency(
								closeBal.getIgstbal().getTaxValue() != null
										? closeBal.getIgstbal().getTaxValue()
										: "0"));
						respCloseBal.setIgstBalInterestValue(
								GenUtil.formatCurrency(closeBal.getIgstbal()
										.getInterestValue() != null
												? closeBal.getIgstbal()
														.getInterestValue()
												: "0"));
						respCloseBal.setIgstBalPenalty(GenUtil.formatCurrency(
								closeBal.getIgstbal().getPenalty() != null
										? closeBal.getIgstbal().getPenalty()
										: "0"));
						respCloseBal.setIgstBalFees(GenUtil.formatCurrency(
								closeBal.getIgstbal().getFees() != null
										? closeBal.getIgstbal().getFees()
										: "0"));
						respCloseBal.setIgstBalOther(GenUtil.formatCurrency(
								closeBal.getIgstbal().getOther() != null
										? closeBal.getIgstbal().getOther()
										: "0"));
						respCloseBal.setIgstBalTotal(GenUtil.formatCurrency(
								closeBal.getIgstbal().getTotal() != null
										? closeBal.getIgstbal().getTotal()
										: "0"));

						// CGST Balance
						respCloseBal.setCgstBalTaxValue(GenUtil.formatCurrency(
								closeBal.getCgstbal().getTaxValue() != null
										? closeBal.getCgstbal().getTaxValue()
										: "0"));
						respCloseBal.setCgstBalInterestValue(
								GenUtil.formatCurrency(closeBal.getCgstbal()
										.getInterestValue() != null
												? closeBal.getCgstbal()
														.getInterestValue()
												: "0"));
						respCloseBal.setCgstBalPenalty(GenUtil.formatCurrency(
								closeBal.getCgstbal().getPenalty() != null
										? closeBal.getCgstbal().getPenalty()
										: "0"));
						respCloseBal.setCgstBalFees(GenUtil.formatCurrency(
								closeBal.getCgstbal().getFees() != null
										? closeBal.getCgstbal().getFees()
										: "0"));
						respCloseBal.setCgstBalOther(GenUtil.formatCurrency(
								closeBal.getCgstbal().getOther() != null
										? closeBal.getCgstbal().getOther()
										: "0"));
						respCloseBal.setCgstBalTotal(GenUtil.formatCurrency(
								closeBal.getCgstbal().getTotal() != null
										? closeBal.getCgstbal().getTotal()
										: "0"));

						// SGST Balance
						respCloseBal.setSgstBalTaxValue(GenUtil.formatCurrency(
								closeBal.getSgstbal().getTaxValue() != null
										? closeBal.getSgstbal().getTaxValue()
										: "0"));
						respCloseBal.setSgstBalInterestValue(
								GenUtil.formatCurrency(closeBal.getSgstbal()
										.getInterestValue() != null
												? closeBal.getSgstbal()
														.getInterestValue()
												: "0"));
						respCloseBal.setSgstBalPenalty(GenUtil.formatCurrency(
								closeBal.getSgstbal().getPenalty() != null
										? closeBal.getSgstbal().getPenalty()
										: "0"));
						respCloseBal.setSgstBalFees(GenUtil.formatCurrency(
								closeBal.getSgstbal().getFees() != null
										? closeBal.getSgstbal().getFees()
										: "0"));
						respCloseBal.setSgstBalOther(GenUtil.formatCurrency(
								closeBal.getSgstbal().getOther() != null
										? closeBal.getSgstbal().getOther()
										: "0"));
						respCloseBal.setSgstBalTotal(GenUtil.formatCurrency(
								closeBal.getSgstbal().getTotal() != null
										? closeBal.getSgstbal().getTotal()
										: "0"));

						allDetailsDto.add(respCloseBal);
					}

				}
			}

			if (!allDetailsDto.isEmpty()) {
				workbook = writeToExcel1(allDetailsDto);
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

	public Workbook getCreditReversalAndReclaim(
			List<CreditLedgerBulkDetailsDto> reqDto, Long id, String fromDate1,
			String toDate1) {

		Workbook workbook = null;
		Gson gson = new Gson();
		final int[] srNoCounter = { 1 };
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Inside CreditLedgerReportServiceImpl class and inside getCreditCashAndCrRevReclaimReport method ",
						reqDto.toString());
			}
			List<CashDetailsReportRespDto> allDetailsDto = new ArrayList<>();

			List<ItcCrReversalAndReclaimDto> allValDetailsDto = new ArrayList<>();

			for (CreditLedgerBulkDetailsDto req : reqDto) {
				String gstin = req.getGstin();
				String fromDate = fromDate1;
				String toDate = toDate1;

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("gstn and fromdate and todate is ", gstin,
							fromDate, toDate);
				}

				GetCashLedgerDetailsReqDto gGetCashLedgerDetailsDto = new GetCashLedgerDetailsReqDto();
				gGetCashLedgerDetailsDto.setGstin(gstin);
				gGetCashLedgerDetailsDto.setFromDate(fromDate);
				gGetCashLedgerDetailsDto.setToDate(toDate);
				// String apiResp=null;
				String apiResp = cashDataAtGstn
						.getCreditReversalAndReclaimfromGstnTest(
								gGetCashLedgerDetailsDto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("AsPI response from GSTIN is {}", apiResp);
				}

				if (apiResp != null) {
					JsonObject reqObj = JsonParser.parseString(apiResp)
							.getAsJsonObject();
					CreditReverseAndReclaimLedgerDto respDto = gson.fromJson(
							reqObj, CreditReverseAndReclaimLedgerDto.class);
					CrReversalLedgerOpenCloseBalDto closeBal = respDto
							.getClosenBal();

					List<CreditRevAndReclaimTransactionDto> respTransDtos = respDto
							.getTransTypeBalDto();

					if (respTransDtos != null) {
						// AtomicInteger srNoCounter = new AtomicInteger(1);
						respTransDtos.forEach(obj -> {
							ItcCrReversalAndReclaimDto transBal = new ItcCrReversalAndReclaimDto();
							transBal.setSrNo(srNoCounter[0]++);
							transBal.setGstin(gstin);
							transBal.setItcTransDate(obj.getTransDate() != null
									? obj.getTransDate()
									: null);
							transBal.setDesc(obj.getDescription() != null
									? obj.getDescription()
									: "Closing Balence");
							transBal.setRefNo(obj.getReferenceNo() != null
									? obj.getReferenceNo()
									: null);
							transBal.setTaxPeriod(
									obj.getRtnprd() != null ? obj.getRtnprd()
											: null);
							LedgerItcReclaimBalanceAmts itc4a5 = obj
									.getItc4a5();
							// Retrieve values from itc4a5 and set in transBal
							if (itc4a5 != null) {
								transBal.setTable4a5Igst(GenUtil
										.formatCurrency(itc4a5.getIgst() != null
												? itc4a5.getIgst()
												: "0"));
								transBal.setTable4a5Cgst(GenUtil
										.formatCurrency(itc4a5.getCgst() != null
												? itc4a5.getCgst()
												: "0"));
								transBal.setTable4a5Sgst(GenUtil
										.formatCurrency(itc4a5.getSgst() != null
												? itc4a5.getSgst()
												: "0"));
								transBal.setTable4a5Cess(GenUtil
										.formatCurrency(itc4a5.getCess() != null
												? itc4a5.getCess()
												: "0"));
							}

							// Retrieve values from itc4b2 and set in transBal
							LedgerItcReclaimBalanceAmts itc4b2 = obj
									.getItc4b2();
							if (itc4b2 != null) {
								transBal.setTable4b2Igst(GenUtil
										.formatCurrency(itc4b2.getIgst() != null
												? itc4b2.getIgst()
												: "0"));
								transBal.setTable4b2Cgst(GenUtil
										.formatCurrency(itc4b2.getCgst() != null
												? itc4b2.getCgst()
												: "0"));
								transBal.setTable4b2Sgst(GenUtil
										.formatCurrency(itc4b2.getSgst() != null
												? itc4b2.getSgst()
												: "0"));
								transBal.setTable4b2Cess(GenUtil
										.formatCurrency(itc4b2.getCess() != null
												? itc4b2.getCess()
												: "0"));
							}

							// Retrieve values from itc4d1 and set in transBal
							LedgerItcReclaimBalanceAmts itc4d1 = obj
									.getItc4d1();
							if (itc4d1 != null) {
								transBal.setTable4d1Igst(GenUtil
										.formatCurrency(itc4d1.getIgst() != null
												? itc4d1.getIgst()
												: "0"));
								transBal.setTable4d1Cgst(GenUtil
										.formatCurrency(itc4d1.getCgst() != null
												? itc4d1.getCgst()
												: "0"));
								transBal.setTable4d1Sgst(GenUtil
										.formatCurrency(itc4d1.getSgst() != null
												? itc4d1.getSgst()
												: "0"));
								transBal.setTable4d1Cess(GenUtil
										.formatCurrency(itc4d1.getCess() != null
												? itc4d1.getCess()
												: "0"));
							}

							// Retrieve values from clsbal and set in transBal
							LedgerItcReclaimBalanceAmts clsbal = obj
									.getClsbal();
							if (clsbal != null) {
								transBal.setClsBalIgst(GenUtil
										.formatCurrency(clsbal.getIgst() != null
												? clsbal.getIgst()
												: "0"));
								transBal.setClsBalCgst(GenUtil
										.formatCurrency(clsbal.getCgst() != null
												? clsbal.getCgst()
												: "0"));
								transBal.setClsBalSgst(GenUtil
										.formatCurrency(clsbal.getSgst() != null
												? clsbal.getSgst()
												: "0"));
								transBal.setClsBalCess(GenUtil
										.formatCurrency(clsbal.getCess() != null
												? clsbal.getCess()
												: "0"));
							}

							allValDetailsDto.add(transBal);
						});
					}

					if (closeBal != null) {
						ItcCrReversalAndReclaimDto respCloseBal = new ItcCrReversalAndReclaimDto();
						respCloseBal.setSrNo(srNoCounter[0]++);
						respCloseBal
								.setItcTransDate(closeBal.getTransDate() != null
										? closeBal.getTransDate()
										: null);
						respCloseBal.setDesc(closeBal.getDescription() != null
								? closeBal.getDescription()
								: "Closing Balance");
						respCloseBal.setRefNo(closeBal.getReferenceNo() != null
								? closeBal.getReferenceNo()
								: null);
						respCloseBal.setTaxPeriod(closeBal.getRtnprd() != null
								? closeBal.getRtnprd()
								: null);
						respCloseBal.setClsBalIgst(GenUtil.formatCurrency(
								closeBal.getIgst() != null ? closeBal.getIgst()
										: "0"));
						respCloseBal.setClsBalCgst(GenUtil.formatCurrency(
								closeBal.getCgst() != null ? closeBal.getCgst()
										: "0"));
						respCloseBal.setClsBalSgst(GenUtil.formatCurrency(
								closeBal.getSgst() != null ? closeBal.getSgst()
										: "0"));
						respCloseBal.setClsBalCess(GenUtil.formatCurrency(
								closeBal.getCess() != null ? closeBal.getCess()
										: "0"));

						allValDetailsDto.add(respCloseBal);

					}
				}
			}

			if (!allValDetailsDto.isEmpty()) {
				workbook = crReclaimExcel(allValDetailsDto, fromDate1, toDate1);
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

	private Workbook crReclaimExcel(
			List<ItcCrReversalAndReclaimDto> allDetailsDto, String fromdate,
			String toDate) {
		Workbook workbook = null;
		int startRow = 7;
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
						"ReportTemplates", "ReversalAndReclaimLedger.xlsx");

				if (LOGGER.isDebugEnabled()) {
					String msg = "CreditLedgerReportServiceImpl class crReclaimExcel method "
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
					Cell cell2 = sheet.getCells().get("B3");
					Cell cell3 = sheet.getCells().get("B4");
					cell2.setValue(fromdate);
					cell3.setValue(toDate);
					String[] invoiceHeaders = null;

					if (!allDetailsDto.isEmpty()) {
						invoiceHeaders = commonUtility
								.getProp("ledger.creditreclaim.report.header")
								.split(",");
						reportCells.importCustomObjects(allDetailsDto,
								invoiceHeaders, isHeaderRequired, startRow,
								startcolumn, allDetailsDto.size(), true,
								"yyyy-mm-dd", false);

					}

					if (LOGGER.isDebugEnabled()) {
						String msg = "CreditLedgerReportServiceImpl.writeToExcel "
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

	// RCM ledger
	public Workbook getRcmLedgerDetails(List<CreditLedgerBulkDetailsDto> reqDto,
			Long id, String fromDate1, String toDate1) {

		Workbook workbook = null;
		Gson gson = new Gson();
		final int[] srNoCounter = { 1 };
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Inside CreditLedgerReportServiceImpl class and inside getRcmLedgerDetails method ",
						reqDto.toString());
			}
			List<RcmDetailsRespDto> allDetailsDto = new ArrayList<>();

			for (CreditLedgerBulkDetailsDto req : reqDto) {
				String gstin = req.getGstin();
				String fromDate = fromDate1;
				String toDate = toDate1;

				/*
				 * if (LOGGER.isDebugEnabled()) {
				 * LOGGER.debug("gstn and fromdate and todate is ", gstin,
				 * fromDate, toDate); }
				 */

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("gstn: {}, fromDate: {}, toDate: {}", gstin,
							fromDate, toDate);
				}

				GetCashLedgerDetailsReqDto gGetCashLedgerDetailsDto = new GetCashLedgerDetailsReqDto();
				gGetCashLedgerDetailsDto.setGstin(gstin);
				gGetCashLedgerDetailsDto.setFromDate(fromDate);
				gGetCashLedgerDetailsDto.setToDate(toDate);
				// String apiResp=null;
				String apiResp = cashDataAtGstn.fromGstnDetailedRcmDetailsTest(
						gGetCashLedgerDetailsDto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"AsPI response from GSTIN for RCM Ledger is {}",
							apiResp);
				}

				if (apiResp != null) {
					JsonObject reqObj = JsonParser.parseString(apiResp)
							.getAsJsonObject();
					RcmDetailedLedgerDetailsDto respDto = gson.fromJson(reqObj,
							RcmDetailedLedgerDetailsDto.class);
					CrReversalLedgerOpenCloseBalDto closeBal = respDto
							.getClosenBal();

					List<RcmDetailedLedgerTransactionDto> respTransDtos = respDto
							.getTransTypeBalDto();

					if (respTransDtos != null) {
						// AtomicInteger serNoCounter = new AtomicInteger(1);
						respTransDtos.forEach(obj -> {
							RcmDetailsRespDto transBal = new RcmDetailsRespDto();
							transBal.setSrNo(srNoCounter[0]++);
							transBal.setGstin(gstin);
							transBal.setDate(obj.getTransDate() != null
									? obj.getTransDate()
									: null);
							transBal.setDesc(obj.getDescription() != null
									? obj.getDescription()
									: null);
							transBal.setRefNo(obj.getReferenceNo() != null
									? obj.getReferenceNo()
									: null);
							transBal.setTaxPeriod(
									obj.getRtnprd() != null ? obj.getRtnprd()
											: null);
							LedgerItcReclaimBalanceAmts table4a2 = obj
									.getItc4a2();
							if (table4a2 != null) {
								transBal.setTable4a2Igst(GenUtil.formatCurrency(
										table4a2.getIgst() != null
												? table4a2.getIgst()
												: "0"));
								transBal.setTable4a2Cgst(GenUtil.formatCurrency(
										table4a2.getCgst() != null
												? table4a2.getCgst()
												: "0"));
								transBal.setTable4a2Sgst(GenUtil.formatCurrency(
										table4a2.getSgst() != null
												? table4a2.getSgst()
												: "0"));
								transBal.setTable4a2Cess(GenUtil.formatCurrency(
										table4a2.getCess() != null
												? table4a2.getCess()
												: "0"));
							}

							LedgerItcReclaimBalanceAmts table4a3 = obj
									.getItc4a3();
							if (table4a3 != null) {
								transBal.setTable4a3Igst(GenUtil.formatCurrency(
										table4a3.getIgst() != null
												? table4a3.getIgst()
												: "0"));
								transBal.setTable4a3Cgst(GenUtil.formatCurrency(
										table4a3.getCgst() != null
												? table4a3.getCgst()
												: "0"));
								transBal.setTable4a3Sgst(GenUtil.formatCurrency(
										table4a3.getSgst() != null
												? table4a3.getSgst()
												: "0"));
								transBal.setTable4a3Cess(GenUtil.formatCurrency(
										table4a3.getCess() != null
												? table4a3.getCess()
												: "0"));
							}

							LedgerItcReclaimBalanceAmts table31d = obj
									.getItc31d();
							if (table31d != null) {
								transBal.setTable31dIgst(GenUtil.formatCurrency(
										table31d.getIgst() != null
												? table31d.getIgst()
												: "0"));
								transBal.setTable31dCgst(GenUtil.formatCurrency(
										table31d.getCgst() != null
												? table31d.getCgst()
												: "0"));
								transBal.setTable31dSgst(GenUtil.formatCurrency(
										table31d.getSgst() != null
												? table31d.getSgst()
												: "0"));
								transBal.setTable31dCess(GenUtil.formatCurrency(
										table31d.getCess() != null
												? table31d.getCess()
												: "0"));
							}

							LedgerItcReclaimBalanceAmts clsbal = obj
									.getClsbal();
							if (clsbal != null) {
								transBal.setClsBalIgst(GenUtil
										.formatCurrency(clsbal.getIgst() != null
												? clsbal.getIgst()
												: "0"));
								transBal.setClsBalCgst(GenUtil
										.formatCurrency(clsbal.getCgst() != null
												? clsbal.getCgst()
												: "0"));
								transBal.setClsBalSgst(GenUtil
										.formatCurrency(clsbal.getSgst() != null
												? clsbal.getSgst()
												: "0"));
								transBal.setClsBalCess(GenUtil
										.formatCurrency(clsbal.getCess() != null
												? clsbal.getCess()
												: "0"));
							}

							allDetailsDto.add(transBal);
						});
					}

					if (closeBal != null) {
						RcmDetailsRespDto respCloseBal = new RcmDetailsRespDto();
						respCloseBal.setSrNo(srNoCounter[0]++);
						respCloseBal.setGstin(gstin);
						respCloseBal.setDesc("Closing Balance");
						respCloseBal.setClsBalIgst(
							    GenUtil.formatCurrency(closeBal.getIgst() != null ? closeBal.getIgst() : "0"));
							respCloseBal.setClsBalCgst(
							    GenUtil.formatCurrency(closeBal.getCgst() != null ? closeBal.getCgst() : "0"));
							respCloseBal.setClsBalSgst(
							    GenUtil.formatCurrency(closeBal.getSgst() != null ? closeBal.getSgst() : "0"));
							respCloseBal.setClsBalCess(
							    GenUtil.formatCurrency(closeBal.getCess() != null ? closeBal.getCess() : "0"));


						allDetailsDto.add(respCloseBal);

					}

				}
			}

			if (!allDetailsDto.isEmpty()) {
				workbook = rcmLedgerExcelDwnld(allDetailsDto, fromDate1,
						toDate1);
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

	// negative ledger
	public Workbook getNegativeLedgerDetails(
			List<CreditLedgerBulkDetailsDto> reqDto, Long id, String fromDate1,
			String toDate1) {

		Workbook workbook = null;
		Gson gson = new Gson();
		final int[] srNoCounter = { 1 };
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Inside CreditLedgerReportServiceImpl class and inside getNegativeLedgerDetails method ",
						reqDto.toString());
			}
			List<NegativeDetailsRespDto> allDetailsDto = new ArrayList<>();

			for (CreditLedgerBulkDetailsDto req : reqDto) {
				String gstin = req.getGstin();
				String fromDate = fromDate1;
				String toDate = toDate1;

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("gstn and fromdate and todate is ", gstin,
							fromDate, toDate);
				}

				GetCashLedgerDetailsReqDto gGetCashLedgerDetailsDto = new GetCashLedgerDetailsReqDto();
				gGetCashLedgerDetailsDto.setGstin(gstin);
				gGetCashLedgerDetailsDto.setFromDate(fromDate);
				gGetCashLedgerDetailsDto.setToDate(toDate);

				String apiResp = cashDataAtGstn
						.fromGstnDetailedNegativeDetailsTest(
								gGetCashLedgerDetailsDto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"AsPI response from GSTIN for RCM Ledger is {}",
							apiResp);
				}

				if (apiResp != null) {
					JsonObject reqObj = JsonParser.parseString(apiResp)
							.getAsJsonObject();
					NegativeDetailedLedgerDetailsDto respDto = gson.fromJson(
							reqObj, NegativeDetailedLedgerDetailsDto.class);
					List<CrReversalLedgerOpenCloseBalDto> closeBalList = respDto
							.getClosenBal();
					List<NegativeDetailedLedgerTransactionDto> respTransDtos = respDto
							.getTransTypeBalDto();

					if (respTransDtos != null) {
						AtomicInteger srNumCounter = new AtomicInteger(1);
						respTransDtos.forEach(obj -> {
							NegativeDetailsRespDto transBal = new NegativeDetailsRespDto();
							transBal.setGstin(gstin);
							transBal.setSrNo(srNoCounter[0]++);

							transBal.setDate(obj.getTransDate() != null
									? obj.getTransDate()
									: null);
							transBal.setDesc(obj.getDescription() != null
									? obj.getDescription()
									: null);
							transBal.setRefNo(obj.getReferenceNo() != null
									? obj.getReferenceNo()
									: null);
							transBal.setTaxPeriod(
									obj.getRtnprd() != null ? obj.getRtnprd()
											: null);
							transBal.setTranTyp(
									obj.getTrantyp() != null ? obj.getTrantyp()
											: null);

							List<LedgerItcReclaimBalanceAmts> negliabList = obj
									.getNegliab();
							if (negliabList != null && !negliabList.isEmpty()) {
								LedgerItcReclaimBalanceAmts amtCrDrOtrThnRevChg = negliabList
										.get(0);
								LedgerItcReclaimBalanceAmts amtCrDrRevChg = (negliabList
										.size() > 1) ? negliabList.get(1)
												: null;

								// For first object
										if (amtCrDrOtrThnRevChg != null) {
										    transBal.setAmtCrDrOtherIgst(
										            GenUtil.formatCurrency(amtCrDrOtrThnRevChg.getIgst() != null
										                    ? amtCrDrOtrThnRevChg.getIgst()
										                    : "0"));
										    transBal.setAmtCrDrOtherCgst(
										            GenUtil.formatCurrency(amtCrDrOtrThnRevChg.getCgst() != null
										                    ? amtCrDrOtrThnRevChg.getCgst()
										                    : "0"));
										    transBal.setAmtCrDrOtherSgst(
										            GenUtil.formatCurrency(amtCrDrOtrThnRevChg.getSgst() != null
										                    ? amtCrDrOtrThnRevChg.getSgst()
										                    : "0"));
										    transBal.setAmtCrDrOtherCess(
										            GenUtil.formatCurrency(amtCrDrOtrThnRevChg.getCess() != null
										                    ? amtCrDrOtrThnRevChg.getCess()
										                    : "0"));
										    transBal.setTrancd(amtCrDrOtrThnRevChg.getTrancd());
										    transBal.setDesc(amtCrDrOtrThnRevChg.getDesc());
										}

										// For second object (if present)
										if (amtCrDrRevChg != null) {
										    transBal.setAmtCrDrRevChargeIgst(
										            GenUtil.formatCurrency(amtCrDrRevChg.getIgst() != null
										                    ? amtCrDrRevChg.getIgst()
										                    : "0"));
										    transBal.setAmtCrDrRevChargeCgst(
										            GenUtil.formatCurrency(amtCrDrRevChg.getCgst() != null
										                    ? amtCrDrRevChg.getCgst()
										                    : "0"));
										    transBal.setAmtCrDrRevChargeSgst(
										            GenUtil.formatCurrency(amtCrDrRevChg.getSgst() != null
										                    ? amtCrDrRevChg.getSgst()
										                    : "0"));
										    transBal.setAmtCrDrRevChargeCess(
										            GenUtil.formatCurrency(amtCrDrRevChg.getCess() != null
										                    ? amtCrDrRevChg.getCess()
										                    : "0"));
										    transBal.setTrancd(amtCrDrRevChg.getTrancd());
										    transBal.setDesc(amtCrDrRevChg.getDesc());
										}

										List<LedgerItcReclaimBalanceAmts> negliaBalenceList = obj.getNegliabal();
										LedgerItcReclaimBalanceAmts clsBalOtrThnRevChg = negliaBalenceList.get(0);
										LedgerItcReclaimBalanceAmts clsBalRevChg = negliaBalenceList.get(1);

										if (clsBalOtrThnRevChg != null) {
										    transBal.setClsBalOtherIgst(
										            GenUtil.formatCurrency(clsBalOtrThnRevChg.getIgst() != null
										                    ? clsBalOtrThnRevChg.getIgst()
										                    : "0"));
										    transBal.setClsBalOtherCgst(
										            GenUtil.formatCurrency(clsBalOtrThnRevChg.getCgst() != null
										                    ? clsBalOtrThnRevChg.getCgst()
										                    : "0"));
										    transBal.setClsBalOtherSgst(
										            GenUtil.formatCurrency(clsBalOtrThnRevChg.getSgst() != null
										                    ? clsBalOtrThnRevChg.getSgst()
										                    : "0"));
										    transBal.setClsBalOtherCess(
										            GenUtil.formatCurrency(clsBalOtrThnRevChg.getCess() != null
										                    ? clsBalOtrThnRevChg.getCess()
										                    : "0"));
										}

										// For second object (if present)
										if (clsBalRevChg != null) {
										    transBal.setClsBalRevChargeIgst(
										            GenUtil.formatCurrency(clsBalRevChg.getIgst() != null
										                    ? clsBalRevChg.getIgst()
										                    : "0"));
										    transBal.setClsBalRevChargeCgst(
										            GenUtil.formatCurrency(clsBalRevChg.getCgst() != null
										                    ? clsBalRevChg.getCgst()
										                    : "0"));
										    transBal.setClsBalRevChargeSgst(
										            GenUtil.formatCurrency(clsBalRevChg.getSgst() != null
										                    ? clsBalRevChg.getSgst()
										                    : "0"));
										    transBal.setClsBalRevChargeCess(
										            GenUtil.formatCurrency(clsBalRevChg.getCess() != null
										                    ? clsBalRevChg.getCess()
										                    : "0"));
										}
							}

							allDetailsDto.add(transBal);
						});

					}

					if (closeBalList != null && !closeBalList.isEmpty()) {
						CrReversalLedgerOpenCloseBalDto clsBalOther = closeBalList
								.get(0);
						CrReversalLedgerOpenCloseBalDto clsBalRevChg = closeBalList
								.get(1);

						NegativeDetailsRespDto respCloseBal = new NegativeDetailsRespDto();
						respCloseBal.setSrNo(srNoCounter[0]++);
						respCloseBal.setGstin(gstin);
						respCloseBal.setDesc("Closing Balance");
						respCloseBal.setClsBalOtherIgst(
						        GenUtil.formatCurrency(clsBalOther.getIgst() != null
						                ? clsBalOther.getIgst()
						                : "0"));
						respCloseBal.setClsBalOtherCgst(
						        GenUtil.formatCurrency(clsBalOther.getCgst() != null
						                ? clsBalOther.getCgst()
						                : "0"));
						respCloseBal.setClsBalOtherSgst(
						        GenUtil.formatCurrency(clsBalOther.getSgst() != null
						                ? clsBalOther.getSgst()
						                : "0"));
						respCloseBal.setClsBalOtherCess(
						        GenUtil.formatCurrency(clsBalOther.getCess() != null
						                ? clsBalOther.getCess()
						                : "0"));

						respCloseBal.setClsBalRevChargeIgst(
						        GenUtil.formatCurrency(clsBalRevChg.getIgst() != null
						                ? clsBalRevChg.getIgst()
						                : "0"));
						respCloseBal.setClsBalRevChargeCgst(
						        GenUtil.formatCurrency(clsBalRevChg.getCgst() != null
						                ? clsBalRevChg.getCgst()
						                : "0"));
						respCloseBal.setClsBalRevChargeSgst(
						        GenUtil.formatCurrency(clsBalRevChg.getSgst() != null
						                ? clsBalRevChg.getSgst()
						                : "0"));
						respCloseBal.setClsBalRevChargeCess(
						        GenUtil.formatCurrency(clsBalRevChg.getCess() != null
						                ? clsBalRevChg.getCess()
						                : "0"));


						allDetailsDto.add(respCloseBal);

					}

				}
			}

			if (!allDetailsDto.isEmpty()) {
				workbook = negativeLedgerExcelDwnld(allDetailsDto, fromDate1,
						toDate1);
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

	private Workbook rcmLedgerExcelDwnld(List<RcmDetailsRespDto> allDetailsDto,
			String fromdate, String toDate) {
		Workbook workbook = null;
		int startRow = 7;
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
						"ReportTemplates", "ITC RCM Ledger.xlsx");

				if (LOGGER.isDebugEnabled()) {
					String msg = "CreditLedgerReportServiceImpl class crReclaimExcel method "
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
					Cell cell2 = sheet.getCells().get("B3");
					Cell cell3 = sheet.getCells().get("B4");
					cell2.setValue(fromdate);
					cell3.setValue(toDate);
					String[] invoiceHeaders = null;

					if (!allDetailsDto.isEmpty()) {
						invoiceHeaders = commonUtility
								.getProp("ledger.rcm.bulk.report.header")
								.split(",");
						reportCells.importCustomObjects(allDetailsDto,
								invoiceHeaders, isHeaderRequired, startRow,
								startcolumn, allDetailsDto.size(), true,
								"yyyy-mm-dd", false);

					}

					if (LOGGER.isDebugEnabled()) {
						String msg = "RcmServiceImpl.writeToExcel "
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

	private Workbook negativeLedgerExcelDwnld(
			List<NegativeDetailsRespDto> allDetailsDto, String fromdate,
			String toDate) {
		Workbook workbook = null;
		int startRow = 8;
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
						"ReportTemplates", "Negative Liability Ledger.xlsx");

				if (LOGGER.isDebugEnabled()) {
					String msg = "CreditLedgerReportServiceImpl class crReclaimExcel method "
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
					Cell cell2 = sheet.getCells().get("B3");
					Cell cell3 = sheet.getCells().get("B4");
					cell2.setValue(fromdate);
					cell3.setValue(toDate);
					String[] invoiceHeaders = null;

					if (!allDetailsDto.isEmpty()) {
						invoiceHeaders = commonUtility.getProp(
								"ledger.negetiveLiab.bulk.report.header")
								.split(",");
						reportCells.importCustomObjects(allDetailsDto,
								invoiceHeaders, isHeaderRequired, startRow,
								startcolumn, allDetailsDto.size(), true,
								"yyyy-mm-dd", false);

					}

					if (LOGGER.isDebugEnabled()) {
						String msg = "CreditLedgerReportServiceImpl.writeToExcel "
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
