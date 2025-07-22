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
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.views.client.Anx1ProcesssedInfo;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

@Service("Anx1ProcessedInfoRecordsServiceImpl")
public class Anx1ProcessedInfoRecordsServiceImpl
		implements Anx1ProcessedInfoReportsService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ProcessedInfoRecordsServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Anx1ProcessedInfoReportsDaoImpl")
	private Anx1ProcessedInfoReportsDao anx1ProcessedInfoReportsDao;
	
	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Override
	public Workbook findProcessedInfoRec(SearchCriteria criteria,
			PageRequest pageReq) {
		
		Anx1FileStatusReportsReqDto request = (Anx1FileStatusReportsReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		List<Anx1ProcesssedInfo> responseFromView = new ArrayList<>();
		responseFromView = anx1ProcessedInfoReportsDao
				.getProcessedInfoReports(request);
		LOGGER.debug("responseFromView"+responseFromView);

		if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.processed.info.report.headers").split(",");
			LOGGER.debug("workbook properties reading starting ");
			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"ANX1_ProcessedInfoReport.xlsx");
			LOGGER.debug("workbook reading ending");
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
