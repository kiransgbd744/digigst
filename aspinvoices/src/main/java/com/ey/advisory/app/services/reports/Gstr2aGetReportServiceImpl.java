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
import com.ey.advisory.app.docs.dto.GstnConsolidatedReqDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.search.PageRequest;

/**
 * @author Sujith.Nanga
 *
 * 
 */
@Service("Gstr2aGetReportServiceImpl")
public class Gstr2aGetReportServiceImpl
		implements Gstr1AGstnErrorReportService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2aGetReportServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr2GetB2BReportDaoImpl")
	private Gstr1aGetDao gstr2ab2bGetDao;

	@Autowired
	@Qualifier("Gstr2GetB2BAReportDaoImpl")
	private Gstr1aGetDao gstr2ab2baGetDao;

	@Autowired
	@Qualifier("Gstr2GetCDNReportDaoImpl")
	private Gstr1aGetDao gstr2acdnGetDao;

	@Autowired
	@Qualifier("Gstr2GetCDNAReportDaoImpl")
	private Gstr1aGetDao gstr2acdnaGetDao;

	@Autowired
	@Qualifier("Gstr2GetISDReportDaoImpl")
	private Gstr1aGetDao gstr2aisdGetDao;

	@Autowired
	@Qualifier("Gstr2GetIMPGReportDaoImpl")
	private Gstr1aGetDao gstr2aimpgGetDao;

	@Autowired
	@Qualifier("Gstr2GetIMPGSEZReportDaoImpl")
	private Gstr1aGetDao gstr2aimpgsezGetDao;

	@Autowired
	@Qualifier("Gstr2GetIMPGSEZAMDReportDaoImpl")
	private Gstr1aGetDao gstr2aimpgsezamdGetDao;

	public Workbook findGstnErrorReports(
			List<GstnConsolidatedReqDto> criteriaDtos, PageRequest pageReq) {
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		List<Object> responseFromB2B = new ArrayList<>();
		List<Object> responseFromB2BA = new ArrayList<>();
		List<Object> responseFromCDN = new ArrayList<>();
		List<Object> responseFromCDNA = new ArrayList<>();
		List<Object> responseFromISD = new ArrayList<>();
		List<Object> responseFromIMPG = new ArrayList<>();
		List<Object> responseFromIMPGSEZ = new ArrayList<>();
		List<Object> responseFromIMPGSEZAMD = new ArrayList<>();

		for (GstnConsolidatedReqDto request : criteriaDtos) {
			responseFromB2B.addAll(
					gstr2ab2bGetDao.getGstnConsolidatedReports(request));
			responseFromB2BA.addAll(
					gstr2ab2baGetDao.getGstnConsolidatedReports(request));
			responseFromCDN.addAll(
					gstr2acdnGetDao.getGstnConsolidatedReports(request));
			responseFromCDNA.addAll(
					gstr2acdnaGetDao.getGstnConsolidatedReports(request));
			responseFromISD.addAll(
					gstr2aisdGetDao.getGstnConsolidatedReports(request));
			responseFromIMPG.addAll(
					gstr2aimpgGetDao.getGstnConsolidatedReports(request));
			responseFromIMPGSEZ.addAll(
					gstr2aimpgsezGetDao.getGstnConsolidatedReports(request));
			responseFromIMPGSEZAMD.addAll(
					gstr2aimpgsezamdGetDao.getGstnConsolidatedReports(request));
		}

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"GSTR2Adownload.xlsx");

		if (responseFromB2B != null && responseFromB2B.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr2a.new.get.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromB2B, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromB2B.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromB2BA != null && responseFromB2BA.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr2a.new.get.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(1).getCells();
			errorDumpCells.importCustomObjects(responseFromB2BA, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromB2BA.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromCDN != null && responseFromCDN.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr2a.new.get.report.headers").split(",");

			// Worksheet tabSheet = workbook.getWorksheets().get(2);
			/*
			 * tabSheet.setSelected(true);
			 * tabSheet.setTabColor(Color.getBlue());
			 */
			Cells errorDumpCells = workbook.getWorksheets().get(2).getCells();
			// Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromCDN, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromCDN.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromCDNA != null && responseFromCDNA.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr2a.new.get.report.headers").split(",");

			// Worksheet tabSheet = workbook.getWorksheets().get(3);
			/*
			 * tabSheet.setSelected(true);
			 * tabSheet.setTabColor(Color.getBlue());
			 */
			Cells errorDumpCells = workbook.getWorksheets().get(3).getCells();
			// Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromCDNA, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromCDNA.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromISD != null && responseFromISD.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr2a.new.get.report.headers").split(",");

			// Worksheet tabSheet = workbook.getWorksheets().get(4);
			Cells errorDumpCells = workbook.getWorksheets().get(4).getCells();
			/*
			 * tabSheet.setSelected(true);
			 * tabSheet.setTabColor(Color.getBlue());
			 */

			// Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromISD, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromISD.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromIMPG != null && responseFromIMPG.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr2a.new.get.report.headers").split(",");

			// Worksheet tabSheet = workbook.getWorksheets().get(5);
			/*
			 * tabSheet.setSelected(true);
			 * tabSheet.setTabColor(Color.getBlue());
			 */
			Cells errorDumpCells = workbook.getWorksheets().get(5).getCells();
			// Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromIMPG, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromIMPG.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromIMPGSEZ != null && responseFromIMPGSEZ.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr2a.new.get.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(6).getCells();

			errorDumpCells.importCustomObjects(responseFromIMPGSEZ,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromIMPGSEZ.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromIMPGSEZAMD != null
				&& responseFromIMPGSEZAMD.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr2a.new.get.report.headers").split(",");
			Cells errorDumpCells = workbook.getWorksheets().get(7).getCells();
			// Worksheet tabSheet = workbook.getWorksheets().get(7);
			/*
			 * tabSheet.setSelected(true);
			 * tabSheet.setTabColor(Color.getBlue());
			 */

			// Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromIMPGSEZAMD,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromIMPGSEZAMD.size(), true, "yyyy-mm-dd", false);
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
