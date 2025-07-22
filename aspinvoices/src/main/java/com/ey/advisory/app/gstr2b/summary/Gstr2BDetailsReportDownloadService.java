package com.ey.advisory.app.gstr2b.summary;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.config.ConfigConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr2BDetailsReportDownloadService")
public class Gstr2BDetailsReportDownloadService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr2BDetailsDaoImpl")
	private Gstr2BDetailsDao dao;

	public Workbook writeToExcel(List<String> gstins, String fromTaxPeriod,
			String toTaxPeriod) {

		Workbook workbook = null;
		int startRow = 5;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Gstr2BDetailsDto> dbResp = dao.getDetailsResp(gstins, toTaxPeriod,
				fromTaxPeriod);

		List<Gstr2BEntityLevelTableSummaryReportDto> resp = dbResp.stream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));

		if (resp != null && !resp.isEmpty()) {

			String[] invoiceHeaders = commonUtility
					.getProp(
							"gstr2b.entity.level.table.wise.summary.report.column")
					.split(",");

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates",
					"GSTR 2B Entity Level Table wise summary Report.xlsx");

			if (LOGGER.isDebugEnabled()) {
				String msg = "Gstr2BDetailsReportDownloadService.writeToExcel "
						+ "workbook created writing data to the workbook";
				LOGGER.debug(msg);
			}

			Cells reportCells = workbook.getWorksheets().get(0).getCells();

			Worksheet sheet = workbook.getWorksheets().get(0);

			Cell cell2 = sheet.getCells().get("B2");
			Cell cell3 = sheet.getCells().get("E2");

			String yearF = fromTaxPeriod.substring(0, 4);
			String monthF = fromTaxPeriod.substring(4, 6);
			String fromTaxPer = monthF + yearF;

			String yearT = toTaxPeriod.substring(0, 4);
			String monthT = toTaxPeriod.substring(4, 6);
			String toTaxPer = monthT + yearT;

			cell2.setValue(fromTaxPer);
			cell3.setValue(toTaxPer);

			reportCells.importCustomObjects(resp, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn, resp.size(), true,
					"yyyy-mm-dd", false);
			try {
				if (LOGGER.isDebugEnabled()) {
					String msg = "Gstr2BDetailsReportDownloadService"
							+ ".writeToExcel saving workbook";
					LOGGER.debug(msg);
				}

				workbook.save(ConfigConstants.GSTR2B_DETAILS_REPORT,
						SaveFormat.XLSX);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Workbook has been generated successfully with the"
									+ " error response list in the directory : %s",
							workbook.getAbsolutePath());
				}
			} catch (Exception e) {
				String msg = String.format(
						"Exception occured while "
								+ "saving excel sheet into folder, %s ",
						e.getMessage());
				LOGGER.error(msg);
				throw new AppException(e.getMessage(), e);
			}

		} else {
			throw new AppException("No records found, cannot generate report");
		}
		return workbook;
	}

	private Gstr2BEntityLevelTableSummaryReportDto convert(Gstr2BDetailsDto o) {

		Gstr2BEntityLevelTableSummaryReportDto dto = new Gstr2BEntityLevelTableSummaryReportDto();
		String docType = o.getTableName();
		if (docType != null && (docType.equalsIgnoreCase("CR")
				|| docType.equalsIgnoreCase("C")
				|| docType.equalsIgnoreCase("RCR")
				|| docType.equalsIgnoreCase("B2B - Credit notes")
				|| docType.equalsIgnoreCase("B2B - Credit notes (Amendment)")
				|| docType
						.equalsIgnoreCase("B2B - Credit notes (Reverse charge)")
				|| docType.equalsIgnoreCase(
						"B2B - Credit notes (Reverse charge)(Amendment)")
				|| docType.equalsIgnoreCase("ISD - Credit notes") || docType
						.equalsIgnoreCase("ISD - Credit notes (Amendment)"))) {
			
		/*	dto.setAvailItcCess(CheckForNegativeValue(o.getAvailItcCess()));
			dto.setAvailItcCgst(CheckForNegativeValue(o.getAvailItcCgst()));
			dto.setAvailItcIgst(CheckForNegativeValue(o.getAvailItcIgst()));
			dto.setAvailItcSgst(CheckForNegativeValue(o.getAvailItcSgst()));
			dto.setNonAvailItcCess(CheckForNegativeValue(o.getNonAvailItcCess()));
			dto.setNonAvailItcCgst(CheckForNegativeValue(o.getNonAvailItcCgst()));
			dto.setNonAvailItcIgst(CheckForNegativeValue(o.getNonAvailItcIgst()));
			dto.setNonAvailItcSgst(CheckForNegativeValue(o.getNonAvailItcSgst()));
			dto.setTotalTaxCess(CheckForNegativeValue(o.getTotalTaxCess()));
			dto.setTotalTaxCgst(CheckForNegativeValue(o.getTotalTaxCgst()));
			dto.setTotalTaxIgst(CheckForNegativeValue(o.getTotalTaxIgst()));
			dto.setTotalTaxSgst(CheckForNegativeValue(o.getTotalTaxSgst()));*/
			
			dto.setAvailItcCess(o.getAvailItcCess());
			dto.setAvailItcCgst(o.getAvailItcCgst());
			dto.setAvailItcIgst(o.getAvailItcIgst());
			dto.setAvailItcSgst(o.getAvailItcSgst());
			dto.setNonAvailItcCess(o.getNonAvailItcCess());
			dto.setNonAvailItcCgst(o.getNonAvailItcCgst());
			dto.setNonAvailItcIgst(o.getNonAvailItcIgst());
			dto.setNonAvailItcSgst(o.getNonAvailItcSgst());
			dto.setTotalTaxCess(o.getTotalTaxCess());
			dto.setTotalTaxCgst(o.getTotalTaxCgst());
			dto.setTotalTaxIgst(o.getTotalTaxIgst());
			dto.setTotalTaxSgst(o.getTotalTaxSgst());
			
			dto.setRejectedItcCess(o.getRejectedItcCess());
			dto.setRejectedItcCgst(o.getRejectedItcCgst());
			dto.setRejectedItcIgst(o.getRejectedItcIgst());
			dto.setRejectedItcSgst(o.getRejectedItcSgst());
			
			
			
		} else {
			
			dto.setAvailItcCess(o.getAvailItcCess());
			dto.setAvailItcCgst(o.getAvailItcCgst());
			dto.setAvailItcIgst(o.getAvailItcIgst());
			dto.setAvailItcSgst(o.getAvailItcSgst());
			dto.setNonAvailItcCess(o.getNonAvailItcCess());
			dto.setNonAvailItcCgst(o.getNonAvailItcCgst());
			dto.setNonAvailItcIgst(o.getNonAvailItcIgst());
			dto.setNonAvailItcSgst(o.getNonAvailItcSgst());
			dto.setTotalTaxCess(o.getTotalTaxCess());
			dto.setTotalTaxCgst(o.getTotalTaxCgst());
			dto.setTotalTaxIgst(o.getTotalTaxIgst());
			dto.setTotalTaxSgst(o.getTotalTaxSgst());
			
			dto.setRejectedItcCess(o.getRejectedItcCess());
			dto.setRejectedItcCgst(o.getRejectedItcCgst());
			dto.setRejectedItcIgst(o.getRejectedItcIgst());
			dto.setRejectedItcSgst(o.getRejectedItcSgst());
		}

		
		// docTYpe
		dto.setTableName(o.getTableName());
		dto.setRecipientGstin(o.getRgstin());

		return dto;
	}

	/*private BigDecimal CheckForNegativeValue(Object value) {

		if (value != null) {
			if (value instanceof BigDecimal) {
				return new BigDecimal((value != null
						? ((((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null));
			}
		}
		return null;
	}*/
}
