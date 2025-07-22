package com.ey.advisory.app.gstr2b.summary;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr2BSummaryReportDownloadService")
public class Gstr2BSummaryReportDownloadService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr2BSummaryDaoImpl")
	private Gstr2BSummaryDaoImpl dao;

	public Workbook writeToExcel(List<String> gstins, String fromTaxPeriod,
			String toTaxPeriod) {

		Workbook workbook = null;
		int startRow = 5;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Gstr2BSummaryDto> dbResp = dao.getSummaryResp(gstins, toTaxPeriod,
				fromTaxPeriod);

		List<Gstr2BEntityLevelSummaryReportDto> resp = dbResp.stream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));

		if (resp != null && !resp.isEmpty()) {

			String[] invoiceHeaders = commonUtility
					.getProp("gstr2b.entity.level.summary.report.column")
					.split(",");

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates",
					"GSTR 2B Entity Level Summary Report.xlsx");

			if (LOGGER.isDebugEnabled()) {
				String msg = "Gstr2BSummaryReportDownloadService.writeToExcel "
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
					String msg = "Gstr2BSummaryReportDownloadService"
							+ ".writeToExcel saving workbook";
					LOGGER.debug(msg);
				}

				workbook.save(ConfigConstants.GSTR2B_SUMMARY_REPORT,
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

	private Gstr2BEntityLevelSummaryReportDto convert(Gstr2BSummaryDto o) {

		Gstr2BEntityLevelSummaryReportDto dto = new Gstr2BEntityLevelSummaryReportDto();

		dto.setAvailItcCess(o.getAvailItcCess());
		dto.setAvailItcCgst(o.getAvailItcCgst());
		dto.setAvailItcIgst(o.getAvailItcIgst());
		dto.setAvailItcSgst(o.getAvailItcSgst());

		dto.setCount(o.getCount());

		dto.setPanGstinCount(o.getVendorPanCount().toString() + "/"
				+ o.getVendorGstinCount().toString());

		dto.setNonAvailItcCess(o.getNonAvailItcCess());
		dto.setNonAvailItcCgst(o.getNonAvailItcCgst());
		dto.setNonAvailItcIgst(o.getNonAvailItcIgst());
		dto.setNonAvailItcSgst(o.getNonAvailItcSgst());
		
		dto.setRejectedItcCess(o.getRejectedItcCess());
		dto.setRejectedItcCgst(o.getRejectedItcCgst());
		dto.setRejectedItcIgst(o.getRejectedItcIgst());
		dto.setRejectedItcSgst(o.getRejectedItcSgst());

		dto.setGetGSTR2BStatus(
				o.getStatus().concat(" ").concat(o.getGetGstr2bStatus()));
		dto.setTotalTaxCess(o.getTotalTaxCess());
		dto.setTotalTaxCgst(o.getTotalTaxCgst());
		dto.setTotalTaxIgst(o.getTotalTaxIgst());
		dto.setTotalTaxSgst(o.getTotalTaxSgst());
		dto.setRecipientGstin(o.getGstin());
		dto.setStatus(o.getStatus());

		return dto;
	}

}
