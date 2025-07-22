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

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("Gstr1GstnErrorReportServiceImpl")
public class Gstr1GstnErrorReportServiceImpl
		implements Gstr1GstnErrorReportService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1GstnErrorReportServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr1GstnB2BErrorDaoImpl")
	private Gstr1GstnErrorDao gstr1GstnB2BErrorDao;

	@Autowired
	@Qualifier("Gstr1GstnB2CLErrorDaoImpl")
	private Gstr1GstnErrorDao gstr1GstnB2CLErrorDao;

	@Autowired
	@Qualifier("Gstr1GstnEXPErrorDaoImpl")
	private Gstr1GstnErrorDao gstr1GstnEXPErrorDao;

	@Autowired
	@Qualifier("Gstr1GstnCDNRErrorDaoImpl")
	private Gstr1GstnErrorDao gstr1GstnCDNRErrorDao;

	@Autowired
	@Qualifier("Gstr1GstnCDNURErrorDaoImpl")
	private Gstr1GstnErrorDao gstr1GstnCDNURErrorDao;

	@Autowired
	@Qualifier("Gstr1GstnB2CSErrorDaoImpl")
	private Gstr1GstnErrorDao gstr1GstnB2CSErrorDao;

	@Autowired
	@Qualifier("Gstr1GstnNilErrorDaoImpl")
	private Gstr1GstnErrorDao gstr1GstnNilErrorDao;
	
	@Autowired
	@Qualifier("Gstr1GstnHsnSummErrorDaoImpl")
	private Gstr1GstnErrorDao gstr1GstnHsnErrorDao;
	
	@Autowired
	@Qualifier("Gstr1GstnAmendmentsDaoImpl")
	private Gstr1GstnErrorDao gstr1GstnAmendmentsErrorDao;

	@Override
	public Workbook findGstnErrorReports(SearchCriteria criteria,
			PageRequest pageReq) {

		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Object> responseFromB2B = new ArrayList<>();
		responseFromB2B = gstr1GstnB2BErrorDao.getGstr1GstnErrorReport(request);

		List<Object> responseFromB2CL = new ArrayList<>();
		responseFromB2CL = gstr1GstnB2CLErrorDao
				.getGstr1GstnErrorReport(request);

		List<Object> responseFromEXP = new ArrayList<>();
		responseFromEXP = gstr1GstnEXPErrorDao.getGstr1GstnErrorReport(request);

		List<Object> responseFromCDNR = new ArrayList<>();
		responseFromCDNR = gstr1GstnCDNRErrorDao
				.getGstr1GstnErrorReport(request);

		List<Object> responseFromCDNUR = new ArrayList<>();
		responseFromCDNUR = gstr1GstnCDNURErrorDao
				.getGstr1GstnErrorReport(request);

		List<Object> responseFromB2CS = new ArrayList<>();
		responseFromB2CS = gstr1GstnB2CSErrorDao
				.getGstr1GstnErrorReport(request);
		
		List<Object> responseFromHsn = new ArrayList<>();
		responseFromHsn = gstr1GstnHsnErrorDao.getGstr1GstnErrorReport(request);


		List<Object> responseFromNil = new ArrayList<>();
		responseFromNil = gstr1GstnNilErrorDao.getGstr1GstnErrorReport(request);

		List<Object> responseFromAmendments = new ArrayList<>();
		responseFromAmendments = gstr1GstnAmendmentsErrorDao
				.getGstr1GstnErrorReport(request);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"GSTR1_GST_Error_Report.xlsx");

		if (responseFromB2B != null && responseFromB2B.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.b2b.error.report.headers").split(",");

			Worksheet tabSheet = workbook.getWorksheets().get(0);
			tabSheet.setSelected(true);
			tabSheet.setTabColor(Color.getBlue());
			
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromB2B, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromB2B.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromB2CL != null && responseFromB2CL.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.b2cl.error.report.headers").split(",");

			Worksheet tabSheet = workbook.getWorksheets().get(1);
			tabSheet.setSelected(true);
			tabSheet.setTabColor(Color.getBlue());
			
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromB2CL, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromB2CL.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromEXP != null && responseFromEXP.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.exp.error.report.headers").split(",");

			Worksheet tabSheet = workbook.getWorksheets().get(2);
			tabSheet.setSelected(true);
			tabSheet.setTabColor(Color.getBlue());
			
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromEXP, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromEXP.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromCDNR != null && responseFromCDNR.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.cdnr.error.report.headers").split(",");

			Worksheet tabSheet = workbook.getWorksheets().get(3);
			tabSheet.setSelected(true);
			tabSheet.setTabColor(Color.getBlue());
			
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromCDNR, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromCDNR.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromCDNUR != null && responseFromCDNUR.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.cdnur.error.report.headers")
					.split(",");

			Worksheet tabSheet = workbook.getWorksheets().get(4);
			tabSheet.setSelected(true);
			tabSheet.setTabColor(Color.getBlue());
			
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromCDNUR,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromCDNUR.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromB2CS != null && responseFromB2CS.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.b2cs.error.report.headers").split(",");

			Worksheet tabSheet = workbook.getWorksheets().get(5);
			tabSheet.setSelected(true);
			tabSheet.setTabColor(Color.getBlue());
			
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromB2CS, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromB2CS.size(), true, "yyyy-mm-dd", false);
		}
		if (responseFromNil != null && responseFromNil.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.nil.error.report.headers").split(",");

			Worksheet tabSheet = workbook.getWorksheets().get(6);
			tabSheet.setSelected(true);
			tabSheet.setTabColor(Color.getBlue());
			
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromNil, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromNil.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromHsn != null && responseFromHsn.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.hsnSumm.error.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(9).getCells();
			errorDumpCells.importCustomObjects(responseFromHsn, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromHsn.size(), true, "yyyy-mm-dd", false);
		}
		if (responseFromAmendments != null && responseFromAmendments.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.amendments.report.headers")
					.split(",");

			Worksheet tabSheet = workbook.getWorksheets().get(11);
			tabSheet.setSelected(true);
			tabSheet.setTabColor(Color.getBlue());
			
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromAmendments,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromAmendments.size(), true, "yyyy-mm-dd", false);
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
