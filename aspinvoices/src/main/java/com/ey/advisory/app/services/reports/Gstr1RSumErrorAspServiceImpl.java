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
 * @author Laxmi.Salukuti
 *
 */
@Service("Gstr1RSumErrorAspServiceImpl")
public class Gstr1RSumErrorAspServiceImpl implements Gstr1ReviewSummReportsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(Gstr1RSumErrorAspServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr1RSummErrorAspReportsDaoImpl")
	private Gstr1ReviewSummaryReportsDao gstr1ReviewSummaryReportsDao;

	@Autowired
	@Qualifier("Gstr1RSummErrorB2CSReportsDaoImpl")
	private Gstr1ReviewSummaryReportsDao gstr1B2csReportsDao;

	@Autowired
	@Qualifier("Gstr1RSummErrorAdvanceRecReportsDaoImpl")
	private Gstr1ReviewSummaryReportsDao gstr1AdvanceRecDao;

	@Autowired
	@Qualifier("Gstr1RSummErrorAdvanceAdjReportsDaoImpl")
	private Gstr1ReviewSummaryReportsDao gstr1AdvanceAdjDao;

	@Autowired
	@Qualifier("Gstr1RSummErrorInvoiceReportsDaoImpl")
	private Gstr1ReviewSummaryReportsDao gstr1InvDao;

	@Override
	public Workbook findGstr1ReviewSummRecords(SearchCriteria criteria, PageRequest pageReq) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Object> responseFromView = new ArrayList<>();
		responseFromView = gstr1ReviewSummaryReportsDao.getGstr1RSReports(request);

		List<Object> responseFromViewB2cs = new ArrayList<>();
		responseFromViewB2cs = gstr1B2csReportsDao.getGstr1RSReports(request);

		List<Object> responseFromViewAr = new ArrayList<>();
		responseFromViewAr = gstr1AdvanceRecDao.getGstr1RSReports(request);

		List<Object> responseFromViewAdj = new ArrayList<>();
		responseFromViewAdj = gstr1AdvanceAdjDao.getGstr1RSReports(request);

		List<Object> responseFromViewInv = new ArrayList<>();
		responseFromViewInv = gstr1InvDao.getGstr1RSReports(request);
		
		workbook = createWorkbookWithExcelTemplate("ReportTemplates", "Gstr1_Asp_Error_ReviewSummaryReport.xlsx");

		LOGGER.debug("Gstr1 error ASP data response" + responseFromView);

		if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility.getProp("anx1.api.new.report.headers").split(",");
			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromView, invoiceHeaders, isHeaderRequired, startRow,
					startcolumn, responseFromView.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromViewB2cs != null && responseFromViewB2cs.size() > 0) {
			String[] invoiceHeaders = commonUtility.getProp("gstr1.vertical.b2cS.error.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(1).getCells();
			errorDumpCells.importCustomObjects(responseFromViewB2cs, invoiceHeaders, isHeaderRequired, startRow,
					startcolumn, responseFromViewB2cs.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromViewAr != null && responseFromViewAr.size() > 0) {
			String[] invoiceHeaders = commonUtility.getProp("gstr1.vertical.advrec.error.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(2).getCells();
			errorDumpCells.importCustomObjects(responseFromViewAr, invoiceHeaders, isHeaderRequired, startRow,
					startcolumn, responseFromViewAr.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromViewAdj != null && responseFromViewAdj.size() > 0) {
			String[] invoiceHeaders = commonUtility.getProp("gstr1.vertical.advadj.error.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(3).getCells();
			errorDumpCells.importCustomObjects(responseFromViewAdj, invoiceHeaders, isHeaderRequired, startRow,
					startcolumn, responseFromViewAdj.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromViewInv != null && responseFromViewInv.size() > 0) {
			String[] invoiceHeaders = commonUtility.getProp("gstr1.vertical.invoice.error.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(4).getCells();
			errorDumpCells.importCustomObjects(responseFromViewInv, invoiceHeaders, isHeaderRequired, startRow,
					startcolumn, responseFromViewInv.size(), true, "yyyy-mm-dd", false);
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