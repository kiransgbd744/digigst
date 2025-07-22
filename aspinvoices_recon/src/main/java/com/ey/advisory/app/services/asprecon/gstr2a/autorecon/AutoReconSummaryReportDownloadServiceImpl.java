package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("AutoReconSummaryReportDownloadServiceImpl")
public class AutoReconSummaryReportDownloadServiceImpl
		implements AutoReconSummaryReportDownloadService {

	@Autowired
	@Qualifier("AutoReconSummaryDaoImpl")
	private AutoReconSummaryDao autoReconSummaryDao;

	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository entityInfoRepo;

	@Autowired
	CommonUtility commonUtility;

	@Override
	public Workbook getReconSummaryReport(ArrayList<String> recipientGstins,
			String fromTaxPeriodPR, String toTaxPeriodPR,
			String fromTaxPeriod2A, String toTaxPeriod2A, String fromReconDate,
			String toReconDate, Long entityId, String criteria) {

		Workbook workbook = null;
		try {
			LocalDate reconFromDate = LocalDate.now();
			LocalDate reconToDate = LocalDate.now();
			Integer derivedFromTaxPeriodPR = 0;
			Integer derivedToTaxPeriodPR = 0;
			Integer derivedFromTaxPeriod2A = 0;
			Integer derivedToTaxPeriod2A = 0;
			boolean isDateRange = false;

			if (fromReconDate != null && fromReconDate != ""
					&& !fromReconDate.isEmpty() && toReconDate != null
					&& toReconDate != "" && !toReconDate.isEmpty()) {
				reconFromDate = LocalDate.parse(fromReconDate);
				reconToDate = LocalDate.parse(toReconDate);
				isDateRange = true;
			}
			if ((fromTaxPeriodPR != null && fromTaxPeriodPR != ""
					&& !fromTaxPeriodPR.isEmpty() && fromTaxPeriod2A != null
					&& fromTaxPeriod2A != "" && !fromTaxPeriod2A.isEmpty())
					&& (toTaxPeriodPR != null && toTaxPeriodPR != ""
							&& !toTaxPeriodPR.isEmpty() && toTaxPeriod2A != null
							&& toTaxPeriod2A != ""
							&& !toTaxPeriod2A.isEmpty())) {
				derivedFromTaxPeriodPR = Integer.valueOf(fromTaxPeriodPR
						.substring(2).concat(fromTaxPeriodPR.substring(0, 2)));
				derivedToTaxPeriodPR = Integer.valueOf(toTaxPeriodPR
						.substring(2).concat(toTaxPeriodPR.substring(0, 2)));

				derivedFromTaxPeriod2A = Integer.valueOf(fromTaxPeriod2A
						.substring(2).concat(fromTaxPeriod2A.substring(0, 2)));
				derivedToTaxPeriod2A = Integer.valueOf(toTaxPeriod2A
						.substring(2).concat(toTaxPeriod2A.substring(0, 2)));

			}
			List<Object[]> objList = autoReconSummaryDao
					.getAutoReconSummaryData(recipientGstins, isDateRange,
							derivedFromTaxPeriodPR, derivedToTaxPeriodPR,
							derivedFromTaxPeriod2A, derivedToTaxPeriod2A,
							reconFromDate, reconToDate, criteria);

			Object obj = autoReconSummaryDao.getAutoReconUpdatedOn(entityId);
			Timestamp updatedOn = obj != null ? (Timestamp) obj : null;
			String updatedOnItc = "";
			if (updatedOn != null) {
				updatedOnItc = EYDateUtil.toISTDateTimeFromUTC(updatedOn)
						.toString();
				updatedOnItc = updatedOnItc.substring(0, 19);
				updatedOnItc = updatedOnItc.replace("T", " ");
			}
			LOGGER.debug("Converting of list<obj[]> to List<dto> BEGIN");

			List<AutoReconSummaryDto> retList = objList.stream()
					.map(o -> convertToDto(o))
					.collect(Collectors.toCollection(ArrayList::new));

			Set<String> uniqueParticular = new HashSet<>();

			for (AutoReconSummaryDto particular : retList) {
				uniqueParticular.add(particular.getReportCategory());
			}
			uniqueParticular.remove("TOTAL");
			uniqueParticular.remove("GRAND TOTAL");
			List<String> particulars = Lists.newArrayList(uniqueParticular);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("convertScreenLevelData() "
						+ "Parameter particulars = %s ", particulars);
				LOGGER.debug(msg);
			}
			// Auto Lock List
			List<AutoReconSummaryDto> respListLock = convertScreenLevelData(
					particulars, retList, "Auto Lock");

			// UnLock List
			List<AutoReconSummaryDto> respListUnlock = convertScreenLevelData(
					particulars, retList, "Unlock");

			LOGGER.debug("Converting Query And converting to List END");

			Collections.sort(respListLock, (a, b) -> a.getOrderPosition()
					.compareToIgnoreCase(b.getOrderPosition()));

			respListLock.remove(0);
			respListLock.remove(3);
			respListLock.remove(15);

			respListLock.forEach(e -> {
				if (e.getReportCategory() == null) {
					if (e.getReportType().equalsIgnoreCase("Exact Match")
							|| e.getReportType()
									.equalsIgnoreCase("Match With Tolerance") 
							|| e.getReportType()
									.equalsIgnoreCase("Match (Imports)")) {
						e.setReportCategory("Match");
					} else if (e.getReportType()
							.equalsIgnoreCase("Value Mismatch")
							|| e.getReportType()
									.equalsIgnoreCase("POS Mismatch")
							|| e.getReportType()
									.equalsIgnoreCase("Doc Date Mismatch")
							|| e.getReportType()
									.equalsIgnoreCase("Doc Type Mismatch")
							|| e.getReportType()
									.equalsIgnoreCase("Doc No Mismatch I")
							|| e.getReportType()
									.equalsIgnoreCase("Multi-Mismatch")
							|| e.getReportType()
									.equalsIgnoreCase("Doc No Mismatch II")
							|| e.getReportType()
									.equalsIgnoreCase("Mismatch (Imports)")
							|| e.getReportType()
									.equalsIgnoreCase("Potential-I")
							|| e.getReportType()
									.equalsIgnoreCase("Potential-II")
							|| e.getReportType()
									.equalsIgnoreCase("Logical Match")
									
							|| e.getReportType().equalsIgnoreCase(
									"Doc No & Doc Date Mismatch")) {
						e.setReportCategory("Mismatch");
					} else if (e.getReportType().equalsIgnoreCase("Force Match")
							|| e.getReportType().equalsIgnoreCase(
									"GSTR-3B Locked Records")) {
						e.setReportCategory("Force Match/3B Response");
					}
				}

			});

			List<AutoReconSummaryGrandTotalDto> respListlockUpdated = new ArrayList<AutoReconSummaryGrandTotalDto>();
			respListLock.forEach(e -> {
				AutoReconSummaryGrandTotalDto dto = new AutoReconSummaryGrandTotalDto();
				dto.setReportCategory(e.getReportCategory());
				dto.setReportType(e.getReportType());
				dto.setPrCount(e.getPrCount());
				dto.setPrTotalTax(appendDecimalDigit(e.getPrTotalTax()));
				dto.setPrTotalTaxPer(appendDecimalDigit(e.getPrTotalTaxPer()));
				dto.setPrIgst(appendDecimalDigit(e.getPrIgst()));
				dto.setPrCgst(appendDecimalDigit(e.getPrCgst()));
				dto.setPrSgst(appendDecimalDigit(e.getPrSgst()));
				dto.setPrCess(appendDecimalDigit(e.getPrCess()));
				dto.setA2Count(e.getA2Count());
				dto.setA2TotalTax(appendDecimalDigit(e.getA2TotalTax()));
				dto.setA2TotalTaxPer(appendDecimalDigit(e.getA2TotalTaxPer()));
				dto.setA2Igst(appendDecimalDigit(e.getA2Igst()));
				dto.setA2Cgst(appendDecimalDigit(e.getA2Cgst()));
				dto.setA2Sgst(appendDecimalDigit(e.getA2Sgst()));
				dto.setA2Cess(appendDecimalDigit(e.getA2Cess()));
				dto.setLockStatus(e.getLockStatus());
				respListlockUpdated.add(dto);
			});

			Collections.sort(respListUnlock, (a, b) -> a.getOrderPosition()
					.compareToIgnoreCase(b.getOrderPosition()));

			respListUnlock.remove(0);
			respListUnlock.remove(3);
			respListUnlock.remove(15);

			respListUnlock.forEach(e -> {
				if (e.getReportCategory() == null) {
					if (e.getReportType().equalsIgnoreCase("Exact Match")
							|| e.getReportType()
									.equalsIgnoreCase("Match With Tolerance")
							|| e.getReportType()
									.equalsIgnoreCase("Match (Imports)")) {
						e.setReportCategory("Match");
					} else if (e.getReportType()
							.equalsIgnoreCase("Value Mismatch") 
							|| e.getReportType()
									.equalsIgnoreCase("POS Mismatch")
							|| e.getReportType()
									.equalsIgnoreCase("Doc Date Mismatch")
							|| e.getReportType()
									.equalsIgnoreCase("Doc Type Mismatch")
							|| e.getReportType()
									.equalsIgnoreCase("Doc No Mismatch I")
							|| e.getReportType()
									.equalsIgnoreCase("Multi-Mismatch")
							|| e.getReportType()
									.equalsIgnoreCase("Doc No Mismatch II")
							|| e.getReportType()
									.equalsIgnoreCase("Mismatch (Imports)")
							|| e.getReportType()
									.equalsIgnoreCase("Potential-I")
							|| e.getReportType()
									.equalsIgnoreCase("Potential-II")
							|| e.getReportType()
									.equalsIgnoreCase("Logical Match")
							|| e.getReportType()
									.equalsIgnoreCase("Doc No & Doc Date Mismatch")) {
						e.setReportCategory("Mismatch");
					} else if (e.getReportType()
							.equalsIgnoreCase("Addition in PR")
							|| e.getReportType()
									.equalsIgnoreCase("Addition in 2A")
							|| e.getReportType()
									.equalsIgnoreCase("Addition in PR (Imports)")
							|| e.getReportType().equalsIgnoreCase(
									"Addition in 2A (Imports)")) {
						e.setReportCategory("Additional Entries");
					}
				}

			});

			List<AutoReconSummaryGrandTotalDto> respGrandTotal = new ArrayList<AutoReconSummaryGrandTotalDto>();
			respListUnlock.forEach(e -> {
				if (e.getReportCategory().equalsIgnoreCase("GRAND TOTAL")) {
					AutoReconSummaryGrandTotalDto dto = new AutoReconSummaryGrandTotalDto();
					dto.setReportCategory(e.getReportCategory());
					dto.setReportType(e.getReportType());
					dto.setPrCount(e.getPrCount());
					dto.setPrTotalTax(appendDecimalDigit(e.getPrTotalTax()));
					dto.setPrTotalTaxPer(
							appendDecimalDigit(e.getPrTotalTaxPer()));
					dto.setPrIgst(appendDecimalDigit(e.getPrIgst()));
					dto.setPrCgst(appendDecimalDigit(e.getPrCgst()));
					dto.setPrSgst(appendDecimalDigit(e.getPrSgst()));
					dto.setPrCess(appendDecimalDigit(e.getPrCess()));
					dto.setA2Count(e.getA2Count());
					dto.setA2TotalTax(appendDecimalDigit(e.getA2TotalTax()));
					dto.setA2TotalTaxPer(
							appendDecimalDigit(e.getA2TotalTaxPer()));
					dto.setA2Igst(appendDecimalDigit(e.getA2Igst()));
					dto.setA2Cgst(appendDecimalDigit(e.getA2Cgst()));
					dto.setA2Sgst(appendDecimalDigit(e.getA2Sgst()));
					dto.setA2Cess(appendDecimalDigit(e.getA2Cess()));
					dto.setLockStatus(e.getLockStatus());
					respGrandTotal.add(dto);
				}
			});
			respListUnlock.remove(20);

			List<AutoReconSummaryGrandTotalDto> respListUnlockUpdated = new ArrayList<AutoReconSummaryGrandTotalDto>();
			respListUnlock.forEach(e -> {
				AutoReconSummaryGrandTotalDto dto = new AutoReconSummaryGrandTotalDto();
				dto.setReportCategory(e.getReportCategory());
				dto.setReportType(e.getReportType());
				dto.setPrCount(e.getPrCount());
				dto.setPrTotalTax(appendDecimalDigit(e.getPrTotalTax()));
				dto.setPrTotalTaxPer(appendDecimalDigit(e.getPrTotalTaxPer()));
				dto.setPrIgst(appendDecimalDigit(e.getPrIgst()));
				dto.setPrCgst(appendDecimalDigit(e.getPrCgst()));
				dto.setPrSgst(appendDecimalDigit(e.getPrSgst()));
				dto.setPrCess(appendDecimalDigit(e.getPrCess()));
				dto.setA2Count(e.getA2Count());
				dto.setA2TotalTax(appendDecimalDigit(e.getA2TotalTax()));
				dto.setA2TotalTaxPer(appendDecimalDigit(e.getA2TotalTaxPer()));
				dto.setA2Igst(appendDecimalDigit(e.getA2Igst()));
				dto.setA2Cgst(appendDecimalDigit(e.getA2Cgst()));
				dto.setA2Sgst(appendDecimalDigit(e.getA2Sgst()));
				dto.setA2Cess(appendDecimalDigit(e.getA2Cess()));
				dto.setLockStatus(e.getLockStatus());
				respListUnlockUpdated.add(dto);
			});
			// Incremental Data
			List<Object[]> objListIncrementalData = autoReconSummaryDao
					.getIncrementalDataSummary(recipientGstins);

			LOGGER.debug("Converting of list<obj[]> to List<dto> BEGIN");

			Object[] objIncremental = (Object[]) autoReconSummaryDao
					.getIncrementalDataUpdatedOn(entityId);
			Timestamp updatedOnIncremental = objIncremental[0] != null
					? (Timestamp) objIncremental[0] : null;
			String updatedOnItcIncremental = "";
			@SuppressWarnings("unused")
			String toDateItc = "";
			if (updatedOnIncremental != null) {
				updatedOnItcIncremental = EYDateUtil
						.toISTDateTimeFromUTC(updatedOnIncremental).toString();
				updatedOnItcIncremental = String
						.format("%-20s", updatedOnItcIncremental)
						.replace(' ', '0');
				updatedOnItcIncremental = updatedOnItcIncremental.substring(0,
						19);
				updatedOnItcIncremental = updatedOnItcIncremental.replace("T",
						" ");
				String toDate = updatedOnItcIncremental.substring(0, 10);
				LocalDate date = LocalDate.parse(toDate);
				date = date.plusDays(1);
				toDateItc = date + updatedOnItcIncremental.substring(10);
			}
			// Incremental List
			List<IncrementalDataSummaryDto> retListIncremental = objListIncrementalData
					.stream().map(o -> convertToIncrementalDataDto(o))
					.collect(Collectors.toCollection(ArrayList::new));

			Collections.sort(retListIncremental,
					(a, b) -> a.getOrder().compareToIgnoreCase(b.getOrder()));

			retListIncremental.forEach(e -> {
				if (e.getReportCategory().equalsIgnoreCase("Modified")) {
					e.setReportCategory("Modified Records");
				}

				if (e.getReportCategory().equalsIgnoreCase("Deleted")) {
					e.setReportCategory("Deleted Records");
				}

			});

			List<IncrementalDataSummaryFinalDto> retListIncrementalFinal = new ArrayList<IncrementalDataSummaryFinalDto>();

			retListIncremental.forEach(e -> {
				IncrementalDataSummaryFinalDto dto = new IncrementalDataSummaryFinalDto();
				dto.setReportCategory(e.getReportCategory());
				dto.setPrCount(e.getPrCount());
				dto.setPrTotalTax(appendDecimalDigit(e.getPrTotalTax()));
				dto.setPrTotalTaxPer(appendDecimalDigit(e.getPrTotalTaxPer()));
				dto.setPrIgst(appendDecimalDigit(e.getPrIgst()));
				dto.setPrCgst(appendDecimalDigit(e.getPrCgst()));
				dto.setPrSgst(appendDecimalDigit(e.getPrSgst()));
				dto.setPrCess(appendDecimalDigit(e.getPrCess()));
				dto.setA2Count(e.getA2Count());
				dto.setA2TotalTax(appendDecimalDigit(e.getA2TotalTax()));
				dto.setA2TotalTaxPer(appendDecimalDigit(e.getA2TotalTaxPer()));
				dto.setA2Igst(appendDecimalDigit(e.getA2Igst()));
				dto.setA2Cgst(appendDecimalDigit(e.getA2Cgst()));
				dto.setA2Sgst(appendDecimalDigit(e.getA2Sgst()));
				dto.setA2Cess(appendDecimalDigit(e.getA2Cess()));
				dto.setOrder(e.getOrder());
				retListIncrementalFinal.add(dto);
			});

			Optional<EntityInfoEntity> optional = entityInfoRepo
					.findById(entityId);
			EntityInfoEntity entity = optional.get();
			String entityName = entity.getEntityName();
			String gstinList = String.join(",", recipientGstins);

			workbook = writeToExcel(respListlockUpdated, respListUnlockUpdated,
					respGrandTotal, retListIncrementalFinal, entityName,
					gstinList, fromTaxPeriod2A, toTaxPeriod2A, fromTaxPeriodPR,
					toTaxPeriodPR, fromReconDate, toReconDate, entityId,criteria);
			return workbook;
		} catch (Exception ee) {
			String msg = String.format(
					"Error while Executing getReconSummaryDetails  "
							+ "  for recipientGstins :%s"
							+ " , fromTaxPeriodPR :%s, toTaxPeriodPR :%s "
							+ " , fromTaxPeriod2A :%s ,toTaxPeriod2A :%s "
							+ " , fromReconDate :%s and toReconDate :%s ",
					recipientGstins, fromTaxPeriodPR, toTaxPeriodPR,
					fromTaxPeriod2A, toTaxPeriod2A, fromReconDate, toReconDate);
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}

	}

	private Workbook writeToExcel(
			List<AutoReconSummaryGrandTotalDto> respListLock,
			List<AutoReconSummaryGrandTotalDto> respListUnlock,
			List<AutoReconSummaryGrandTotalDto> respGrandTotal,
			List<IncrementalDataSummaryFinalDto> retListIncremental,
			String entityName, String gstins, String fromTaxPeriod2A,
			String toTaxPeriod2A, String fromTaxPeriodPR, String toTaxPeriodPR,
			String fromReconDate, String toReconDate, Long entityId,String criteria) {

		Workbook workbook = null;

		if (respListLock != null && !respListLock.isEmpty()
				|| respListUnlock != null && !respListUnlock.isEmpty()
				|| retListIncremental != null
						&& !retListIncremental.isEmpty()) {

			String[] invoiceHeadersRecon = commonUtility
					.getProp("auto.recon.summary.report.header").split(",");

			String[] invoiceHeadersIncremental = commonUtility
					.getProp("auto.recon.incremental.summary.report.header")
					.split(",");

			if (LOGGER.isDebugEnabled()) {
				String msg = "AutoReconSummaryReportDownloadServiceImpl.writeToExcel "
						+ "workbook created writing data to the workbook";
				LOGGER.debug(msg);
			}

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates", "AutoReconSummary.xlsx");

			Cells reportCells = workbook.getWorksheets().get(0).getCells();

			Worksheet sheet = workbook.getWorksheets().get(0);

			String gstinsList = String.join(",", gstins);

			Cell cell = sheet.getCells().get("C4");
			Cell cell1 = sheet.getCells().get("C5");
			Cell cell2 = sheet.getCells().get("G4");
			Cell cell3 = sheet.getCells().get("G5");

			String taxPeriodPr = fromTaxPeriodPR + "-" + toTaxPeriodPR;
			String taxPeriod2A = fromTaxPeriod2A + "-" + toTaxPeriod2A;

			cell.setValue(entityName);
			cell1.setValue(gstinsList);
			if(criteria.equalsIgnoreCase("PRtaxperiod")){
			cell2.setValue(taxPeriodPr);
			
			}
			else if(criteria.equalsIgnoreCase("2Ataxperiod"))
			{
				cell3.setValue(taxPeriod2A);
			}
			else
			{
				cell2.setValue(taxPeriodPr);
				cell3.setValue(taxPeriod2A);
			}

			Cells cells = sheet.getCells();

			reportCells.importCustomObjects(respListLock, invoiceHeadersRecon,
					false, 10, 1, respListLock.size(), true, "yyyy-mm-dd",
					false);

			reportCells.importCustomObjects(respListUnlock, invoiceHeadersRecon,
					false, 30, 1, respListUnlock.size(), true, "yyyy-mm-dd",
					false);

			reportCells.importCustomObjects(respGrandTotal, invoiceHeadersRecon,
					false, 51, 1, respListUnlock.size(), true, "yyyy-mm-dd",
					false);

			reportCells.importCustomObjects(retListIncremental,
					invoiceHeadersIncremental, false, 60, 2,
					retListIncremental.size(), true, "yyyy-mm-dd", false);
			cells.merge(60, 1, 1, 2);//58
			cells.merge(61, 1, 1, 2);//59
			cells.merge(62, 1, 1, 2);//60
		}
		return workbook;
	}

	private IncrementalDataSummaryDto convertToIncrementalDataDto(
			Object[] arr) {
		IncrementalDataSummaryDto dto = new IncrementalDataSummaryDto();
		try {
			String reportCateg = arr[0] != null ? (String) arr[0] : "";
			dto.setReportCategory(reportCateg);
			dto.setPrCount(
					arr[1] != null ? (GenUtil.getBigInteger(arr[1])).longValue() : 0);
			dto.setPrTotalTax(GenUtil.defaultToZeroIfNull((BigDecimal) arr[2]));
			dto.setPrTotalTaxPer(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[3]));
			dto.setPrIgst(GenUtil.defaultToZeroIfNull((BigDecimal) arr[4]));
			dto.setPrCgst(GenUtil.defaultToZeroIfNull((BigDecimal) arr[5]));
			dto.setPrSgst(GenUtil.defaultToZeroIfNull((BigDecimal) arr[6]));
			dto.setPrCess(GenUtil.defaultToZeroIfNull((BigDecimal) arr[7]));
			dto.setA2Count(
					arr[8] != null ? (GenUtil.getBigInteger(arr[8])).longValue() : 0);
			dto.setA2TotalTax(GenUtil.defaultToZeroIfNull((BigDecimal) arr[9]));
			dto.setA2TotalTaxPer(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[10]));
			dto.setA2Igst(GenUtil.defaultToZeroIfNull((BigDecimal) arr[11]));
			dto.setA2Cgst(GenUtil.defaultToZeroIfNull((BigDecimal) arr[12]));
			dto.setA2Sgst(GenUtil.defaultToZeroIfNull((BigDecimal) arr[13]));
			dto.setA2Cess(GenUtil.defaultToZeroIfNull((BigDecimal) arr[14]));
			if (reportCateg.equalsIgnoreCase("New Records"))
				dto.setOrder("A");
			else if (reportCateg.equalsIgnoreCase("Modified"))
				dto.setOrder("B");
			else if (reportCateg.equalsIgnoreCase("Deleted"))
				dto.setOrder("C");
			else
				dto.setOrder("D");

			return dto;
		} catch (Exception ee) {
			String msg = String.format("Error while converting to "
					+ " Incremental Data Summary dto");
			LOGGER.error(msg, ee);
			throw new AppException(msg, ee);
		}
	}

	private AutoReconSummaryDto convertToDto(Object[] arr) {
		AutoReconSummaryDto dto = new AutoReconSummaryDto();
		try {

			dto.setReportCategory(arr[0] != null ? (String) arr[0] : "");
			String perticularName = arr[1] != null ? (String) arr[1] : "";
			dto.setReportType(perticularName);
			if(perticularName.equalsIgnoreCase("Addition in PR(Imports)")) {
				dto.setReportType("Addition in PR (Imports)");
			}else if(perticularName.equalsIgnoreCase("Addition in 2A(Imports)")) {
				dto.setReportType("Addition in 2A (Imports)");
			}
			
			dto.setPrCount(
					arr[2] != null ? (GenUtil.getBigInteger(arr[2])).longValue() : 0);
			dto.setPrTotalTax(GenUtil.defaultToZeroIfNull((BigDecimal) arr[3]));
			dto.setPrTotalTaxPer(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[4]));
			dto.setPrIgst(GenUtil.defaultToZeroIfNull((BigDecimal) arr[5]));
			dto.setPrCgst(GenUtil.defaultToZeroIfNull((BigDecimal) arr[6]));
			dto.setPrSgst(GenUtil.defaultToZeroIfNull((BigDecimal) arr[7]));
			dto.setPrCess(GenUtil.defaultToZeroIfNull((BigDecimal) arr[8]));
			dto.setA2Count(
					arr[9] != null ? (GenUtil.getBigInteger(arr[9])).longValue() : 0);
			dto.setA2TotalTax(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[10]));
			dto.setA2TotalTaxPer(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[11]));
			dto.setA2Igst(GenUtil.defaultToZeroIfNull((BigDecimal) arr[12]));
			dto.setA2Cgst(GenUtil.defaultToZeroIfNull((BigDecimal) arr[13]));
			dto.setA2Sgst(GenUtil.defaultToZeroIfNull((BigDecimal) arr[14]));
			dto.setA2Cess(GenUtil.defaultToZeroIfNull((BigDecimal) arr[15]));
			dto.setLockStatus(arr[16] != null ? (String) arr[16] : "");

			if (perticularName.equalsIgnoreCase("Exact Match")
					|| perticularName.equalsIgnoreCase("Match With Tolerance")
					|| perticularName.equalsIgnoreCase("Value Mismatch")
					|| perticularName.equalsIgnoreCase("POS Mismatch")
					|| perticularName.equalsIgnoreCase("Doc Date Mismatch")
					|| perticularName.equalsIgnoreCase("Doc Type Mismatch")
					|| perticularName.equalsIgnoreCase("Doc No Mismatch I")
					|| perticularName.equalsIgnoreCase("Multi-Mismatch")
					|| perticularName.equalsIgnoreCase("Doc No Mismatch II")
					|| perticularName.equalsIgnoreCase("Addition in PR")
					|| perticularName.equalsIgnoreCase("Addition in 2A")
					|| perticularName.equalsIgnoreCase("Addition in PR(Imports)")
					|| perticularName.equalsIgnoreCase("Addition in 2A(Imports)")
					|| perticularName.equalsIgnoreCase("Doc No & Doc Date Mismatch")) {
				dto.setLevel("L2");
			}
			return dto;
		} catch (Exception ee) {
			String msg = String.format("Error while converting to dto");
			LOGGER.error(msg, ee);
			throw new AppException(msg, ee);
		}

	}

	private List<AutoReconSummaryDto> convertScreenLevelData(
			List<String> particular, List<AutoReconSummaryDto> retList,
			String type) {
		// Match
		Long prCountMatch = 0L;
		BigDecimal prTotalTaxMatch = BigDecimal.ZERO;
		BigDecimal prTotalTaxPerMatch = BigDecimal.ZERO;
		BigDecimal prIgstMatch = BigDecimal.ZERO;
		BigDecimal prCgstMatch = BigDecimal.ZERO;
		BigDecimal prSgstMatch = BigDecimal.ZERO;
		BigDecimal prCessMatch = BigDecimal.ZERO;
		Long a2CountMatch = 0L;
		BigDecimal a2TotalTaxMatch = BigDecimal.ZERO;
		BigDecimal a2TotalTaxPerMatch = BigDecimal.ZERO;
		BigDecimal a2IgstMatch = BigDecimal.ZERO;
		BigDecimal a2CgstMatch = BigDecimal.ZERO;
		BigDecimal a2SgstMatch = BigDecimal.ZERO;
		BigDecimal a2CessMatch = BigDecimal.ZERO;

		// MisMatch
		Long prCountMisMatch = 0L;
		BigDecimal prTotalTaxMisMatch = BigDecimal.ZERO;
		BigDecimal prTotalTaxPerMisMatch = BigDecimal.ZERO;
		BigDecimal prIgstMisMatch = BigDecimal.ZERO;
		BigDecimal prCgstMisMatch = BigDecimal.ZERO;
		BigDecimal prSgstMisMatch = BigDecimal.ZERO;
		BigDecimal prCessMisMatch = BigDecimal.ZERO;
		Long a2CountMisMatch = 0L;
		BigDecimal a2TotalTaxMisMatch = BigDecimal.ZERO;
		BigDecimal a2TotalTaxPerMisMatch = BigDecimal.ZERO;
		BigDecimal a2IgstMisMatch = BigDecimal.ZERO;
		BigDecimal a2CgstMisMatch = BigDecimal.ZERO;
		BigDecimal a2SgstMisMatch = BigDecimal.ZERO;
		BigDecimal a2CessMisMatch = BigDecimal.ZERO;

		// Additional Entries
		Long prCountAe = 0L;
		BigDecimal prTotalTaxAe = BigDecimal.ZERO;
		BigDecimal prTotalTaxPerAe = BigDecimal.ZERO;
		BigDecimal prIgstAe = BigDecimal.ZERO;
		BigDecimal prCgstAe = BigDecimal.ZERO;
		BigDecimal prSgstAe = BigDecimal.ZERO;
		BigDecimal prCessAe = BigDecimal.ZERO;
		Long a2CountAe = 0L;
		BigDecimal a2TotalTaxAe = BigDecimal.ZERO;
		BigDecimal a2TotalTaxPerAe = BigDecimal.ZERO;
		BigDecimal a2IgstAe = BigDecimal.ZERO;
		BigDecimal a2CgstAe = BigDecimal.ZERO;
		BigDecimal a2SgstAe = BigDecimal.ZERO;
		BigDecimal a2CessAe = BigDecimal.ZERO;

		// Force Match/3B Response
		Long prCountFm = 0L;
		BigDecimal prTotalTaxFm = BigDecimal.ZERO;
		BigDecimal prTotalTaxPerFm = BigDecimal.ZERO;
		BigDecimal prIgstFm = BigDecimal.ZERO;
		BigDecimal prCgstFm = BigDecimal.ZERO;
		BigDecimal prSgstFm = BigDecimal.ZERO;
		BigDecimal prCessFm = BigDecimal.ZERO;
		Long a2CountFm = 0L;
		BigDecimal a2TotalTaxFm = BigDecimal.ZERO;
		BigDecimal a2TotalTaxPerFm = BigDecimal.ZERO;
		BigDecimal a2IgstFm = BigDecimal.ZERO;
		BigDecimal a2CgstFm = BigDecimal.ZERO;
		BigDecimal a2SgstFm = BigDecimal.ZERO;
		BigDecimal a2CessFm = BigDecimal.ZERO;

		List<AutoReconSummaryDto> obj = new ArrayList<>();
		final String level = "L1";

		LOGGER.debug("Calculate value for level 1 and level 2 started");

		for (AutoReconSummaryDto resp : retList) {

			if (type.equalsIgnoreCase(resp.getLockStatus())) {

				AutoReconSummaryDto gstr2Recon = new AutoReconSummaryDto();
				if ("Match".equalsIgnoreCase(resp.getReportCategory())) {
					prCountMatch = prCountMatch + resp.getPrCount();
					prTotalTaxMatch = prTotalTaxMatch.add(resp.getPrTotalTax());
					prTotalTaxPerMatch = prTotalTaxPerMatch
							.add(resp.getPrTotalTaxPer());
					prIgstMatch = prIgstMatch.add(resp.getPrIgst());
					prCgstMatch = prCgstMatch.add(resp.getPrCgst());
					prSgstMatch = prSgstMatch.add(resp.getPrSgst());
					prCessMatch = prCessMatch.add(resp.getPrCess());

					a2CountMatch = a2CountMatch + resp.getA2Count();
					a2TotalTaxMatch = a2TotalTaxMatch.add(resp.getA2TotalTax());
					a2TotalTaxPerMatch = a2TotalTaxPerMatch
							.add(resp.getA2TotalTaxPer());
					a2IgstMatch = a2IgstMatch.add(resp.getA2Igst());
					a2CgstMatch = a2CgstMatch.add(resp.getA2Cgst());
					a2SgstMatch = a2SgstMatch.add(resp.getA2Sgst());
					a2CessMatch = a2CessMatch.add(resp.getA2Cess());
				} else if ("MisMatch"
						.equalsIgnoreCase(resp.getReportCategory())) {
					prCountMisMatch = prCountMisMatch + resp.getPrCount();
					prTotalTaxMisMatch = prTotalTaxMisMatch
							.add(resp.getPrTotalTax());
					prTotalTaxPerMisMatch = prTotalTaxPerMisMatch
							.add(resp.getPrTotalTaxPer());
					prIgstMisMatch = prIgstMisMatch.add(resp.getPrIgst());
					prCgstMisMatch = prCgstMisMatch.add(resp.getPrCgst());
					prSgstMisMatch = prSgstMisMatch.add(resp.getPrSgst());
					prCessMisMatch = prCessMisMatch.add(resp.getPrCess());

					a2CountMisMatch = a2CountMisMatch + resp.getA2Count();
					a2TotalTaxMisMatch = a2TotalTaxMisMatch
							.add(resp.getA2TotalTax());
					a2TotalTaxPerMisMatch = a2TotalTaxPerMisMatch
							.add(resp.getA2TotalTaxPer());
					a2IgstMisMatch = a2IgstMisMatch.add(resp.getA2Igst());
					a2CgstMisMatch = a2CgstMisMatch.add(resp.getA2Cgst());
					a2SgstMisMatch = a2SgstMisMatch.add(resp.getA2Sgst());
					a2CessMisMatch = a2CessMisMatch.add(resp.getA2Cess());
				} else if ("Additional Entries"
						.equalsIgnoreCase(resp.getReportCategory())) {
					prCountAe = prCountAe + resp.getPrCount();
					prTotalTaxAe = prTotalTaxAe.add(resp.getPrTotalTax());
					prTotalTaxPerAe = prTotalTaxPerAe
							.add(resp.getPrTotalTaxPer());
					prIgstAe = prIgstAe.add(resp.getPrIgst());
					prCgstAe = prCgstAe.add(resp.getPrCgst());
					prSgstAe = prSgstAe.add(resp.getPrSgst());
					prCessAe = prCessAe.add(resp.getPrCess());

					a2CountAe = a2CountAe + resp.getA2Count();
					a2TotalTaxAe = a2TotalTaxAe.add(resp.getA2TotalTax());
					a2TotalTaxPerAe = a2TotalTaxPerAe
							.add(resp.getA2TotalTaxPer());
					a2IgstAe = a2IgstAe.add(resp.getA2Igst());
					a2CgstAe = a2CgstAe.add(resp.getA2Cgst());
					a2SgstAe = a2SgstAe.add(resp.getA2Sgst());
					a2CessAe = a2CessAe.add(resp.getA2Cess());
				} else if ("Force Match/3B Response"
						.equalsIgnoreCase(resp.getReportCategory())) {
					prCountFm = prCountFm + resp.getPrCount();
					prTotalTaxFm = prTotalTaxFm.add(resp.getPrTotalTax());
					prTotalTaxPerFm = prTotalTaxPerFm
							.add(resp.getPrTotalTaxPer());
					prIgstFm = prIgstFm.add(resp.getPrIgst());
					prCgstFm = prCgstFm.add(resp.getPrCgst());
					prSgstFm = prSgstFm.add(resp.getPrSgst());
					prCessFm = prCessFm.add(resp.getPrCess());

					a2CountFm = a2CountFm + resp.getA2Count();
					a2TotalTaxFm = a2TotalTaxFm.add(resp.getA2TotalTax());
					a2TotalTaxPerFm = a2TotalTaxPerFm
							.add(resp.getA2TotalTaxPer());
					a2IgstFm = a2IgstFm.add(resp.getA2Igst());
					a2CgstFm = a2CgstFm.add(resp.getA2Cgst());
					a2SgstFm = a2SgstFm.add(resp.getA2Sgst());
					a2CessFm = a2CessFm.add(resp.getA2Cess());
				}

				String perticularName = (resp.getReportType() != null)
						? (String) resp.getReportType() : "";
				if ("TOTAL".equalsIgnoreCase(resp.getReportCategory())
						|| "GRAND TOTAL"
								.equalsIgnoreCase(resp.getReportCategory()))
					gstr2Recon.setReportCategory(resp.getReportCategory());
				if (!"TOTAL".equalsIgnoreCase(resp.getReportCategory())
						&& !"GRAND TOTAL"
								.equalsIgnoreCase(resp.getReportCategory()))
					gstr2Recon.setReportType(perticularName);
				gstr2Recon.setPrCount(resp.getPrCount());
				gstr2Recon.setPrTotalTax((BigDecimal) resp.getPrTotalTax());
				gstr2Recon
						.setPrTotalTaxPer((BigDecimal) resp.getPrTotalTaxPer());
				gstr2Recon.setPrIgst((BigDecimal) resp.getPrIgst());
				gstr2Recon.setPrCgst((BigDecimal) resp.getPrCgst());
				gstr2Recon.setPrSgst((BigDecimal) resp.getPrSgst());
				gstr2Recon.setPrCess((BigDecimal) resp.getPrCess());

				gstr2Recon.setA2Count(resp.getA2Count());
				gstr2Recon.setA2TotalTax((BigDecimal) resp.getA2TotalTax());
				gstr2Recon
						.setA2TotalTaxPer((BigDecimal) resp.getA2TotalTaxPer());
				gstr2Recon.setA2Igst((BigDecimal) resp.getA2Igst());
				gstr2Recon.setA2Cgst((BigDecimal) resp.getA2Cgst());
				gstr2Recon.setA2Sgst((BigDecimal) resp.getA2Sgst());
				gstr2Recon.setA2Cess((BigDecimal) resp.getA2Cess());
				gstr2Recon.setLockStatus(type);

				if ("TOTAL".equalsIgnoreCase(resp.getReportCategory())) {
					gstr2Recon.setOrderPosition("W");
					gstr2Recon.setLevel("L1");
				} else if ("GRAND TOTAL"
						.equalsIgnoreCase(resp.getReportCategory())) {
					gstr2Recon.setOrderPosition("X");
					gstr2Recon.setLevel("L1");
				} else {
					gstr2Recon.setLevel("L2");
				}

				if (perticularName.equalsIgnoreCase("Exact Match")) {
					gstr2Recon.setOrderPosition("B");
				} else if (perticularName
						.equalsIgnoreCase("Match With Tolerance")) {
					gstr2Recon.setOrderPosition("C");
				} else if (perticularName.equalsIgnoreCase("Match (Imports)")) {
					gstr2Recon.setOrderPosition("D");
				}

				else if (perticularName.equalsIgnoreCase("Value Mismatch")) {
					gstr2Recon.setOrderPosition("F");
				} else if (perticularName.equalsIgnoreCase("POS Mismatch")) {
					gstr2Recon.setOrderPosition("G");
				} else if (perticularName
						.equalsIgnoreCase("Doc Date Mismatch")) {
					gstr2Recon.setOrderPosition("H");
				} else if (perticularName
						.equalsIgnoreCase("Doc Type Mismatch")) {
					gstr2Recon.setOrderPosition("I");
				} else if (perticularName
						.equalsIgnoreCase("Doc No Mismatch I")) {
					gstr2Recon.setOrderPosition("J");
				} else if (perticularName
						.equalsIgnoreCase("Doc No Mismatch II")) {
					gstr2Recon.setOrderPosition("K");
				} else if (perticularName
						.equalsIgnoreCase("Doc No & Doc Date Mismatch")) {
					gstr2Recon.setOrderPosition("L");
				} else if (perticularName.equalsIgnoreCase("Multi-Mismatch")) {
					gstr2Recon.setOrderPosition("M");
				}

				else if (perticularName
						.equalsIgnoreCase("Mismatch (Imports)")) {
					gstr2Recon.setOrderPosition("N");
				}

				else if (perticularName.equalsIgnoreCase("Potential-I")) {
					gstr2Recon.setOrderPosition("O");
				} else if (perticularName.equalsIgnoreCase("Potential-II")) {
					gstr2Recon.setOrderPosition("P");
				} else if (perticularName.equalsIgnoreCase("Logical Match")) {
					gstr2Recon.setOrderPosition("Q");
				}

				else if (perticularName.equalsIgnoreCase("Addition in PR")) {
					gstr2Recon.setOrderPosition("S");
				}
				
				else if (perticularName.equalsIgnoreCase("Addition in 2A")) {
					gstr2Recon.setOrderPosition("T");
				} 
				else if (perticularName.equalsIgnoreCase("Addition in PR (Imports)")) {
					gstr2Recon.setOrderPosition("T");
				}
				
				else if (perticularName.equalsIgnoreCase("Addition in 2A (Imports)")) {
					gstr2Recon.setOrderPosition("T");
				} 
				
				else if (perticularName.equalsIgnoreCase("Force Match")) {
					gstr2Recon.setOrderPosition("V");
				} else if (perticularName
						.equalsIgnoreCase("GSTR-3B Locked Records")) {
					gstr2Recon.setOrderPosition("W");
				}
				obj.add(gstr2Recon);
			}
		}
		LOGGER.debug("Calculate value for level 1 and level 2 ended");
		LOGGER.debug("Setting values for level 1 started");

		for (int i = 0; i < particular.size(); i++) {
			AutoReconSummaryDto gstr2Recon = new AutoReconSummaryDto();
			if ("Match".equalsIgnoreCase(particular.get(i))) {
				gstr2Recon.setReportCategory(particular.get(i));
				gstr2Recon.setPrCount(prCountMatch);
				gstr2Recon.setPrTotalTax(prTotalTaxMatch);
				gstr2Recon.setPrTotalTaxPer(prTotalTaxPerMatch);
				gstr2Recon.setPrIgst(prIgstMatch);
				gstr2Recon.setPrCgst(prCgstMatch);
				gstr2Recon.setPrSgst(prSgstMatch);
				gstr2Recon.setPrCess(prCessMatch);
				gstr2Recon.setA2Count(a2CountMatch);
				gstr2Recon.setA2TotalTax(a2TotalTaxMatch);
				gstr2Recon.setA2TotalTaxPer(a2TotalTaxPerMatch);
				gstr2Recon.setA2Igst(a2IgstMatch);
				gstr2Recon.setA2Cgst(a2CgstMatch);
				gstr2Recon.setA2Sgst(a2SgstMatch);
				gstr2Recon.setA2Cess(a2CessMatch);
				gstr2Recon.setLevel(level);
				gstr2Recon.setOrderPosition("A");
				gstr2Recon.setLockStatus(type);
				obj.add(gstr2Recon);
			} else if ("Mismatch".equalsIgnoreCase(particular.get(i))) {
				gstr2Recon.setReportCategory(particular.get(i));
				gstr2Recon.setPrCount(prCountMisMatch);
				gstr2Recon.setPrTotalTax(prTotalTaxMisMatch);
				gstr2Recon.setPrTotalTaxPer(prTotalTaxPerMisMatch);
				gstr2Recon.setPrIgst(prIgstMisMatch);
				gstr2Recon.setPrCgst(prCgstMisMatch);
				gstr2Recon.setPrSgst(prSgstMisMatch);
				gstr2Recon.setPrCess(prCessMisMatch);
				gstr2Recon.setA2Count(a2CountMisMatch);
				gstr2Recon.setA2TotalTax(a2TotalTaxMisMatch);
				gstr2Recon.setA2TotalTaxPer(a2TotalTaxPerMisMatch);
				gstr2Recon.setA2Igst(a2IgstMisMatch);
				gstr2Recon.setA2Cgst(a2CgstMisMatch);
				gstr2Recon.setA2Sgst(a2SgstMisMatch);
				gstr2Recon.setA2Cess(a2CessMisMatch);
				gstr2Recon.setLevel(level);
				gstr2Recon.setOrderPosition("E");
				gstr2Recon.setLockStatus(type);
				obj.add(gstr2Recon);

			} else if ("Additional Entries"
					.equalsIgnoreCase(particular.get(i))) {
				if (type.equals("Unlock")) {
					gstr2Recon.setReportCategory(particular.get(i));
					gstr2Recon.setPrCount(prCountAe);
					gstr2Recon.setPrTotalTax(prTotalTaxAe);
					gstr2Recon.setPrTotalTaxPer(prTotalTaxPerAe);
					gstr2Recon.setPrIgst(prIgstAe);
					gstr2Recon.setPrCgst(prCgstAe);
					gstr2Recon.setPrSgst(prSgstAe);
					gstr2Recon.setPrCess(prCessAe);
					gstr2Recon.setA2Count(a2CountAe);
					gstr2Recon.setA2TotalTax(a2TotalTaxAe);
					gstr2Recon.setA2TotalTaxPer(a2TotalTaxPerAe);
					gstr2Recon.setA2Igst(a2IgstAe);
					gstr2Recon.setA2Cgst(a2CgstAe);
					gstr2Recon.setA2Sgst(a2SgstAe);
					gstr2Recon.setA2Cess(a2CessAe);
					gstr2Recon.setLevel(level);
					gstr2Recon.setOrderPosition("R");
					gstr2Recon.setLockStatus(type);
					obj.add(gstr2Recon);
				}
			} else if ("Force Match/3B Response"
					.equalsIgnoreCase(particular.get(i))) {
				if (type.equalsIgnoreCase("Auto Lock")) {
					gstr2Recon.setReportCategory(particular.get(i));
					gstr2Recon.setPrCount(prCountFm);
					gstr2Recon.setPrTotalTax(prTotalTaxFm);
					gstr2Recon.setPrTotalTaxPer(prTotalTaxPerFm);
					gstr2Recon.setPrIgst(prIgstFm);
					gstr2Recon.setPrCgst(prCgstFm);
					gstr2Recon.setPrSgst(prSgstFm);
					gstr2Recon.setPrCess(prCessFm);
					gstr2Recon.setA2Count(a2CountFm);
					gstr2Recon.setA2TotalTax(a2TotalTaxFm);
					gstr2Recon.setA2TotalTaxPer(a2TotalTaxPerFm);
					gstr2Recon.setA2Igst(a2IgstFm);
					gstr2Recon.setA2Cgst(a2CgstFm);
					gstr2Recon.setA2Sgst(a2SgstFm);
					gstr2Recon.setA2Cess(a2CessFm);
					gstr2Recon.setLevel(level);
					gstr2Recon.setOrderPosition("U");
					gstr2Recon.setLockStatus(type);
					obj.add(gstr2Recon);
				}
			}

		}
		LOGGER.debug("Setting values for level 1 ended");

		return obj;
	}

	private String appendDecimalDigit(BigDecimal b) {

		String val = b.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();

		String[] s = val.split("\\.");
		if (s.length == 2) {
			if (s[1].length() == 1)
				return "'" + (s[0] + "." + s[1] + "0");
			else {
				return "'" + val;
			}
		} else
			return "'" + (val + ".00");

	}
}
