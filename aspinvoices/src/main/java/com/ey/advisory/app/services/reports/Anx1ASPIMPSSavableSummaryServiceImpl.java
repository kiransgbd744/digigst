/**
 * 
 */
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

/**
 * @author Sujith.Nanga
 *
 * 
 */
@Service("Anx1ASPIMPSSavableSummaryServiceImpl")
public class Anx1ASPIMPSSavableSummaryServiceImpl
		implements Anx1ASPIMPSSavableService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ASPIMPSSavableSummaryServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Anx1ASPIMPSSavableTotalSummaryDaoImpl")
	private Anx1ASPIMPSSavableSummaryDao gstr1ASPImpsSavableTotalDao;

	@Autowired
	@Qualifier("Anx1ASPIMPSSavableSummaryLevelDaoImpl")
	private Anx1ASPIMPSSavableSummaryDao gstr1ASPImpsSavableSummaryDao;

	@Autowired
	@Qualifier("Anx1ASPIMPSSavableTransDaoImpl")
	private Anx1ASPIMPSSavableSummaryDao gstr1ASPImpsSavableSummaryLevelDao;

	@Override
	public Workbook findAnx1IMPSSavableReports(SearchCriteria criteria,
			PageRequest pageReq) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Object> responseFromViewTotal = new ArrayList<>();
		responseFromViewTotal = gstr1ASPImpsSavableTotalDao
				.getAnx1IMPSSavableReports(request);

		List<Object> responseFromViewSum = new ArrayList<>();
		responseFromViewSum = gstr1ASPImpsSavableSummaryDao
				.getAnx1IMPSSavableReports(request);

		List<Object> responseFromViewTrans = new ArrayList<>();
		responseFromViewTrans = gstr1ASPImpsSavableSummaryLevelDao
				.getAnx1IMPSSavableReports(request);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"ANX-1_ASP_Import of Services 3I Savable.xlsx");

		LOGGER.debug("ANX1 Asp Import of Services Savable data response"
				+ responseFromViewTotal);

		if (responseFromViewTotal != null && responseFromViewTotal.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.Savable.imps.totalsummary.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromViewTotal,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewTotal.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromViewSum != null && responseFromViewSum.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.Savable.imps.sumlevel.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(1).getCells();
			errorDumpCells.importCustomObjects(responseFromViewSum,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewSum.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromViewTrans != null && responseFromViewTrans.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.Savable.imps.trans.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(2).getCells();
			errorDumpCells.importCustomObjects(responseFromViewTrans,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewTrans.size(), true, "yyyy-mm-dd", false);
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
