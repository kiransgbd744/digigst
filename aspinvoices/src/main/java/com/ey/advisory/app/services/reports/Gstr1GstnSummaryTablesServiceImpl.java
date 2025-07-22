/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.aspose.cells.Cells;
import com.aspose.cells.Color;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Service("Gstr1GstnSummaryTablesServiceImpl")
@Slf4j
// @Log4j
public class Gstr1GstnSummaryTablesServiceImpl
		implements Gstr1GstnSummaryTablesService {

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr1GstnB2CSSummaryTablesDaoImpl")
	private Gstr1GstnSummaryTablesDao gstr1GstnB2CSSummaryTablesDao;

	@Autowired
	@Qualifier("Gstr1GstnB2CSASummaryTablesDaoImpl")
	private Gstr1GstnSummaryTablesDao gstr1GstnB2CSASummaryTablesDao;

	@Autowired
	@Qualifier("Gstr1GstnATSummaryTablesDaoImpl")
	private Gstr1GstnSummaryTablesDao gstr1GstnATSummaryTablesDao;

	@Autowired
	@Qualifier("Gstr1GstnATASummaryTablesDaoImpl")
	private Gstr1GstnSummaryTablesDao gstr1GstnATASummaryTablesDao;

	@Autowired
	@Qualifier("Gstr1GstnTXPSummaryTablesDaoImpl")
	private Gstr1GstnSummaryTablesDao gstr1GstnTXPSummaryTablesDao;

	@Autowired
	@Qualifier("Gstr1GstnTXPASummaryTablesDaoImpl")
	private Gstr1GstnSummaryTablesDao gstr1GstnTXPASummaryTablesDao;

	@Autowired
	@Qualifier("Gstr1GstnNilNonExemptSummaryTablesDaoImpl")
	private Gstr1GstnSummaryTablesDao gstr1GstnNilNonExemptSummaryTablesDao;

	@Autowired
	@Qualifier("Gstr1GstnHsnSummaryTablesDaoImpl")
	private Gstr1GstnSummaryTablesDao gstr1GstnHsnSummaryTablesDao;

	@Autowired
	@Qualifier("Gstr1GstnInvSeriesTablesDaoImpl")
	private Gstr1GstnSummaryTablesDao gstr1GstnInvSeriesTablesDao;

	@Override
	public Workbook findGstSummaryTables(SearchCriteria criteria,
			PageRequest pageReq) {

		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		List<Object> responseFromViewB2CS = new ArrayList<>();
		responseFromViewB2CS = gstr1GstnB2CSSummaryTablesDao
				.getGstr1GstnSummaryTablesReport(request);

		List<Object> responseFromViewB2CSA = new ArrayList<>();
		responseFromViewB2CSA = gstr1GstnB2CSASummaryTablesDao
				.getGstr1GstnSummaryTablesReport(request);

		List<Object> responseFromViewAT = new ArrayList<>();
		responseFromViewAT = gstr1GstnATSummaryTablesDao
				.getGstr1GstnSummaryTablesReport(request);

		List<Object> responseFromViewATA = new ArrayList<>();
		responseFromViewATA = gstr1GstnATASummaryTablesDao
				.getGstr1GstnSummaryTablesReport(request);

		List<Object> responseFromViewTXP = new ArrayList<>();
		responseFromViewTXP = gstr1GstnTXPSummaryTablesDao
				.getGstr1GstnSummaryTablesReport(request);

		List<Object> responseFromViewTXPA = new ArrayList<>();
		responseFromViewTXPA = gstr1GstnTXPASummaryTablesDao
				.getGstr1GstnSummaryTablesReport(request);

		List<Object> responseFromViewNil = new ArrayList<>();
		responseFromViewNil = gstr1GstnNilNonExemptSummaryTablesDao
				.getGstr1GstnSummaryTablesReport(request);

		List<Object> responseFromViewHsn = new ArrayList<>();
		responseFromViewHsn = gstr1GstnHsnSummaryTablesDao
				.getGstr1GstnSummaryTablesReport(request);

		List<Object> responseFromViewInv = new ArrayList<>();
		responseFromViewInv = gstr1GstnHsnSummaryTablesDao
				.getGstr1GstnSummaryTablesReport(request);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"GSTR-1_GSTN_Summary_Tables_Saved_Submitted.xlsx");

		LOGGER.debug(
				"Gstr1 Asp B2CS Savable data response" + responseFromViewB2CS);

		if (responseFromViewB2CS != null && responseFromViewB2CS.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.summary.tables.b2cs.report.headers")
					.split(",");

			Worksheet tabSheet = workbook.getWorksheets().get(0);
			tabSheet.setSelected(true);
			tabSheet.setTabColor(Color.getBlue());
			
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromViewB2CS,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewB2CS.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromViewB2CSA != null && responseFromViewB2CSA.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.summary.tables.b2csa.report.headers")
					.split(",");
			
			Worksheet tabSheet = workbook.getWorksheets().get(1);
			tabSheet.setSelected(true);
			tabSheet.setTabColor(Color.getBlue());
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromViewB2CSA,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewB2CSA.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromViewAT != null && responseFromViewAT.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.summary.tables.at.report.headers")
					.split(",");

			Worksheet tabSheet = workbook.getWorksheets().get(2);
			tabSheet.setSelected(true);
			tabSheet.setTabColor(Color.getBlue());
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromViewAT,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewAT.size(), true, "yyyy-mm-dd", false);
		}
		if (responseFromViewATA != null && responseFromViewATA.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.summary.tables.ata.report.headers")
					.split(",");

			Worksheet tabSheet = workbook.getWorksheets().get(3);
			tabSheet.setSelected(true);
			tabSheet.setTabColor(Color.getBlue());
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromViewATA,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewATA.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromViewTXP != null && responseFromViewTXP.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.summary.tables.txp.report.headers")
					.split(",");

			Worksheet tabSheet = workbook.getWorksheets().get(4);
			tabSheet.setSelected(true);
			tabSheet.setTabColor(Color.getBlue());
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromViewTXP,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewTXP.size(), true, "yyyy-mm-dd", false);
		}
		if (responseFromViewTXPA != null && responseFromViewTXPA.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.summary.tables.txpa.report.headers")
					.split(",");

			Worksheet tabSheet = workbook.getWorksheets().get(5);
			tabSheet.setSelected(true);
			tabSheet.setTabColor(Color.getBlue());
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromViewTXPA,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewTXPA.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromViewNil != null && responseFromViewNil.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.summary.tables.nil.report.headers")
					.split(",");
			Worksheet tabSheet = workbook.getWorksheets().get(6);
			tabSheet.setSelected(true);
			tabSheet.setTabColor(Color.getBlue());
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromViewNil,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewNil.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromViewHsn != null && responseFromViewHsn.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.summary.tables.hsn.report.headers")
					.split(",");
			Worksheet tabSheet = workbook.getWorksheets().get(7);
			tabSheet.setSelected(true);
			tabSheet.setTabColor(Color.getBlue());
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromViewHsn,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewHsn.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromViewInv != null && responseFromViewInv.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.summary.tables.inv.report.headers")
					.split(",");

			Worksheet tabSheet = workbook.getWorksheets().get(8);
			tabSheet.setSelected(true);
			tabSheet.setTabColor(Color.getBlue());
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromViewInv,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewInv.size(), true, "yyyy-mm-dd", false);
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
