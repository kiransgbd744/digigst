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
 * @author Sujith.Nanga
 *
 */

	
	@Service("Gstr1NewGstnErrorReportServiceImpl")
	public class Gstr1NewGstnErrorReportServiceImpl
			implements Gstr1GstnErrorReportService {

		private static final Logger LOGGER = LoggerFactory
				.getLogger(Gstr1NewGstnErrorReportServiceImpl.class);

		@Autowired
		CommonUtility commonUtility;

		@Autowired
		@Qualifier("Gstr1GstnNewB2BErrorDaoImpl")
		private Gstr1GstnErrorDao gstr1GstnB2BErrorDao;

		/*@Autowired
		@Qualifier("Gstr1GstnNewEXPErrorDaoImpl")
		private Gstr1GstnErrorDao gstr1GstnEXPErrorDao;

		@Autowired
		@Qualifier("Gstr1GstnNewCDNRErrorDaoImpl")
		private Gstr1GstnErrorDao gstr1GstCDNRErrorDao;

		@Autowired
		@Qualifier("Gstr1GstnNewCDNURErrorDaoImpl")
		private Gstr1GstnErrorDao gstr1GstCDNURErrorDao;*/


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

			/*List<Object> responseFromEXP = new ArrayList<>();
			responseFromEXP = gstr1GstnEXPErrorDao.getGstr1GstnErrorReport(request);

			List<Object> responseFromCDNR = new ArrayList<>();
			responseFromCDNR = gstr1GstCDNRErrorDao
					.getGstr1GstnErrorReport(request);

			List<Object> responseFromCDNUR = new ArrayList<>();
			responseFromCDNUR = gstr1GstCDNURErrorDao
					.getGstr1GstnErrorReport(request);*/

			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"GSTR1_NEW__Error_Report.xlsx");

			if (responseFromB2B != null && responseFromB2B.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp("gstr1.new.gstn.b2b.error.report.headers").split(",");

				Worksheet tabSheet = workbook.getWorksheets().get(0);
				Cells errorDumpCells = tabSheet.getCells();
				errorDumpCells.importCustomObjects(responseFromB2B, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn,
						responseFromB2B.size(), true, "yyyy-mm-dd", false);
			}


			/*if (responseFromEXP != null && responseFromEXP.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp("gstr1.new.gstn.b2b.error.report.headers").split(",");

				Worksheet tabSheet = workbook.getWorksheets().get(1);
				
				Cells errorDumpCells = tabSheet.getCells();
				errorDumpCells.importCustomObjects(responseFromEXP, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn,
						responseFromEXP.size(), true, "yyyy-mm-dd", false);
			}

			if (responseFromCDNR != null && responseFromCDNR.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp("gstr1.new.gstn.b2b.error.report.headers").split(",");

				Worksheet tabSheet = workbook.getWorksheets().get(2);
				
				Cells errorDumpCells = tabSheet.getCells();
				errorDumpCells.importCustomObjects(responseFromCDNR, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn,
						responseFromCDNR.size(), true, "yyyy-mm-dd", false);
			}

			if (responseFromCDNUR != null && responseFromCDNUR.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp("gstr1.new.gstn.b2b.error.report.headers")
						.split(",");

				Worksheet tabSheet = workbook.getWorksheets().get(3);
				
				Cells errorDumpCells = tabSheet.getCells();
				errorDumpCells.importCustomObjects(responseFromCDNUR,
						invoiceHeaders, isHeaderRequired, startRow, startcolumn,
						responseFromCDNUR.size(), true, "yyyy-mm-dd", false);
			}*/
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
