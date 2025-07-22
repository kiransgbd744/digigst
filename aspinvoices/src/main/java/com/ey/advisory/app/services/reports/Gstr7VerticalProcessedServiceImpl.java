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
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

@Service("Gstr7VerticalProcessedServiceImpl")
public class Gstr7VerticalProcessedServiceImpl implements Gstr7VerticalReportsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(Gstr7VerticalProcessedServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr7VerticalProcessedReportsDaoImpl")
	private Gstr7VerticalDao gstr7VerticalDao;

	@Override
	public Workbook downloadReports(SearchCriteria criteria, PageRequest pageReq) {
		Gstr1VerticalDownloadReportsReqDto request = (Gstr1VerticalDownloadReportsReqDto) criteria;

		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Object> responseFromView = new ArrayList<>();
		responseFromView = gstr7VerticalDao.getGstr7VerticalReports(request);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates", "Gstr7_Processed_Records.xlsx");

		LOGGER.debug("gstr6 PR Summary data response" + responseFromView);

		if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility.getProp("gstr7.asp.process.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromView, invoiceHeaders, isHeaderRequired, startRow,
					startcolumn, responseFromView.size(), true, "yyyy-mm-dd", false);
		}

		return workbook;
	}

	private Workbook createWorkbookWithExcelTemplate(String folderName, String fileName) {
		Workbook workbook = null;
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			URL template_Dir = classLoader.getResource(folderName + "/");
			String templatePath = template_Dir.getPath() + fileName;
			LoadOptions options = new LoadOptions(FileFormatType.XLSX);
			CommonUtility.setAsposeLicense();
			workbook = new Workbook(templatePath, options);
			workbook.getSettings().setMemorySetting(MemorySetting.MEMORY_PREFERENCE);
		} catch (Exception ex) {
			LOGGER.error("Exception in creating workbook : ", ex);
		}
		return workbook;
	}
}
