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
import java.util.Optional;
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
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.docs.dto.ledger.CashLedgerOpenCloseBalDto;
import com.ey.advisory.app.docs.dto.ledger.GetCashITCBalanceReqDto;
import com.ey.advisory.app.docs.dto.ledger.LiabDetailsReportRespDto;
import com.ey.advisory.app.docs.dto.ledger.LiabilityLedgersDetailsDto;
import com.ey.advisory.app.docs.dto.ledger.TransactionTypeLibBalDto;
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
@Service("LiabilityLedgerReportServiceImpl")
public class LiabilityLedgerReportServiceImpl
		implements LiabilityLedgerReportService {

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

	@Autowired
	@Qualifier("liabilityLedgerDetailsForReturnLiabilityDataAtGstnImpl")
	private LiabilityLedgerDetailsForReturnLiabilityDataAtGstn taxDataAtGstn;

	private static final String UPLOADED_FILENAME_MSG = "Uploaded file Name :'%s'";

	public void getLiabilityLedgerReport(Long id, String fromReturnPeriod,
			String toReturnPeriod, List<String> activeGstnList)
			throws IOException

	{
		File tempDir = null;
		Workbook workbook = null;
		Optional<FileStatusDownloadReportEntity> optEntity = fileStatusDownloadReportRepo
				.findById(id);
		FileStatusDownloadReportEntity entity = optEntity.get();

		try {
			tempDir = createTempDir();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Inside CreditLedgerReportServiceImpl active gstins for credit ledger report",
						activeGstnList);
			}

			String reportType = entity.getReportType();
			String filename1 = tempDir.getAbsolutePath() + File.separator
					+ reportType + "_" + fromReturnPeriod + "_" + toReturnPeriod
					+ ".xlsx";

			workbook = getLiabilityData(id, fromReturnPeriod, toReturnPeriod,
					activeGstnList);

			if (workbook != null) {
				workbook.save(filename1);

				String zipFileName = zipLiabilityLedgerReport(tempDir,
						reportType, id);
				File zipFile = new File(tempDir, zipFileName);

				/*
				 * String uploadedFileName =
				 * DocumentUtility.uploadZipFile(zipFile,
				 * "LiabilityLedgerReport");
				 */

				Pair<String, String> uploadedFileName = DocumentUtility
						.uploadFile(zipFile, "LiabilityLedgerReport");
				String uploadedDocName = uploadedFileName.getValue0();
				String docId = uploadedFileName.getValue1();

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format(UPLOADED_FILENAME_MSG,
							uploadedDocName));
				}

				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.REPORT_GENERATED, uploadedDocName,
						LocalDateTime.now(), docId);
			}
		} catch (Exception ex) {
			LOGGER.error("Exception occured in LiabilityLedgerReportServiceImpl"
					+ ex);
			throw new AppException(ex);
		} finally {
			anx1CsvDownloadUtil.deleteTemporaryDirectory(tempDir);

		}
	}

	public Workbook getLiabilityData(Long id, String fromReturnPeriod,
			String toReturnPeriod, List<String> activeGstnList) {

		Workbook workbook = null;
		String apiResp = null;
		Gson gson = new Gson();
		try {
			List<LiabDetailsReportRespDto> allDetailsDto = new ArrayList<>();
			List<String> taxPeriods = GenUtil
					.deriveTaxPeriodsGivenFromAndToPeriod(fromReturnPeriod,
							toReturnPeriod);
			for (String gstin : activeGstnList) {
				// String gstin = req.getGstin();
				GetCashITCBalanceReqDto liabilityDetailsDto = new GetCashITCBalanceReqDto();
				for (String retPeriod : taxPeriods) {
					liabilityDetailsDto.setGstin(gstin);
					liabilityDetailsDto.setRetPeriod(retPeriod);

					apiResp = taxDataAtGstn
							.fromGstnTestLiab(liabilityDetailsDto);

					if (apiResp != null) {
						JsonObject requestObject = JsonParser
								.parseString(apiResp).getAsJsonObject();
						LiabilityLedgersDetailsDto liabLedgerDetDto = gson
								.fromJson(requestObject,
										LiabilityLedgersDetailsDto.class);

						List<TransactionTypeLibBalDto> transTypeLibBalDto = liabLedgerDetDto
								.getTransTypeBalDto();
						CashLedgerOpenCloseBalDto closeBal = liabLedgerDetDto
								.getCloseBal();
						if (transTypeLibBalDto != null) {
							transTypeLibBalDto.forEach(obj -> {
								LiabDetailsReportRespDto transBal = new LiabDetailsReportRespDto();
								transBal.setGstin(gstin);
								transBal.setDptDate(obj.getDptDate());
								transBal.setReferenceNo(obj.getReferenceNo());
								transBal.setRetPeriod(
										liabLedgerDetDto.getRetPeriod());
								// prod changes
								transBal.setDschrgType(
										obj.getDischargingType());
								//
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
								// igst

								// IGST Bal
								transBal.setIgstBalTaxValue(
										GenUtil.formatCurrency(obj.getIgstBal()
												.getTaxValue() != null
														? obj.getIgstBal()
																.getTaxValue()
														: "0"));
								transBal.setIgstBalInterestValue(
										GenUtil.formatCurrency(obj.getIgstBal()
												.getInterestValue() != null
														? obj.getIgstBal()
																.getInterestValue()
														: "0"));
								transBal.setIgstBalPenalty(
										GenUtil.formatCurrency(obj.getIgstBal()
												.getPenalty() != null
														? obj.getIgstBal()
																.getPenalty()
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
								// igstBal

								// cgst
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
								// cgst

								// cgst bal
								transBal.setCgstBalTaxValue(
										GenUtil.formatCurrency(obj.getCgstBal()
												.getTaxValue() != null
														? obj.getCgstBal()
																.getTaxValue()
														: "0"));
								transBal.setCgstBalInterestValue(
										GenUtil.formatCurrency(obj.getCgstBal()
												.getInterestValue() != null
														? obj.getCgstBal()
																.getInterestValue()
														: "0"));
								transBal.setCgstBalPenalty(
										GenUtil.formatCurrency(obj.getCgstBal()
												.getPenalty() != null
														? obj.getCgstBal()
																.getPenalty()
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
								// cgst bal

								// sgst
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
								transBal.setSgstBalTaxValue(
										GenUtil.formatCurrency(obj.getSgstBal()
												.getTaxValue() != null
														? obj.getSgstBal()
																.getTaxValue()
														: "0"));
								transBal.setSgstBalInterestValue(
										GenUtil.formatCurrency(obj.getSgstBal()
												.getInterestValue() != null
														? obj.getSgstBal()
																.getInterestValue()
														: "0"));
								transBal.setSgstBalPenalty(
										GenUtil.formatCurrency(obj.getSgstBal()
												.getPenalty() != null
														? obj.getSgstBal()
																.getPenalty()
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
								// sgst bal

								// cess
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
								// cess

								// cess bal
								transBal.setCessBalTaxValue(
										GenUtil.formatCurrency(obj.getCessBal()
												.getTaxValue() != null
														? obj.getCessBal()
																.getTaxValue()
														: "0"));
								transBal.setCessBalInterestValue(
										GenUtil.formatCurrency(obj.getCessBal()
												.getInterestValue() != null
														? obj.getCessBal()
																.getInterestValue()
														: "0"));
								transBal.setCessBalPenalty(
										GenUtil.formatCurrency(obj.getCessBal()
												.getPenalty() != null
														? obj.getCessBal()
																.getPenalty()
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
								// cess bal

								allDetailsDto.add(transBal);
							});
						}
						if (closeBal != null) {
							LiabDetailsReportRespDto respCloseBal = new LiabDetailsReportRespDto();
							respCloseBal.setGstin(gstin);
							respCloseBal
									.setDescription(closeBal.getDescription());

							// igst bal
							respCloseBal.setIgstBalTaxValue(
									GenUtil.formatCurrency(closeBal.getIgstbal()
											.getTaxValue() != null
													? closeBal.getIgstbal()
															.getTaxValue()
													: "0"));
							respCloseBal.setIgstBalInterestValue(
									GenUtil.formatCurrency(closeBal.getIgstbal()
											.getInterestValue() != null
													? closeBal.getIgstbal()
															.getInterestValue()
													: "0"));
							respCloseBal.setIgstBalPenalty(
									GenUtil.formatCurrency(closeBal.getIgstbal()
											.getPenalty() != null
													? closeBal.getIgstbal()
															.getPenalty()
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
							// cgst bal
							respCloseBal.setCgstBalTaxValue(
									GenUtil.formatCurrency(closeBal.getCgstbal()
											.getTaxValue() != null
													? closeBal.getCgstbal()
															.getTaxValue()
													: "0"));
							respCloseBal.setCgstBalInterestValue(
									GenUtil.formatCurrency(closeBal.getCgstbal()
											.getInterestValue() != null
													? closeBal.getCgstbal()
															.getInterestValue()
													: "0"));
							respCloseBal.setCgstBalPenalty(
									GenUtil.formatCurrency(closeBal.getCgstbal()
											.getPenalty() != null
													? closeBal.getCgstbal()
															.getPenalty()
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
							// sgst bal
							respCloseBal.setSgstBalTaxValue(
									GenUtil.formatCurrency(closeBal.getSgstbal()
											.getTaxValue() != null
													? closeBal.getSgstbal()
															.getTaxValue()
													: "0"));
							respCloseBal.setSgstBalInterestValue(
									GenUtil.formatCurrency(closeBal.getSgstbal()
											.getInterestValue() != null
													? closeBal.getSgstbal()
															.getInterestValue()
													: "0"));
							respCloseBal.setSgstBalPenalty(
									GenUtil.formatCurrency(closeBal.getSgstbal()
											.getPenalty() != null
													? closeBal.getSgstbal()
															.getPenalty()
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

							// cess bal
							respCloseBal.setGstin(gstin);
							respCloseBal.setCessBalTaxValue(
									GenUtil.formatCurrency(closeBal.getCessbal()
											.getTaxValue() != null
													? closeBal.getCessbal()
															.getTaxValue()
													: "0"));
							respCloseBal.setCessBalInterestValue(
									GenUtil.formatCurrency(closeBal.getCessbal()
											.getInterestValue() != null
													? closeBal.getCessbal()
															.getInterestValue()
													: "0"));
							respCloseBal.setCessBalPenalty(
									GenUtil.formatCurrency(closeBal.getCessbal()
											.getPenalty() != null
													? closeBal.getCessbal()
															.getPenalty()
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
							// cess bal

							allDetailsDto.add(respCloseBal);
						}
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

	private Workbook writeToExcel(
			List<LiabDetailsReportRespDto> allDetailsDto) {
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
						"ReportTemplates", "LiabilityLedgerReport.xlsx");

				if (LOGGER.isDebugEnabled()) {
					String msg = "LiabilityLedgerReportServiceImpl writeToExcel1 "
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
								.getProp("ledger.Liability.report.header")
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
					workbook.save(
							ConfigConstants.LIABILITY_lEDGER_REPORT_DOWNLOAD,
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

	private String zipLiabilityLedgerReport(File tempDir, String reportType,
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

}
