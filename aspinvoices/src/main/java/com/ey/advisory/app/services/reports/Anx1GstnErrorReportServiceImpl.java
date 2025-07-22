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

@Service("Anx1GstnErrorReportServiceImpl")
public class Anx1GstnErrorReportServiceImpl
		implements Anx1GstnErrorReportService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1GstnErrorReportServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Anx1GSTNB2C3AErrorReportsDaoImpl")
	private Anx1GstnErrorDao anx1GstnB2C3AErrorDao;

	@Autowired
	@Qualifier("Anx1GSTNB2B3BErrorReportsDaoImpl")
	private Anx1GstnErrorDao anx1GstnB2B3BErrorDao;

	@Autowired
	@Qualifier("Anx1GSTN3CErrorReportsDaoImpl")
	private Anx1GstnErrorDao anx1Gstn3CErrorDao;

	@Autowired
	@Qualifier("Anx1GSTN3DErrorReportDaoImpl")
	private Anx1GstnErrorDao anx1Gstn3DErrorDao;

	@Autowired
	@Qualifier("Anx1GSTN3EErrorReportDaoImpl")
	private Anx1GstnErrorDao anx1Gstn3EErrorDao;

	@Autowired
	@Qualifier("Anx1GSTN3FErrorReportDaoImpl")
	private Anx1GstnErrorDao anx1Gstn3FErrorDao;

	@Autowired
	@Qualifier("Anx1GSTN3GErrorReportDaoImpl")
	private Anx1GstnErrorDao anx1Gstn3GErrorDao;

	@Autowired
	@Qualifier("Anx1GSTN3HErrorReportDaoImpl")
	private Anx1GstnErrorDao anx1Gstn3HErrorDao;

	@Autowired
	@Qualifier("Anx1GSTN3IErrorReportDaoImpl")
	private Anx1GstnErrorDao anx1Gstn3IErrorDao;

	@Autowired
	@Qualifier("Anx1GSTN3JErrorReportDaoImpl")
	private Anx1GstnErrorDao anx1Gstn3JErrorDao;

	@Autowired
	@Qualifier("Anx1GSTN3KErrorReportDaoImpl")
	private Anx1GstnErrorDao anx1Gstn3kErrorDao;

	@Autowired
	@Qualifier("Anx1GSTN3LErrorReportDaoImpl")
	private Anx1GstnErrorDao anx1Gstn3LErrorDao;

	@Autowired
	@Qualifier("Anx1GSTNTable4ErrorReportDaoImpl")
	private Anx1GstnErrorDao anx1GstnTable4ErrorDao;

	@Override
	public Workbook findGstnErrorReports(SearchCriteria criteria,
			PageRequest pageReq) {

		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Object> responseFromB2C3A = new ArrayList<>();
		responseFromB2C3A = anx1GstnB2C3AErrorDao
				.getAnx1GstnErrorReport(request);

		List<Object> responseFromB2B3B = new ArrayList<>();
		responseFromB2B3B = anx1GstnB2B3BErrorDao
				.getAnx1GstnErrorReport(request);

		List<Object> responseFrom3C = new ArrayList<>();
		responseFrom3C = anx1Gstn3CErrorDao.getAnx1GstnErrorReport(request);

		List<Object> responseFrom3D = new ArrayList<>();
		responseFrom3D = anx1Gstn3DErrorDao.getAnx1GstnErrorReport(request);

		List<Object> responseFrom3E = new ArrayList<>();
		responseFrom3E = anx1Gstn3EErrorDao.getAnx1GstnErrorReport(request);

		List<Object> responseFrom3F = new ArrayList<>();
		responseFrom3F = anx1Gstn3FErrorDao.getAnx1GstnErrorReport(request);

		List<Object> responseFrom3G = new ArrayList<>();
		responseFrom3G = anx1Gstn3GErrorDao.getAnx1GstnErrorReport(request);

		List<Object> responseFrom3H = new ArrayList<>();
		responseFrom3H = anx1Gstn3HErrorDao.getAnx1GstnErrorReport(request);

		List<Object> responseFrom3I = new ArrayList<>();
		responseFrom3I = anx1Gstn3IErrorDao.getAnx1GstnErrorReport(request);

		List<Object> responseFrom3J = new ArrayList<>();
		responseFrom3J = anx1Gstn3JErrorDao.getAnx1GstnErrorReport(request);

		List<Object> responseFrom3K = new ArrayList<>();
		responseFrom3K = anx1Gstn3kErrorDao.getAnx1GstnErrorReport(request);

		List<Object> responseFrom3L = new ArrayList<>();
		responseFrom3L = anx1Gstn3LErrorDao.getAnx1GstnErrorReport(request);

		List<Object> responseFromTable4 = new ArrayList<>();
		responseFromTable4 = anx1GstnTable4ErrorDao
				.getAnx1GstnErrorReport(request);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"GSTN_ANX-1_Errors_GSTN_Format.xlsx");

		if (responseFromB2C3A != null && responseFromB2C3A.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.GSTNB2C3AError.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromB2C3A,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromB2C3A.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromB2B3B != null && responseFromB2B3B.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.GSTNB2B3BError.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(1).getCells();
			errorDumpCells.importCustomObjects(responseFromB2B3B,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromB2B3B.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFrom3C != null && responseFrom3C.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.GSTN3CBError.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(2).getCells();
			errorDumpCells.importCustomObjects(responseFrom3C, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFrom3C.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFrom3D != null && responseFrom3D.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.GSTN3DError.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(3).getCells();
			errorDumpCells.importCustomObjects(responseFrom3D, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFrom3D.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFrom3E != null && responseFrom3E.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.GSTN3EError.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(4).getCells();
			errorDumpCells.importCustomObjects(responseFrom3E, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFrom3E.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFrom3F != null && responseFrom3E.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.GSTN3FError.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(5).getCells();
			errorDumpCells.importCustomObjects(responseFrom3F, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFrom3F.size(), true, "yyyy-mm-dd", false);
		}
		if (responseFrom3G != null && responseFrom3G.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.GSTN3GError.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(6).getCells();
			errorDumpCells.importCustomObjects(responseFrom3G, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFrom3G.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFrom3H != null && responseFrom3H.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.GSTN3HError.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(7).getCells();
			errorDumpCells.importCustomObjects(responseFrom3H, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFrom3H.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFrom3I != null && responseFrom3I.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.GSTN3IError.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(8).getCells();
			errorDumpCells.importCustomObjects(responseFrom3I, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFrom3I.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFrom3J != null && responseFrom3J.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.GSTN3JError.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(9).getCells();
			errorDumpCells.importCustomObjects(responseFrom3J, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFrom3J.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFrom3K != null && responseFrom3K.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.GSTN3KError.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(10).getCells();
			errorDumpCells.importCustomObjects(responseFrom3K, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFrom3K.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFrom3L != null && responseFrom3L.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.GSTN3LError.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(11).getCells();
			errorDumpCells.importCustomObjects(responseFrom3L, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFrom3L.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromTable4 != null && responseFromTable4.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.GSTNTable4Error.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(12).getCells();
			errorDumpCells.importCustomObjects(responseFromTable4,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromTable4.size(), true, "yyyy-mm-dd", false);
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
