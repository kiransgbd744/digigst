package com.ey.advisory.app.gmr;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.FindOptions;
import com.aspose.cells.LookAtType;
import com.aspose.cells.LookInType;
import com.aspose.cells.Style;
import com.aspose.cells.StyleFlag;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Ravindra V S
 *
 */

@Service("GMRInwardSummaryDownloadServiceImpl")
@Slf4j
public class GMRInwardSummaryDownloadServiceImpl
		implements GMRInwardSummaryDownloadService {

	@Autowired
	@Qualifier("GMRInwardSummaryDownloadDaoImpl")
	GMRInwardSummaryDownloadDao gmrInwardSummarydao;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	EntityInfoRepository entityInfo;

	List<String> boldList = Arrays.asList("Regular inward supplies",
			"Cross charge",
			"Inward supplies under RCM",
			"Ineligible Inward supplies (Supplies on which ITC is blocked/ ineligible)",
			"Non GST inward supplies (i.e. GST levied @ Nil rate)", "ITC reversals",
			"Total Input Tax claimed (B.1 + B.2 + B.3 + B.4 + B.5 + B.6 - B.9)",
			"Total Input Tax Credit as per GSTR2A", "Balance of the Input Credit (Accumulated)");

	@Override
	public Workbook find(GmrInwardSummaryDto req) {

		Workbook workbook = null;

		Integer valueDefault = 999999;
		Integer fromTaxPeriod = req.getFromTaxPeriod() != null
				? derivedTaxPeriod(req.getFromTaxPeriod()) : valueDefault;

		Integer toTaxPeriod = req.getToTaxPeriod() != null
				? derivedTaxPeriod(req.getToTaxPeriod()) : valueDefault;
		List<String> gstinList = req.getSgstins();

		List<GmrInwardSummaryDownloadDto> response = gmrInwardSummarydao
				.find(gstinList, fromTaxPeriod, toTaxPeriod);

		if (response == null) {
			return null;
		}
		workbook = writeToExcel(response, gstinList, req);

		return workbook;

	}

	private Workbook writeToExcel(
			List<GmrInwardSummaryDownloadDto> gmrDataResponse,
			List<String> gstinList, GmrInwardSummaryDto req) {

		Workbook workbook = null;
		int startRow = 10;
		int startcolumn = 1;
		boolean isHeaderRequired = false;

		if (gmrDataResponse != null && !gmrDataResponse.isEmpty()) {

			String[] invoiceHeaders = commonUtility
					.getProp("gmr.inward.summary.report.header").split(",");

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates", "GMRInwardReport.xlsx");

			if (LOGGER.isDebugEnabled()) {
				String msg = "find.writeToExcel "
						+ "workbook created writing data to the workbook";
				LOGGER.debug(msg);
			}
			Cells reportCells = workbook.getWorksheets().get(0).getCells();
			Worksheet sheet = workbook.getWorksheets().get(0);

			GmrInwardEntityAndMonthDto obj = entityAndTaxPeriod(req);

			String taxPeriod = obj.getFromMonth() + " "
					+ req.getFromTaxPeriod().toString().substring(4, 6) + " to "
					+ obj.getToMonth() + " "
					+ req.getToTaxPeriod().toString().substring(4, 6);

			Cell cell1 = sheet.getCells().get("C4");
			Cell cell2 = sheet.getCells().get("C5");
			Cell cell3 = sheet.getCells().get("C6");
			Cell cell4 = sheet.getCells().get("C7");

			cell1.setValue(obj.getEntityName());
			cell2.setValue(String.join(",", gstinList));
			cell3.setValue(taxPeriod);
			cell4.setValue(dateChange());

			reportCells.importCustomObjects(gmrDataResponse, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					gmrDataResponse.size(), true, "yyyy-mm-dd", false);
			sheet.setGridlinesVisible(false);
			FindOptions opts = new FindOptions();
			opts.setLookInType(LookInType.VALUES);
			opts.setLookAtType(LookAtType.ENTIRE_CONTENT);

			Cell cell = null;
			for (String s : boldList) {
				cell = sheet.getCells().find(s, cell, opts);
				Style rowStyle = sheet.getCells().getRows().get(cell.getRow())
						.getStyle();
				rowStyle.getFont().setBold(true);
				StyleFlag rowStyleFlag = new StyleFlag();
				rowStyleFlag.setFontBold(true);
				sheet.getCells().getRows().get(cell.getRow())
						.applyStyle(rowStyle, rowStyleFlag);
			}

			try {
				if (LOGGER.isDebugEnabled()) {
					String msg = "find.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}
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

	@Override
	public GmrInwardEntityAndMonthDto entityAndTaxPeriod(
			GmrInwardSummaryDto criteria) {
				
		GmrInwardEntityAndMonthDto obj = new GmrInwardEntityAndMonthDto();

		String entityName = entityInfo
				.findEntityNameByEntityId(criteria.getEntityId().get(0));

		obj.setEntityName(entityName);
		obj.setFromMonth(getMonth(
				criteria.getFromTaxPeriod().toString().substring(0, 2)));
		obj.setToMonth(
				getMonth(criteria.getToTaxPeriod().toString().substring(0, 2)));

		return obj;

	}

	private String dateChange() {
		DateTimeFormatter formatter = null;
		String dateTime = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now())
				.toString();
		char ch = 'T';
		dateTime = dateTime.substring(0, 10) + ch + dateTime.substring(10 + 1);
		String s1 = dateTime.substring(0, 19);
		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

		LocalDateTime dateTimes = LocalDateTime.parse(s1, formatter);

		LocalDateTime dateTimeFormatter = EYDateUtil
				.toISTDateTimeFromUTC(dateTimes);
		DateTimeFormatter FOMATTER = DateTimeFormatter
				.ofPattern("dd-MM-yyyy HH:mm:ss");
		String newdate = FOMATTER.format(dateTimeFormatter);
		return newdate;

	}

	private int derivedTaxPeriod(String taxPeriod) {
		int derivedTax = 0;
		derivedTax = Integer.parseInt(
				taxPeriod.substring(2, 6) + taxPeriod.substring(0, 2));
		return derivedTax;
	}

	private String getMonth(String taxPeriod) {
		int month = Integer.parseInt(taxPeriod);
		String monthString;
		switch (month) {
		case 1:
			monthString = "Jan";
			break;
		case 2:
			monthString = "Feb";
			break;
		case 3:
			monthString = "Mar";
			break;
		case 4:
			monthString = "Apr";
			break;
		case 5:
			monthString = "May";
			break;
		case 6:
			monthString = "Jun";
			break;
		case 7:
			monthString = "Jul";
			break;
		case 8:
			monthString = "Aug";
			break;
		case 9:
			monthString = "Sep";
			break;
		case 10:
			monthString = "Oct";
			break;
		case 11:
			monthString = "Nov";
			break;
		case 12:
			monthString = "Dec";
			break;
		default:
			monthString = "Invalid month";
			break;
		}
		return monthString;
	}

}
