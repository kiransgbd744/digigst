package com.ey.advisory.app.services.reports;

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
import com.aspose.cells.Worksheet;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6AProcessedDataRequestDto;
import com.ey.advisory.common.CommonUtility;

@Service("Gstr6aDownloadReportServiceImpl")
public class Gstr6aDownloadReportServiceImpl
		implements Gstr6aDownloadReportService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6ProcessedRecordsScreenServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr6aReportDownlaodDaoImpl")
	private Gstr6aReportDownlaodDaoImpl gstr6aReportDownlaodDaoImpl;

	@Override
	public Workbook findGstr6aDownloadReport(
			Gstr6AProcessedDataRequestDto criteria) {
		Gstr6AProcessedDataRequestDto request = (Gstr6AProcessedDataRequestDto) criteria;
		Workbook workbook = null;
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		List<Object> responseFromView = new ArrayList<>();
		responseFromView = gstr6aReportDownlaodDaoImpl
				.getgstr6aReportDownlaodDaoImpl(request);

		if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("Gstr6a reports download " + responseFromView);
		}
		if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr6a.api.report.download.headers").split(",");
			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"GSTR-6A_DownloadReport.xlsx");
			Worksheet tabSheet = workbook.getWorksheets().get(0);
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromView, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromView.size(), true, "yyyy-mm-dd", false);
		}

		return workbook;
	}

	@Override
	public Workbook findGstr6aDownloadReportDashboard(
			Gstr6AProcessedDataRequestDto criteria) {
		Gstr6AProcessedDataRequestDto request = (Gstr6AProcessedDataRequestDto) criteria;
		Workbook workbook = null;
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		List<Object> responseFromView = new ArrayList<>();
		responseFromView = gstr6aReportDownlaodDaoImpl
				.getgstr6aReportDownlaodDashboardDaoImpl(request);

		if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("Gstr6a reports download " + responseFromView);
		}
		if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr6a.api.report.download.headers").split(",");
			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"GSTR-6A_DownloadReport.xlsx");
			Worksheet tabSheet = workbook.getWorksheets().get(0);
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromView, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromView.size(), true, "yyyy-mm-dd", false);
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
