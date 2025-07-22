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
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

@Service("Gstr6ProcessedServiceImpl")
public class Gstr6ProcessedServiceImpl implements Gstr6ProcessedReportsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(Gstr6ProcessedServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	/*@Autowired
	@Qualifier("Gstr6ProcessedReportDaoImpl")
	private Gstr6ProcessedDao gstr6ProcessedDao;*/
	
	@Autowired
	@Qualifier("DistributionProcessedReportDaoImpl")
    private Gstr6ProcessedDao gstr6distDao;

	@Override
	public Workbook findGstr6Processed(SearchCriteria criteria, PageRequest pageReq) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		/*List<Object> responseFromView = new ArrayList<>();
		responseFromView = gstr6ProcessedDao.getGstr6Reports(request);*/
		
		List<Object> responseFromdist = new ArrayList<>();
		responseFromdist = gstr6distDao.getGstr6Reports(request);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"PR Summary_Processed Records (Current Period).xlsx");

		LOGGER.debug("gstr6 PR Summary data response" + responseFromdist);

		/*if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility.getProp("anx2.prsummary.processed.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromView, invoiceHeaders, isHeaderRequired, startRow,
					startcolumn, responseFromView.size(), true, "yyyy-mm-dd", false);
		}*/
		if (responseFromdist != null && responseFromdist.size() > 0) {
			String[] invoiceHeaders = commonUtility.getProp("dist.processed.excel.report.headers.mapping").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromdist, invoiceHeaders, isHeaderRequired, startRow,
					startcolumn, responseFromdist.size(), true, "yyyy-mm-dd", false);
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
