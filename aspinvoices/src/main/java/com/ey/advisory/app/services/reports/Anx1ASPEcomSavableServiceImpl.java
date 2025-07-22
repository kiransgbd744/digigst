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

@Service("Anx1ASPEcomSavableServiceImpl")
public class Anx1ASPEcomSavableServiceImpl
		implements Anx1ASPEcomSavableService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ASPEcomSavableServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Anx1ASPEcomTotalSummaryDaoImpl")
	private Anx1ASPEcomSavableDao anx1ASPEcomTotSummaryDao;

	@Autowired
	@Qualifier("Anx1ASPEcomSummaryLevelDaoImpl")
	private Anx1ASPEcomSavableDao anx1ASPEcomSummaryLevelDao;

	@Autowired
	@Qualifier("Anx1ASPEcomTransDaoImpl")
	private Anx1ASPEcomSavableDao anx1ASPEcomSavableTransDao;

	@Autowired
	@Qualifier("Anx1ASPEcomB2cSumLevelDaoImpl")
	private Anx1ASPEcomSavableDao anx1ASPEcomB2cSumSavableDao;

	@Override
	public Workbook findAnx1EcomSavableReports(SearchCriteria criteria,
			PageRequest pageReq) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Object> responseFromViewTotal = new ArrayList<>();
		responseFromViewTotal = anx1ASPEcomTotSummaryDao
				.getAnx1EcomSavableReports(request);

		List<Object> responseFromViewSum = new ArrayList<>();
		responseFromViewSum = anx1ASPEcomSummaryLevelDao
				.getAnx1EcomSavableReports(request);

		List<Object> responseFromViewTrans = new ArrayList<>();
		responseFromViewTrans = anx1ASPEcomSavableTransDao
				.getAnx1EcomSavableReports(request);

		List<Object> responseFromViewB2c = new ArrayList<>();
		responseFromViewB2c = anx1ASPEcomB2cSumSavableDao
				.getAnx1EcomSavableReports(request);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"ANX-1_ASP_ECOM_Table4(Savable).xlsx");

		LOGGER.debug(
				"ANX1 Asp B2C Savable data response" + responseFromViewTotal);

		if (responseFromViewTotal != null && responseFromViewTotal.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.Savable.ecom.totalsummary.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromViewTotal,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewTotal.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromViewSum != null && responseFromViewSum.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.Savable.ecom.sumlevel.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(1).getCells();
			errorDumpCells.importCustomObjects(responseFromViewSum,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewSum.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromViewTrans != null && responseFromViewTrans.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.Savable.ecom.trans.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(2).getCells();
			errorDumpCells.importCustomObjects(responseFromViewTrans,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewTrans.size(), true, "yyyy-mm-dd", false);
		}
		if (responseFromViewB2c != null && responseFromViewB2c.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.Savable.ecom.b2c.sumlevel.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(3).getCells();
			errorDumpCells.importCustomObjects(responseFromViewB2c,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewB2c.size(), true, "yyyy-mm-dd", false);
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
