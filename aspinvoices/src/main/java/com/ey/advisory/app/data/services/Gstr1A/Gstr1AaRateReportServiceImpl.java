/**
 * 
 */
package com.ey.advisory.app.data.services.Gstr1A;

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
import com.aspose.cells.Worksheet;
import com.ey.advisory.app.docs.dto.GstnConsolidatedReqDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.search.PageRequest;

/**
 * @author Sujith.Nanga
 *
 */

@Service("Gstr1AaRateReportServiceImpl")
public class Gstr1AaRateReportServiceImpl
		implements Gstr1AAGstnErrorReportService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1AaRateReportServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr1AaGetReportDaoImpl")
	private Gstr1AaGetDao gstr1aGetDao;

	@Autowired
	@Qualifier("Gstr1AGetB2CSSummaryTablesDaoImpl")
	private Gstr1AaGetDao gstr1GstnB2CSSummaryTablesDao;

	@Autowired
	@Qualifier("Gstr1AGetB2CSASummaryTablesDaoImpl")
	private Gstr1AaGetDao gstr1GstnB2CSASummaryTablesDao;

	@Autowired
	@Qualifier("Gstr1AGetATSummaryTablesDaoImpl")
	private Gstr1AaGetDao gstr1GstnATSummaryTablesDao;

	@Autowired
	@Qualifier("Gstr1AGetATASummaryTablesDaoImpl")
	private Gstr1AaGetDao gstr1GstnATASummaryTablesDao;

	@Autowired
	@Qualifier("Gstr1AGetTXPSummaryTablesDaoImpl")
	private Gstr1AaGetDao gstr1GstnTXPSummaryTablesDao;

	@Autowired
	@Qualifier("Gstr1AGetTXPASummaryTablesDaoImpl")
	private Gstr1AaGetDao gstr1GstnTXPASummaryTablesDao;

	@Autowired
	@Qualifier("Gstr1AGetNilNonExemptSummaryTablesDaoImpl")
	private Gstr1AaGetDao gstr1GstnNilNonExemptSummaryTablesDao;

	@Autowired
	@Qualifier("Gstr1AaRateHsnSummaryTablesDaoImpl")
	private Gstr1AaGetDao gstr1GstnHsnSummaryTablesDao;

	@Autowired
	@Qualifier("Gstr1AGetInvSeriesTablesDaoImpl")
	private Gstr1AaGetDao gstr1GstnInvSeriesTablesDao;

	public Workbook findGstnErrorReports(
			List<GstnConsolidatedReqDto> criteriaDtos, PageRequest pageReq) {
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		List<Object> responseFromTrans = new ArrayList<>();
		List<Object> responseFromB2CS = new ArrayList<>();
		List<Object> responseFromB2CSA = new ArrayList<>();
		List<Object> responseFromAT = new ArrayList<>();
		List<Object> responseFromATA = new ArrayList<>();
		List<Object> responseFromViewTXP = new ArrayList<>();
		List<Object> responseFromViewTXPA = new ArrayList<>();
		List<Object> responseFromViewNil = new ArrayList<>();
		List<Object> responseFromViewHsn = new ArrayList<>();
		List<Object> responseFromViewInv = new ArrayList<>();

		for (GstnConsolidatedReqDto request : criteriaDtos) {
			responseFromTrans
					.addAll(gstr1aGetDao.getGstnConsolidatedReports(request));

			responseFromB2CS.addAll(gstr1GstnB2CSSummaryTablesDao
					.getGstnConsolidatedReports(request));

			responseFromB2CSA.addAll(gstr1GstnB2CSASummaryTablesDao
					.getGstnConsolidatedReports(request));

			responseFromAT.addAll(gstr1GstnATSummaryTablesDao
					.getGstnConsolidatedReports(request));

			responseFromATA.addAll(gstr1GstnATASummaryTablesDao
					.getGstnConsolidatedReports(request));

			responseFromViewTXP.addAll(gstr1GstnTXPSummaryTablesDao
					.getGstnConsolidatedReports(request));

			responseFromViewTXPA.addAll(gstr1GstnTXPASummaryTablesDao
					.getGstnConsolidatedReports(request));

			responseFromViewNil.addAll(gstr1GstnNilNonExemptSummaryTablesDao
					.getGstnConsolidatedReports(request));

			responseFromViewHsn.addAll(gstr1GstnHsnSummaryTablesDao
					.getGstnConsolidatedReports(request));

			responseFromViewInv.addAll(gstr1GstnInvSeriesTablesDao
					.getGstnConsolidatedReports(request));
		}
		
		if (responseFromTrans.isEmpty() && responseFromB2CS.isEmpty()
				&& responseFromB2CSA.isEmpty() && responseFromAT.isEmpty()
				&& responseFromATA.isEmpty() && responseFromViewTXP.isEmpty()
				&& responseFromViewTXPA.isEmpty()
				&& responseFromViewNil.isEmpty()
				&& responseFromViewHsn.isEmpty()
				&& responseFromViewInv.isEmpty())
			
			return workbook;
		
		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"GSTR-1A_SumRate_Tables_Saved_Submitted.xlsx");

		if (responseFromTrans != null && responseFromTrans.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.trans.saved.report.headers")
					.split(",");

			Worksheet tabSheet = workbook.getWorksheets()
					.get("Transactional Data");
			/*
			 * tabSheet.setSelected(true);
			 * tabSheet.setTabColor(Color.getBlue());
			 * tabSheet.getAutoFilter().setRange(0, 0, 29);
			 */

			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromTrans,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromTrans.size(), true, "yyyy-mm-dd", false);
		}

		else {
			workbook.getWorksheets().removeAt("Transactional Data");
		}

		if (responseFromB2CS != null && responseFromB2CS.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.summary.tables.b2cs.report.headers")
					.split(",");

			Worksheet tabSheet = workbook.getWorksheets().get("GSTN_B2CS");
			/*
			 * tabSheet.setSelected(true);
			 * tabSheet.setTabColor(Color.getBlue());
			 */
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromB2CS, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromB2CS.size(), true, "yyyy-mm-dd", false);
		}

		else {
			workbook.getWorksheets().removeAt("GSTN_B2CS");
		}

		if (responseFromB2CSA != null && responseFromB2CSA.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.summary.tables.b2csa.report.headers")
					.split(",");

			Worksheet tabSheet = workbook.getWorksheets().get("GSTN_B2CSA");
			/*
			 * tabSheet.setSelected(true);
			 * tabSheet.setTabColor(Color.getBlue());
			 */
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromB2CSA,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromB2CSA.size(), true, "yyyy-mm-dd", false);
		}

		else {
			workbook.getWorksheets().removeAt("GSTN_B2CSA");
		}

		if (responseFromAT != null && responseFromAT.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.summary.tables.at.report.headers")
					.split(",");

			Worksheet tabSheet = workbook.getWorksheets()
					.get("GSTN_Advance Received");
			/*
			 * tabSheet.setSelected(true);
			 * tabSheet.setTabColor(Color.getBlue());
			 */
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromAT, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromAT.size(), true, "yyyy-mm-dd", false);
		}

		else {
			workbook.getWorksheets().removeAt("GSTN_Advance Received");
		}
		if (responseFromATA != null && responseFromATA.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.summary.tables.ata.report.headers")
					.split(",");

			Worksheet tabSheet = workbook.getWorksheets()
					.get("Advance Received Amendment");
			/*
			 * tabSheet.setSelected(true);
			 * tabSheet.setTabColor(Color.getBlue());
			 */
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromATA, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromATA.size(), true, "yyyy-mm-dd", false);
		}

		else {
			workbook.getWorksheets().removeAt("Advance Received Amendment");
		}
		if (responseFromViewTXP != null && responseFromViewTXP.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.summary.tables.txp.report.headers")
					.split(",");

			Worksheet tabSheet = workbook.getWorksheets()
					.get("GSTN_Advance Adjusted");
			/*
			 * tabSheet.setSelected(true);
			 * tabSheet.setTabColor(Color.getBlue());
			 */
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromViewTXP,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewTXP.size(), true, "yyyy-mm-dd", false);
		}

		else {
			workbook.getWorksheets().removeAt("GSTN_Advance Adjusted");
		}
		if (responseFromViewTXPA != null && responseFromViewTXPA.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.summary.tables.txpa.report.headers")
					.split(",");

			Worksheet tabSheet = workbook.getWorksheets()
					.get("Advance Adjusted Amendment");
			/*
			 * tabSheet.setSelected(true);
			 * tabSheet.setTabColor(Color.getBlue());
			 */
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromViewTXPA,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewTXPA.size(), true, "yyyy-mm-dd", false);
		}

		else {
			workbook.getWorksheets().removeAt("Advance Adjusted Amendment");
		}

		if (responseFromViewNil != null && responseFromViewNil.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.summary.tables.nil.report.headers")
					.split(",");
			Worksheet tabSheet = workbook.getWorksheets()
					.get("GSTN_NIL NON Exempt");
			/*
			 * tabSheet.setSelected(true);
			 * tabSheet.setTabColor(Color.getBlue());
			 */
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromViewNil,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewNil.size(), true, "yyyy-mm-dd", false);
		}

		else {
			workbook.getWorksheets().removeAt("GSTN_NIL NON Exempt");
		}

		if (responseFromViewHsn != null && responseFromViewHsn.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp(
							"gstr1.gstn.summary.rate.tables.hsn.report.headers")
					.split(",");
			Worksheet tabSheet = workbook.getWorksheets()
					.get("GSTN_HSN Summary");
			/*
			 * tabSheet.setSelected(true);
			 * tabSheet.setTabColor(Color.getBlue());
			 */
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromViewHsn,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewHsn.size(), true, "yyyy-mm-dd", false);
		}

		else {
			workbook.getWorksheets().removeAt("GSTN_HSN Summary");
		}
		if (responseFromViewInv != null && responseFromViewInv.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.gstn.summary.tables.inv.report.headers")
					.split(",");

			Worksheet tabSheet = workbook.getWorksheets()
					.get("GSTN_Invoice Series");
			/*
			 * tabSheet.setSelected(true);
			 * tabSheet.setTabColor(Color.getBlue());
			 */
			Cells errorDumpCells = tabSheet.getCells();
			errorDumpCells.importCustomObjects(responseFromViewInv,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewInv.size(), true, "yyyy-mm-dd", false);
		}

		else {
			workbook.getWorksheets().removeAt("GSTN_Invoice Series");
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
