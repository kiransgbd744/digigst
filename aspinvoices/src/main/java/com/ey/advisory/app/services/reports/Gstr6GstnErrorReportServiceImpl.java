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
	
	@Service("Gstr6GstnErrorReportServiceImpl")
	public class Gstr6GstnErrorReportServiceImpl
			implements Gstr6GstnErrorReportService {

		private static final Logger LOGGER = LoggerFactory
				.getLogger(Gstr6GstnErrorReportServiceImpl.class);

		@Autowired
		CommonUtility commonUtility;

		@Autowired
		@Qualifier("Gstr6GstnB2BErrorDaoImpl")
		private Gstr6GstnErrorDao gstr6GstnB2BErrorDao;

		@Autowired
		@Qualifier("Gstr6GstnB2BAErrorDaoImpl")
		private Gstr6GstnErrorDao gstr6GstnB2BAErrorDao;

		@Autowired
		@Qualifier("Gstr6GstnCDNErrorDaoImpl")
		private Gstr6GstnErrorDao gstr6GstnCDNErrorDao;

		@Autowired
		@Qualifier("Gstr6GstnCDNAErrorDaoImpl")
		private Gstr6GstnErrorDao gstr6GstnCDNAErrorDao;
		
		@Autowired
		@Qualifier("Gstr6GstnISDErrorDaoImpl")
		private Gstr6GstnErrorDao gstr6GstnISDErrorDao;
		
		@Autowired
		@Qualifier("Gstr6GstnISDAErrorDaoImpl")
		private Gstr6GstnErrorDao gstr6GstnISDAErrorDao;

		/*@Autowired
		@Qualifier("Gstr1GstnCDNURErrorDaoImpl")
		private Gstr1GstnErrorDao gstr1GstnCDNURErrorDao;

		@Autowired
		@Qualifier("Gstr1GstnB2CSErrorDaoImpl")
		private Gstr1GstnErrorDao gstr1GstnB2CSErrorDao;

		@Autowired
		@Qualifier("Gstr1GstnNilErrorDaoImpl")
		private Gstr1GstnErrorDao gstr1GstnNilErrorDao;*/

		/*@Autowired
		@Qualifier("Gstr1GstnAdvRecErrorDaoImpl")
		private Gstr1GstnErrorDao gstr1GstnAdvRecErrorDao;

		@Autowired
		@Qualifier("Gstr1GstnAdvAdjErrorDaoImpl")
		private Gstr1GstnErrorDao gstr1GstnAdvAdjErrorDao;

		@Autowired
		@Qualifier("Gstr1GstnHsnSummErrorDaoImpl")
		private Gstr1GstnErrorDao gstr1GstnHsnErrorDao;

		@Autowired
		@Qualifier("Gstr1GstnDocIssuedErrorDaoImpl")
		private Gstr1GstnErrorDao gstr1GstnDocIssuedErrorDao;*/
		
		/*@Autowired
		@Qualifier("Gstr1GstnAmendmentsDaoImpl")
		private Gstr1GstnErrorDao gstr1GstnAmendmentsErrorDao;
*/
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

			/*List<Object> responseFromCDNUR = new ArrayList<>();
			responseFromCDNUR = gstr6GstnCDNErrorDao
					.getGstr6GstnErrorReport(request);

			List<Object> responseFromB2CS = new ArrayList<>();
			responseFromB2CS = gstr1GstnB2CSErrorDao
					.getGstr1GstnErrorReport(request);

			List<Object> responseFromNil = new ArrayList<>();
			responseFromNil = gstr1GstnNilErrorDao.getGstr1GstnErrorReport(request);*/

			/*List<Object> responseFromadvRec = new ArrayList<>();
			responseFromadvRec = gstr1GstnAdvRecErrorDao
					.getGstr1GstnErrorReport(request);

			List<Object> responseFromadvAdj = new ArrayList<>();
			responseFromadvAdj = gstr1GstnAdvAdjErrorDao
					.getGstr1GstnErrorReport(request);

			List<Object> responseFromHsn = new ArrayList<>();
			responseFromHsn = gstr1GstnHsnErrorDao.getGstr1GstnErrorReport(request);

			List<Object> responseFromDocIssued = new ArrayList<>();
			responseFromDocIssued = gstr1GstnDocIssuedErrorDao
					.getGstr1GstnErrorReport(request);
			*/
			/*List<Object> responseFromAmendments = new ArrayList<>();
			responseFromAmendments = gstr1GstnAmendmentsErrorDao
					.getGstr1GstnErrorReport(request);
*/
			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"GSTR-6_GSTN Error Report.xlsx");

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
						.getProp("gstr6.gstn.cdn.error.report.headers").split(",");

				Cells errorDumpCells = workbook.getWorksheets().get(2).getCells();
				errorDumpCells.importCustomObjects(responseFromCDN, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn,
						responseFromCDN.size(), true, "yyyy-mm-dd", false);
			}

			if (responseFromCDNA != null && responseFromCDNA.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp("gstr6.gstn.cdna.error.report.headers").split(",");

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

			/*if (responseFromCDNUR != null && responseFromCDNUR.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp("gstr1.gstn.cdnur.error.report.headers")
						.split(",");

				Cells errorDumpCells = workbook.getWorksheets().get(4).getCells();
				errorDumpCells.importCustomObjects(responseFromCDNUR,
						invoiceHeaders, isHeaderRequired, startRow, startcolumn,
						responseFromCDNUR.size(), true, "yyyy-mm-dd", false);
			}

			if (responseFromB2CS != null && responseFromB2CS.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp("gstr1.gstn.b2cs.error.report.headers").split(",");

				Cells errorDumpCells = workbook.getWorksheets().get(5).getCells();
				errorDumpCells.importCustomObjects(responseFromB2CS, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn,
						responseFromB2CS.size(), true, "yyyy-mm-dd", false);
			}
			if (responseFromNil != null && responseFromNil.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp("gstr1.gstn.nil.error.report.headers").split(",");

				Cells errorDumpCells = workbook.getWorksheets().get(6).getCells();
				errorDumpCells.importCustomObjects(responseFromNil, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn,
						responseFromNil.size(), true, "yyyy-mm-dd", false);
			}*/

			/*if (responseFromadvRec != null && responseFromadvRec.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp("gstr1.gstn.advRec.error.report.headers")
						.split(",");

				Cells errorDumpCells = workbook.getWorksheets().get(7).getCells();
				errorDumpCells.importCustomObjects(responseFromadvRec,
						invoiceHeaders, isHeaderRequired, startRow, startcolumn,
						responseFromadvRec.size(), true, "yyyy-mm-dd", false);
			}

			if (responseFromadvAdj != null && responseFromadvAdj.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp("gstr1.gstn.advAdj.error.report.headers")
						.split(",");

				Cells errorDumpCells = workbook.getWorksheets().get(8).getCells();
				errorDumpCells.importCustomObjects(responseFromadvAdj,
						invoiceHeaders, isHeaderRequired, startRow, startcolumn,
						responseFromadvAdj.size(), true, "yyyy-mm-dd", false);
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

			if (responseFromDocIssued != null && responseFromDocIssued.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp("gstr1.gstn.docIssued.error.report.headers")
						.split(",");

				Cells errorDumpCells = workbook.getWorksheets().get(10).getCells();
				errorDumpCells.importCustomObjects(responseFromDocIssued,
						invoiceHeaders, isHeaderRequired, startRow, startcolumn,
						responseFromDocIssued.size(), true, "yyyy-mm-dd", false);
			}*/
			
			/*if (responseFromAmendments != null && responseFromAmendments.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp("gstr1.gstn.amendments.report.headers")
						.split(",");

				Cells errorDumpCells = workbook.getWorksheets().get(7).getCells();
				errorDumpCells.importCustomObjects(responseFromAmendments,
						invoiceHeaders, isHeaderRequired, startRow, startcolumn,
						responseFromAmendments.size(), true, "yyyy-mm-dd", false);
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



