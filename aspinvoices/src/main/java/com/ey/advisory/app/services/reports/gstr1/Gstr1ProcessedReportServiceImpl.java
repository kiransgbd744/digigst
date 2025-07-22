package com.ey.advisory.app.services.reports.gstr1;

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
import com.ey.advisory.app.data.daos.client.Gstr1ReportsDao;
import com.ey.advisory.app.data.views.client.Gstr1ErrorReportView;
import com.ey.advisory.app.data.views.client.Gstr1InformationReportView;
import com.ey.advisory.app.data.views.client.Gstr1ProcessedReportView;
import com.ey.advisory.app.data.views.client.Gstr1TotRecReportView;
import com.ey.advisory.app.docs.dto.ReportSearchReqDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * This class is responsible for searching saved and submitted documents to GSTN
 * 
 * @author Mohana.Dasari
 *
 */
@Service("Gstr1ProcessedReportServiceImpl")
public class Gstr1ProcessedReportServiceImpl implements Gstr1ProcessedReportsService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1ProcessedReportServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr1ReportsDaoImpl")
	private Gstr1ReportsDao gstr1ReportsDao;

	@Override
	public Workbook find(SearchCriteria criteria, PageRequest pageReq) {
		ReportSearchReqDto request = (ReportSearchReqDto) criteria;
		Workbook workbook = null;
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		List<Gstr1ProcessedReportView> responseFromView = new ArrayList<>();
		responseFromView = gstr1ReportsDao.getProcessedReports(request);

		if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.processed.report.headers").split(",");
			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"ASP_ProcessedRecords.xlsx");
			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromView, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromView.size(), true, "yyyy-mm-dd", false);
			return workbook;
		}
		return null;

	}
	
	@Override
	public Workbook findError(SearchCriteria criteria, PageRequest pageReq) {
		ReportSearchReqDto request = (ReportSearchReqDto) criteria;
		Workbook workbook = null;
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		List<Gstr1ErrorReportView> responseFromView = new ArrayList<>();
		responseFromView = gstr1ReportsDao.getErrorReports(request);

		if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.error.report.headers").split(",");
			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"ASP_ErrorRecords.xlsx");
			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromView, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromView.size(), true, "yyyy-mm-dd", false);
			return workbook;
		}
		return null;

	}
	
	@Override
	public Workbook findTotRec(SearchCriteria criteria, PageRequest pageReq) {
		ReportSearchReqDto request = (ReportSearchReqDto) criteria;
		Workbook workbook = null;
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		List<Gstr1TotRecReportView> responseFromView = new ArrayList<>();
		responseFromView = gstr1ReportsDao.getTotRecReports(request);

		if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.totrec.report.headers").split(",");
			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"ASP_TotRecRecords.xlsx");
			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromView, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromView.size(), true, "yyyy-mm-dd", false);
			return workbook;
		}
		return null;

	}
	@Override
	public Workbook findInfoRec(SearchCriteria criteria, PageRequest pageReq) {
		// TODO Auto-generated method stub
		ReportSearchReqDto request = (ReportSearchReqDto) criteria;
		Workbook workbook = null;
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		List<Gstr1InformationReportView> responseFromView = new ArrayList<>();
		responseFromView = gstr1ReportsDao.getInfoReports(request);

		if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.info.report.headers").split(",");
			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"ASP_InfoRecords.xlsx");
			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromView, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromView.size(), true, "yyyy-mm-dd", false);
			return workbook;
		}
		return null;
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
