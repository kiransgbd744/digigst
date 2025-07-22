package com.ey.advisory.app.data.services.gstr9;

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
import com.ey.advisory.app.services.reports.Gstr7VerticalDao;
import com.ey.advisory.app.services.reports.Gstr7VerticalReportsService;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Gstr9HsnVerticalTotalReportsServiceImpl")
public class Gstr9HsnVerticalTotalReportsServiceImpl implements Gstr7VerticalReportsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(Gstr9HsnVerticalTotalReportsServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr9HsnVerticalTotalReportsDaoImpl")
	private Gstr7VerticalDao gstr9VerticalDao;

	@Override
	public Workbook downloadReports(SearchCriteria criteria, PageRequest pageReq) {
		Gstr1VerticalDownloadReportsReqDto request = (Gstr1VerticalDownloadReportsReqDto) criteria;

		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Object> responseFromView = new ArrayList<>();
		responseFromView = gstr9VerticalDao.getGstr7VerticalReports(request);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"GSTR9_Total_HSN_Records.xlsx");
        if(LOGGER.isDebugEnabled()){
		LOGGER.debug("gstr7 hsn data response" + responseFromView);
        }

		if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility.getProp("gstr9.asp.total.hsn.report.headers").split(",");

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



