
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

@Service("Gstr1ASPB2CSSavableServiceImpl")
public class Gstr1ASPB2CSSavableServiceImpl
		implements Gstr1ASPB2CSSavableService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1ASPB2CSSavableServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr1ASPB2CSSavableTotalSummaryDaoImpl")
	private Gstr1ASPB2CSSavableTotalDao gstr1ASPB2CSSavableTotalDao;

	@Autowired
	@Qualifier("Gstr1B2CSSavableTransactionalDaoImpl")
	private Gstr1ASPB2CSSavableTransactionalDao gstr1ASPB2CSSavableTransactionalDao;

	@Autowired
	@Qualifier("Gstr1ASPB2CSSavableSummaryLevelDaoImpl")
	private Gstr1ASPB2CSSavableSummaryLevelDao gstr1ASPB2CSSavableSummaryLevelDao;

	@Override
	public Workbook findGstr1B2CSSavableReports(SearchCriteria criteria,
			PageRequest pageReq) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 2;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Object> responseFromViewTotal = new ArrayList<>();
		responseFromViewTotal = gstr1ASPB2CSSavableTotalDao
				.getGstr1B2CSSavableReports(request);

		List<Object> responseFromViewTrans = new ArrayList<>();
		responseFromViewTrans = gstr1ASPB2CSSavableTransactionalDao
				.getGstr1B2CSSavableReports(request);

		List<Object> responseFromViewSummary = new ArrayList<>();
		responseFromViewSummary = gstr1ASPB2CSSavableSummaryLevelDao
				.getGstr1B2CSSavableReports(request);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"GSTR-1_ASP_B2CS_Savable.xlsx");

		LOGGER.debug(
				"Gstr1 Asp B2CS Savable data response" + responseFromViewTotal);

		if (responseFromViewTotal != null && responseFromViewTotal.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.Savable.b2cS.totalsummary.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromViewTotal,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewTotal.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromViewTrans != null && responseFromViewTrans.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.api.new.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(1).getCells();
			errorDumpCells.importCustomObjects(responseFromViewTrans,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewTrans.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromViewSummary != null
				&& responseFromViewSummary.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.Savable.b2cS.summarylevel.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(2).getCells();
			errorDumpCells.importCustomObjects(responseFromViewSummary,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewSummary.size(), true, "yyyy-mm-dd", false);
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
