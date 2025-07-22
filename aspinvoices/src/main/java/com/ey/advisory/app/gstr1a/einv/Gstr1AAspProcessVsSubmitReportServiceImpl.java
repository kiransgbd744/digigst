package com.ey.advisory.app.gstr1a.einv;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.services.reports.gstr1a.Gstr1AAspProcessVsSubmitDao;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 * 
 */

@Service("Gstr1AAspProcessVsSubmitReportServiceImpl")
@Slf4j
public class Gstr1AAspProcessVsSubmitReportServiceImpl
		implements Gstr1AAspProcessVsSubmitReportService {

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr1AAspProcessVsSubmitSummaryReportDaoImpl")
	private Gstr1AAspProcessVsSubmitDao aspProcessVsSubmitSummaryReportDao;

	@Autowired
	@Qualifier("Gstr1AAspProcessVsSubmitAditionalGstnReportDaoImpl")
	private Gstr1AAspProcessVsSubmitDao aspProcessVsSubmitAditionalGstnReportDao;

	@Autowired
	@Qualifier("Gstr1AAspProcessVsSubmitAditionalDigiGstReportDaoImpl")
	private Gstr1AAspProcessVsSubmitDao aspProcessVsSubmitAditionalDigiGstReportDao;

	@Autowired
	@Qualifier("Gstr1AAspProcessVsSubmitB2csReportDaoImpl")
	private Gstr1AAspProcessVsSubmitDao aspProcessVsSubmitB2csReportDao;

	@Autowired
	@Qualifier("Gstr1AAspProcessVsSubmitB2csaReportDaoImpl")
	private Gstr1AAspProcessVsSubmitDao aspProcessVsSubmitB2csaReportDao;

	@Autowired
	@Qualifier("Gstr1AAspProcessVsSubmitAtReportDaoImpl")
	private Gstr1AAspProcessVsSubmitDao aspProcessVsSubmitAtReportDao;

	@Autowired
	@Qualifier("Gstr1AAspProcessVsSubmitAtAReportDaoImpl")
	private Gstr1AAspProcessVsSubmitDao aspProcessVsSubmitAtAReportDao;

	@Autowired
	@Qualifier("Gstr1AAspProcessVsSubmitTxpdReportDaoImpl")
	private Gstr1AAspProcessVsSubmitDao aspProcessVsSubmitTxpdReportDao;

	@Autowired
	@Qualifier("Gstr1AAspProcessVsSubmitTxpdaReportDaoImpl")
	private Gstr1AAspProcessVsSubmitDao aspProcessVsSubmitTxpdaReportDao;

	@Autowired
	@Qualifier("Gstr1AAspProcessVsSubmitHsnReportDaoImpl")
	private Gstr1AAspProcessVsSubmitDao aspProcessVsSubmitHsnReportDao;

	@Autowired
	@Qualifier("Gstr1AAspProcessVsSubmitNilNonExmptReportDaoImpl")
	private Gstr1AAspProcessVsSubmitDao aspProcessVsSubmitNilNonExmptReportDao;

	@Autowired
	@Qualifier("Gstr1AAspProcessVsSubmitDocSeriesReportDaoImpl")
	private Gstr1AAspProcessVsSubmitDao aspProcessVsSubmitDocSeriesReportDao;

	@Autowired
	@Qualifier("Gstr1AAspProcessVsSubmitTable14ReportDaoImpl")
	private Gstr1AAspProcessVsSubmitDao aspProcessVsSubmitTable14ReportDao;
	
	@Autowired
	@Qualifier("Gstr1AAspProcessVsSubmitTable15ReportDaoImpl")
	private Gstr1AAspProcessVsSubmitDao aspProcessVsSubmitTable15ReportDao;
	
	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	EntityInfoDetailsRepository repo;

	@Override
	public Workbook AspProcessVsSubmitReports(SearchCriteria criteria,
			PageRequest pageReq) {

		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
		Workbook workbook = new Workbook();
		// int startRow = 2;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Object> responseFromSubmit = new ArrayList<>();
		responseFromSubmit = aspProcessVsSubmitSummaryReportDao
				.aspProcessVsSubmitDaoReports(request);

		List<Object> responseFromAdd = new ArrayList<>();
		responseFromAdd = aspProcessVsSubmitAditionalGstnReportDao
				.aspProcessVsSubmitDaoReports(request);

		List<Object> responseFromadddigi = new ArrayList<>();
		responseFromadddigi = aspProcessVsSubmitAditionalDigiGstReportDao
				.aspProcessVsSubmitDaoReports(request);

		List<Object> responseFromB2cs = new ArrayList<>();
		responseFromB2cs = aspProcessVsSubmitB2csReportDao
				.aspProcessVsSubmitDaoReports(request);

		List<Object> responseFromB2csa = new ArrayList<>();
		responseFromB2csa = aspProcessVsSubmitB2csaReportDao
				.aspProcessVsSubmitDaoReports(request);

		List<Object> responseFromAt = new ArrayList<>();
		responseFromAt = aspProcessVsSubmitAtReportDao
				.aspProcessVsSubmitDaoReports(request);

		List<Object> responseFromAtA = new ArrayList<>();
		responseFromAtA = aspProcessVsSubmitAtAReportDao
				.aspProcessVsSubmitDaoReports(request);

		List<Object> responseFromTxp = new ArrayList<>();
		responseFromTxp = aspProcessVsSubmitTxpdReportDao
				.aspProcessVsSubmitDaoReports(request);

		List<Object> responseFromTxpa = new ArrayList<>();
		responseFromTxpa = aspProcessVsSubmitTxpdaReportDao
				.aspProcessVsSubmitDaoReports(request);

		List<Object> responseFromHsn = new ArrayList<>();
		responseFromHsn = aspProcessVsSubmitHsnReportDao
				.aspProcessVsSubmitDaoReports(request);

		List<Object> responseFromNil = new ArrayList<>();
		responseFromNil = aspProcessVsSubmitNilNonExmptReportDao
				.aspProcessVsSubmitDaoReports(request);

		List<Object> responseFromDoc = new ArrayList<>();
		responseFromDoc = aspProcessVsSubmitDocSeriesReportDao
				.aspProcessVsSubmitDaoReports(request);
		
		List<Object> responseFrom14 = new ArrayList<>();
		responseFrom14 = aspProcessVsSubmitTable14ReportDao
				.aspProcessVsSubmitDaoReports(request);
		
		List<Object> responseFrom15 = new ArrayList<>();
		responseFrom15 = aspProcessVsSubmitTable15ReportDao
				.aspProcessVsSubmitDaoReports(request);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"AspSubmitted.xlsx");

		String taxPeriodFrom = request.getTaxPeriodFrom();
		String taxPeriodTo = request.getTaxPeriodTo();
		List<Long> entityId = request.getEntityId();
		String genOfTimeAndDate = request.getGenOfTimeAndDate();
		String entityName = "";
		if (entityId != null) {
			entityName = repo.entityNameById(entityId);
		}
		for(int i = 0 ; i < 14 ; i++){
			Cells errorDumpCells = workbook.getWorksheets().get(i).getCells();
			errorDumpCells.get("B2").setValue(entityName);
			errorDumpCells.get("B3").setValue(taxPeriodFrom);
			errorDumpCells.get("B4").setValue(genOfTimeAndDate);
			errorDumpCells.get("D3").setValue(taxPeriodTo);
		}

		if (responseFromSubmit != null && responseFromSubmit.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp(
							"asp.processed.submit.tables.summary.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			/*errorDumpCells.get("B2").setValue(entityName);
			errorDumpCells.get("B3").setValue(taxPeriodFrom);
			errorDumpCells.get("B4").setValue(genOfTimeAndDate);
			errorDumpCells.get("D3").setValue(taxPeriodTo);*/

			errorDumpCells.importCustomObjects(responseFromSubmit,
					invoiceHeaders, isHeaderRequired, 7, startcolumn,
					responseFromSubmit.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromAdd != null && responseFromAdd.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp(
							"asp.processed.submit.tables.aditionalgstn.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(1).getCells();
			/*errorDumpCells.get("B2").setValue(entityName);
			errorDumpCells.get("B3").setValue(taxPeriodFrom);
			errorDumpCells.get("B4").setValue(genOfTimeAndDate);
			errorDumpCells.get("D3").setValue(taxPeriodTo);*/
			errorDumpCells.importCustomObjects(responseFromAdd, invoiceHeaders,
					isHeaderRequired, 6, startcolumn, responseFromAdd.size(),
					true, "yyyy-mm-dd", false);
		}

		if (responseFromadddigi != null && responseFromadddigi.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp(
							"asp.processed.submit.tables.aditionaldigigst.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(2).getCells();
			/*errorDumpCells.get("B2").setValue(entityName);
			errorDumpCells.get("B3").setValue(taxPeriodFrom);
			errorDumpCells.get("B4").setValue(genOfTimeAndDate);
			errorDumpCells.get("D3").setValue(taxPeriodTo);*/
			errorDumpCells.importCustomObjects(responseFromadddigi,
					invoiceHeaders, isHeaderRequired, 6, startcolumn,
					responseFromadddigi.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromB2cs != null && responseFromB2cs.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("asp.processed.submit.tables.b2cs.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(3).getCells();
			/*errorDumpCells.get("B2").setValue(entityName);
			errorDumpCells.get("B3").setValue(taxPeriodFrom);
			errorDumpCells.get("B4").setValue(genOfTimeAndDate);
			errorDumpCells.get("D3").setValue(taxPeriodTo);*/

			errorDumpCells.importCustomObjects(responseFromB2cs, invoiceHeaders,
					isHeaderRequired, 7, startcolumn, responseFromB2cs.size(),
					true, "yyyy-mm-dd", false);
		}

		if (responseFromB2csa != null && responseFromB2csa.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("asp.processed.submit.tables.b2csa.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(4).getCells();
			/*errorDumpCells.get("B2").setValue(entityName);
			errorDumpCells.get("B3").setValue(taxPeriodFrom);
			errorDumpCells.get("B4").setValue(genOfTimeAndDate);
			errorDumpCells.get("D3").setValue(taxPeriodTo);*/

			errorDumpCells.importCustomObjects(responseFromB2csa,
					invoiceHeaders, isHeaderRequired, 7, startcolumn,
					responseFromB2csa.size(), true, "yyyy-mm-dd", false);
		}
		if (responseFromAt != null && responseFromAt.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("asp.processed.submit.tables.at.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(5).getCells();
			/*errorDumpCells.get("B2").setValue(entityName);
			errorDumpCells.get("B3").setValue(taxPeriodFrom);
			errorDumpCells.get("B4").setValue(genOfTimeAndDate);
			errorDumpCells.get("D3").setValue(taxPeriodTo);*/

			errorDumpCells.importCustomObjects(responseFromAt, invoiceHeaders,
					isHeaderRequired, 7, startcolumn, responseFromAt.size(),
					true, "yyyy-mm-dd", false);
		}

		if (responseFromAtA != null && responseFromAtA.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("asp.processed.submit.tables.ata.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(6).getCells();
			/*errorDumpCells.get("B2").setValue(entityName);
			errorDumpCells.get("B3").setValue(taxPeriodFrom);
			errorDumpCells.get("B4").setValue(genOfTimeAndDate);
			errorDumpCells.get("D3").setValue(taxPeriodTo);*/

			errorDumpCells.importCustomObjects(responseFromAtA, invoiceHeaders,
					isHeaderRequired, 7, startcolumn, responseFromAtA.size(),
					true, "yyyy-mm-dd", false);
		}

		if (responseFromTxp != null && responseFromTxp.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.saved.asp.advadj.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(7).getCells();
			/*errorDumpCells.get("B2").setValue(entityName);
			errorDumpCells.get("B3").setValue(taxPeriodFrom);
			errorDumpCells.get("B4").setValue(genOfTimeAndDate);
			errorDumpCells.get("D3").setValue(taxPeriodTo);*/

			errorDumpCells.importCustomObjects(responseFromTxp, invoiceHeaders,
					isHeaderRequired, 7, startcolumn, responseFromTxp.size(),
					true, "yyyy-mm-dd", false);
		}

		if (responseFromTxpa != null && responseFromTxpa.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.saved.asp.advadjamen.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(8).getCells();
			/*errorDumpCells.get("B2").setValue(entityName);
			errorDumpCells.get("B3").setValue(taxPeriodFrom);
			errorDumpCells.get("B4").setValue(genOfTimeAndDate);
			errorDumpCells.get("D3").setValue(taxPeriodTo);*/

			errorDumpCells.importCustomObjects(responseFromTxpa, invoiceHeaders,
					isHeaderRequired, 7, startcolumn, responseFromTxpa.size(),
					true, "yyyy-mm-dd", false);
		}

		if (responseFromNil != null && responseFromNil.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.saved.asp.nilnon.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(9).getCells();
			/*errorDumpCells.get("B2").setValue(entityName);
			errorDumpCells.get("B3").setValue(taxPeriodFrom);
			errorDumpCells.get("B4").setValue(genOfTimeAndDate);
			errorDumpCells.get("D3").setValue(taxPeriodTo);*/

			errorDumpCells.importCustomObjects(responseFromNil, invoiceHeaders,
					isHeaderRequired, 7, startcolumn, responseFromNil.size(),
					true, "yyyy-mm-dd", false);
		}

		if (responseFromHsn != null && responseFromHsn.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.saved.asp.hsn.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(10).getCells();
			/*errorDumpCells.get("B2").setValue(entityName);
			errorDumpCells.get("B3").setValue(taxPeriodFrom);
			errorDumpCells.get("B4").setValue(genOfTimeAndDate);
			errorDumpCells.get("D3").setValue(taxPeriodTo);*/

			errorDumpCells.importCustomObjects(responseFromHsn, invoiceHeaders,
					isHeaderRequired, 7, startcolumn, responseFromHsn.size(),
					true, "yyyy-mm-dd", false);
		}
		if (responseFromDoc != null && responseFromDoc.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.saved.asp.doc.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(11).getCells();
			/*errorDumpCells.get("B2").setValue(entityName);
			errorDumpCells.get("B3").setValue(taxPeriodFrom);
			errorDumpCells.get("B4").setValue(genOfTimeAndDate);
			errorDumpCells.get("D3").setValue(taxPeriodTo);*/

			errorDumpCells.importCustomObjects(responseFromDoc, invoiceHeaders,
					isHeaderRequired, 7, startcolumn, responseFromDoc.size(),
					true, "yyyy-mm-dd", false);
		}
		if (responseFrom14 != null && responseFrom14.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("asp.processed.submit.table.14.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(12).getCells();

			errorDumpCells.importCustomObjects(responseFrom14, invoiceHeaders,
					isHeaderRequired, 7, startcolumn, responseFrom14.size(),
					true, "yyyy-mm-dd", false);
		}
		if (responseFrom15 != null && responseFrom15.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("asp.processed.submit.table.15.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(13).getCells();

			errorDumpCells.importCustomObjects(responseFrom15, invoiceHeaders,
					isHeaderRequired, 7, startcolumn, responseFrom15.size(),
					true, "yyyy-mm-dd", false);
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
