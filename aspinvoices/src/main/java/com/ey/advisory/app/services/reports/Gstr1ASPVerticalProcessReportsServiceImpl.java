
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
@Service("Gstr1ASPVerticalProcessReportsServiceImpl")
public class Gstr1ASPVerticalProcessReportsServiceImpl
		implements Gstr1ASPVerticalProcessReportsService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1ASPVerticalProcessReportsServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr1OutwardVerticalProcessedB2CSDaoImpl")
	Gstr1OutwardVerticalProcessedB2CSDao gstr1OutwardVerticalProcessedB2CSDao;

	@Autowired
	@Qualifier("Gstr1OutwardVerticalProcessedAdvanceReceivedDaoImpl")
	Gstr1OutwardVerticalProcessedAdvanceReceivedDao gstr1OutwardVerticalProcessedAdvanceReceivedDao;
	@Autowired
	@Qualifier("Gstr1OutwardVerticalProcessAdvAdjDaoImpl")
	Gstr1OutwardVerticalProcessAdvAdjDao gstr1OutwardVerticalProcessAdvAdjDao;

	@Autowired
	@Qualifier("Gstr1OutwardVerticalProcessInvDaoImpl")
	private Gstr1OutwardVerticalProcessInvDao gstr1OutwardVerticalProcessInvDao;

	/*@Autowired
	@Qualifier("Gstr1OutwardVerticalProcessNilDaoImpl")
	private Gstr1OutwardVerticalProcessNilDao gstr1OutwardVerticalProcessNilDao;*/

	/*@Autowired
	@Qualifier("Gstr1OutwardVerticalProcessHSNDaoImpl")
	private Gstr1OutwardVerticalProcessHSNDao gstr1OutwardVerticalProcessHSNDao;*/

	@Override
	public Workbook findReports(SearchCriteria criteria, PageRequest pageReq) {

		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		List<Object> responseFromViewB2CS = new ArrayList<>();
		responseFromViewB2CS = gstr1OutwardVerticalProcessedB2CSDao
				.getGstr1RSReports(request);

		List<Object> responseFromViewAR = new ArrayList<>();
		responseFromViewAR = gstr1OutwardVerticalProcessedAdvanceReceivedDao
				.getGstr1RSReports(request);

		List<Object> responseFromViewAA = new ArrayList<>();
		responseFromViewAA = gstr1OutwardVerticalProcessAdvAdjDao
				.getGstr1RSReports(request);

		/*List<Object> responseFromViewNil = new ArrayList<>();
		responseFromViewNil = gstr1OutwardVerticalProcessNilDao
				.getGstr1RSReports(request);*/
/*
		List<Object> responseFromViewHsn = new ArrayList<>();
		responseFromViewHsn = gstr1OutwardVerticalProcessHSNDao
				.getGstr1RSReports(request);*/

		List<Object> responseFromViewInv = new ArrayList<>();
		responseFromViewInv = gstr1OutwardVerticalProcessInvDao
				.getGstr1RSReports(request);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"Gstr1_Asp_ProcessedRecords_Outward_Vertical.xlsx");

		LOGGER.debug(
				"Gstr1 Asp B2CS Savable data response" + responseFromViewB2CS);

		if (responseFromViewB2CS != null && responseFromViewB2CS.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.Asp.vertical.b2cs.process.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromViewB2CS,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewB2CS.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromViewAR != null && responseFromViewAR.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.Asp.vertical.adv.process.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(1).getCells();
			errorDumpCells.importCustomObjects(responseFromViewAR,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewAR.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromViewAA != null && responseFromViewAA.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.Asp.vertical.adj.process.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(2).getCells();
			errorDumpCells.importCustomObjects(responseFromViewAA,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewAA.size(), true, "yyyy-mm-dd", false);
		}
		/*if (responseFromViewNil != null && responseFromViewNil.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.Asp.vertical.nil.process.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(3).getCells();
			errorDumpCells.importCustomObjects(responseFromViewNil,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewNil.size(), true, "yyyy-mm-dd", false);
		}
*/
		/*if (responseFromViewHsn != null && responseFromViewHsn.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.Asp.vertical.hsn.process.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(3).getCells();
			errorDumpCells.importCustomObjects(responseFromViewHsn,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewHsn.size(), true, "yyyy-mm-dd", false);
		}*/
		if (responseFromViewInv != null && responseFromViewInv.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp(
							"gstr1.Asp.vertical.invoice.process.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(3).getCells();
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
