package com.ey.advisory.app.data.daos.client.gstr2;

import java.net.URL;
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
import com.ey.advisory.app.services.jobs.gstr6.DistributionErrorReportDaoImpl;
import com.ey.advisory.app.services.jobs.gstr6.Gstr6ConsolidatedAspErrorReportsDaoImpl;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;
import com.ey.advisory.core.search.PageRequest;

@Service("Gstr6ConsolidatedAspErrorReportsServiceImpl")
public class Gstr6ConsolidatedAspErrorReportsServiceImpl {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6ConsolidatedAspErrorReportsServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr6ConsolidatedAspErrorReportsDaoImpl")
	private Gstr6ConsolidatedAspErrorReportsDaoImpl gstr6AspErrorReportsDaoImpl;
	
	@Autowired
	@Qualifier("DistributionErrorReportDaoImpl")
	private DistributionErrorReportDaoImpl distributionErrorReportsDaoImpl;

	public Workbook findError(Gstr6SummaryRequestDto setDataSecurity,
			PageRequest pageReq) {
		Gstr6SummaryRequestDto request = (Gstr6SummaryRequestDto) setDataSecurity;
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		
		List<Object> responseFromView = new ArrayList<>();
		responseFromView = gstr6AspErrorReportsDaoImpl.getErrorReports(request);
		
		List<Object> responseFromdist = new ArrayList<>();
		responseFromdist = distributionErrorReportsDaoImpl.getErrorReports(request);
		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"GSTR6_APIErrorReport.xlsx");

		if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.inward.api.error.report.column.mapping").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromView, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromView.size(), true, "yyyy-mm-dd", false);
		}
		
		if (responseFromdist != null && responseFromdist.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("dist.asp.csv.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(1).getCells();
			errorDumpCells.importCustomObjects(responseFromdist, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromdist.size(), true, "yyyy-mm-dd", false);
		}
		return workbook;

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
			workbook = new Workbook(templatePath, options);
			workbook.getSettings()
					.setMemorySetting(MemorySetting.MEMORY_PREFERENCE);
		} catch (Exception ex) {
			LOGGER.error("Exception in creating workbook : ", ex);
		}
		return workbook;
	}

}
