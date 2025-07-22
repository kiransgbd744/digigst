package com.ey.advisory.app.services.reports;

import java.net.URL;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.views.client.Anx1DataStatusSummaryReportView;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

@Service("Anx1DataStatusSummaryRecordsServiceImpl")
public class Anx1DataStatusSummaryRecordsServiceImpl
		implements Anx1DataStatusSummaryReportsService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1DataStatusSummaryRecordsServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Anx1DataStatusSummaryReportsDaoImpl")
	private Anx1DataStatusSummaryReportsDao anx1DataStatusSummaryReportsDao;

	@Override
	public Workbook findDataStatusSummaryRec(SearchCriteria criteria,
			PageRequest pageReq) {
		Anx1ReportSearchReqDto request = (Anx1ReportSearchReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		List<Anx1DataStatusSummaryReportView> responseFromView = new ArrayList<>();
		responseFromView = anx1DataStatusSummaryReportsDao
				.getDataStatusSummaryReports(request);

		if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.datastatus.summary.report.headers")
					.split(",");
			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"ANX1_DataStatusSummaryReport.xlsx");
			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromView, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromView.size(), true, "yyyy-mm-dd", false);
		}
		return workbook;

	}

	public static String convertTaxPeriodToString(Integer taxPeriod) {
		String dateStr = taxPeriod.toString();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
		YearMonth ym = YearMonth.parse(dateStr, formatter);
		int year = ym.getYear();
		int monthValue = ym.getMonthValue();
		StringBuffer monthYear = new StringBuffer();
		String monthValueOf = String.valueOf(monthValue);
		String yearValueOf = String.valueOf(year);
		if (monthValue < 9) {
			monthYear.append("0");
			monthYear.append(monthValueOf);
			monthYear.append(yearValueOf);
		} else {
			monthYear.append(monthValueOf);
			monthYear.append(yearValueOf);
		}
		return monthYear.toString();
	}

	private Workbook createWorkbookWithExcelTemplate(String folderName,
			String fileName) {
		Workbook workbook = null;
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			URL template_Dir = classLoader.getResource(folderName + "/");
			String templatePath = template_Dir.getPath() + fileName;
			LoadOptions options = new LoadOptions(FileFormatType.XLSX);
			CommonUtility.setAsposeLicense();
			workbook = new Workbook(templatePath,options);
			workbook.getSettings()
					.setMemorySetting(MemorySetting.MEMORY_PREFERENCE);
		} catch (Exception ex) {
			LOGGER.error("Exception in creating workbook : ", ex);
		}
		return workbook;
	}

}
