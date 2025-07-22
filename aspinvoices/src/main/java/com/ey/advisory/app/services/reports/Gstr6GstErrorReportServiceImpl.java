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

	@Service("Gstr6GstErrorReportServiceImpl")
	public class Gstr6GstErrorReportServiceImpl
			implements Gstr6GstnErrorReportService {

		private static final Logger LOGGER = LoggerFactory
				.getLogger(Gstr6GstErrorReportServiceImpl.class);

		@Autowired
		CommonUtility commonUtility;

		@Autowired
		@Qualifier("Gstr6GstnB2BErrorDaoImpl")
		private Gstr6GstnErrorDao gstr6GstnB2BErrorDao;

		@Autowired
		@Qualifier("Gstr6GstnB2BAErrorDaoImpl")
		private Gstr6GstnErrorDao gstr6GstnB2BAErrorDao;

		@Autowired
		@Qualifier("Gstr6GstCDNErrorDaoImpl")
		private Gstr6GstnErrorDao gstr6GstnCDNErrorDao;

		@Autowired
		@Qualifier("Gstr6GstCDNAErrorDaoImpl")
		private Gstr6GstnErrorDao gstr6GstnCDNAErrorDao;
		
		@Autowired
		@Qualifier("Gstr6GstnISDErrorDaoImpl")
		private Gstr6GstnErrorDao gstr6GstnISDErrorDao;
		
		@Autowired
		@Qualifier("Gstr6GstnISDAErrorDaoImpl")
		private Gstr6GstnErrorDao gstr6GstnISDAErrorDao;

		
		@Override
		public Workbook findGstnErrorReports(SearchCriteria criteria,
				PageRequest pageReq) {

			Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

			Workbook workbook = new Workbook();
			int startRow = 1;
			int startcolumn = 0;
			boolean isHeaderRequired = false;

			List<Object> responseFromB2B = new ArrayList<>();
			responseFromB2B = gstr6GstnB2BErrorDao.getGstr6GstnErrorReport(request);

			List<Object> responseFromB2BA = new ArrayList<>();
			responseFromB2BA = gstr6GstnB2BAErrorDao
					.getGstr6GstnErrorReport(request);

			List<Object> responseFromCDN = new ArrayList<>();
			responseFromCDN = gstr6GstnCDNErrorDao.getGstr6GstnErrorReport(request);

			List<Object> responseFromCDNA = new ArrayList<>();
			responseFromCDNA = gstr6GstnCDNAErrorDao
					.getGstr6GstnErrorReport(request);
			
			List<Object> responseFromISD = new ArrayList<>();
			responseFromISD = gstr6GstnISDErrorDao
					.getGstr6GstnErrorReport(request);
			
			List<Object> responseFromISDA = new ArrayList<>();
			responseFromISDA = gstr6GstnISDAErrorDao
					.getGstr6GstnErrorReport(request);

			
			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"GSTR-6_GST Error Report.xlsx");

			if (responseFromB2B != null && responseFromB2B.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp("gstr6.gstn.b2b.error.report.headers").split(",");

				Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
				errorDumpCells.importCustomObjects(responseFromB2B, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn,
						responseFromB2B.size(), true, "yyyy-mm-dd", false);
			}

			if (responseFromB2BA != null && responseFromB2BA.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp("gstr6.gstn.b2ba.error.report.headers").split(",");

				Cells errorDumpCells = workbook.getWorksheets().get(1).getCells();
				errorDumpCells.importCustomObjects(responseFromB2BA, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn,
						responseFromB2BA.size(), true, "yyyy-mm-dd", false);
			}

			if (responseFromCDN != null && responseFromCDN.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp("gstr6.gst.cdn.error.report.headers").split(",");

				Cells errorDumpCells = workbook.getWorksheets().get(2).getCells();
				errorDumpCells.importCustomObjects(responseFromCDN, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn,
						responseFromCDN.size(), true, "yyyy-mm-dd", false);
			}

			if (responseFromCDNA != null && responseFromCDNA.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp("gstr6.gst.cdna.error.report.headers").split(",");

				Cells errorDumpCells = workbook.getWorksheets().get(3).getCells();
				errorDumpCells.importCustomObjects(responseFromCDNA, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn,
						responseFromCDNA.size(), true, "yyyy-mm-dd", false);
			}
			
			if (responseFromISD != null && responseFromISD.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp("gstr6.gstn.isd.error.report.headers").split(",");

				Cells errorDumpCells = workbook.getWorksheets().get(4).getCells();
				errorDumpCells.importCustomObjects(responseFromISD, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn,
						responseFromISD.size(), true, "yyyy-mm-dd", false);
			}
			
			if (responseFromISDA != null && responseFromISDA.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp("gstr6.gstn.isda.error.report.headers").split(",");

				Cells errorDumpCells = workbook.getWorksheets().get(5).getCells();
				errorDumpCells.importCustomObjects(responseFromISDA, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn,
						responseFromISDA.size(), true, "yyyy-mm-dd", false);
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


