/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.javatuples.Pair;
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
import com.ey.advisory.app.data.entities.customisedreport.CustomisedFieldSelEntity;
import com.ey.advisory.app.data.repositories.client.customisedreport.CustomisedFieldSelRepo;
import com.ey.advisory.app.docs.dto.einvoice.DataStatusEinvoiceDto;
import com.ey.advisory.app.report.convertor.AspSyncCustomisedReportConvertor;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Service("Gstr1NilSectionReportServiceImpl")
public class Gstr1NilSectionReportServiceImpl
		implements Gstr1AspNilRatedReportsService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1AspNilRatedReportsServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr1SecRatedTotalSummaryDaoImpl")
	Gstr1OutwardVerticalProcessNilDao gstr1NilRatedTotalSummDao;

	@Autowired
	@Qualifier("Gstr1NilRatedSummaryUploadDaoImpl")
	Gstr1OutwardVerticalProcessNilDao gstr1NilRatedUploadSummDao;

	@Autowired
	@Qualifier("Gstr1NilSecRatedTransactionalDaoImpl")
	private Gstr1ReviewSummaryCustomizedReportsDao gstr1NilRatedTransactionalDao;

	@Autowired
	CustomisedFieldSelRepo custFieldSeleRepo;

	@Autowired
	@Qualifier("AspSyncCustomisedReportConvertor")
	AspSyncCustomisedReportConvertor aspUploadReptConverter;

	@Override
	public Workbook findNilReport(SearchCriteria criteria,
			PageRequest pageReq) {

		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 2;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Object> responseFromNilRatedTotalSumm = new ArrayList<>();
		responseFromNilRatedTotalSumm = gstr1NilRatedTotalSummDao
				.getGstr1RSReports(request);

		Long entityId = request.getEntityId().get(0);
		Pair<List<Object[]>, List<Object>> gstr1rsReports = gstr1NilRatedTransactionalDao
				.getGstr1RSReports(request);

		List<Object> responseFromNilTransactional = gstr1rsReports.getValue1();
		List<DataStatusEinvoiceDto> responseFromViewObj = new ArrayList<>();

		List<Object> responseFromUpload = new ArrayList<>();
		responseFromUpload = gstr1NilRatedUploadSummDao
				.getGstr1RSReports(request);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"GSTR1_ASP_NIL_EXEMPT_NON_Savable.xlsx");

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1 Asp Nil Savable data response"
					+ responseFromNilRatedTotalSumm);
		}

		if (responseFromNilRatedTotalSumm != null
				&& responseFromNilRatedTotalSumm.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.Asp.Nil.Exp.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			/*
			 * if(request.getIsNilUserInput().equalsIgnoreCase("True")){
			 * errorDumpCells.get("A1").
			 * setValue("Last Updated - DigiGST Computed is considered for save to GSTN"
			 * ); }else{ errorDumpCells.get("A1").
			 * setValue("Last Updated - User Edited is considered for save to GSTN"
			 * ); }
			 */
			errorDumpCells.importCustomObjects(responseFromNilRatedTotalSumm,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromNilRatedTotalSumm.size(), true, "yyyy-mm-dd",
					false);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1 processed data item level response"
					+ responseFromNilTransactional.size());
		}

		List<CustomisedFieldSelEntity> isActiveFieldSel = custFieldSeleRepo
				.findByEntityIdAndReportTypeAndIsActiveTrue(entityId,
						APIConstants.GSTR1_CUST_INV_REPORT);

		if (responseFromNilTransactional != null
				&& responseFromNilTransactional.size() > 0) {

			if (isActiveFieldSel == null || isActiveFieldSel.isEmpty()) {

				String msg = String
						.format("B2CS Gstr1_Asp_Processed_TransactionalReport :"
								+ " No Active column List found for Entity Id %s  "
								+ "and ReportType 'GSTR1_TRANS_LEVEL' Generating "
								+ "Default Report ", entityId);
				LOGGER.debug(msg);
				String[] invoiceCols = commonUtility
						.getProp("anx1.api.new.report.headers").split(",");

				String[] invoiceHeadersNum = commonUtility
						.getProp("gsrt1.b2cs.headersNums").split(",");

				String[] invoiceHeaderNames = commonUtility
						.getProp("gsrt1.b2cs.headersNames").split(",");

				Cells errorDumpCells = workbook.getWorksheets().get(1)
						.getCells();

				errorDumpCells.importArray(invoiceHeadersNum, 0, 0, false);
				errorDumpCells.importArray(invoiceHeaderNames, 1, 0, false);
				startRow = 2;
				errorDumpCells.importCustomObjects(responseFromNilTransactional,
						invoiceCols, isHeaderRequired, startRow, startcolumn,
						responseFromNilTransactional.size(), true, "yyyy-mm-dd",
						false);
			} else {

				List<String> selectedList = Arrays.asList(
						isActiveFieldSel.get(0).getHeaderMapping().split(","));

				Map<String, Object> ap = new HashMap<>();
				ap.put("selectedList", selectedList);

				String[] invoiceHeaders = selectedList
						.toArray(new String[selectedList.size()]);

				responseFromViewObj = responseFromNilTransactional.stream()
						.map(o -> (DataStatusEinvoiceDto) aspUploadReptConverter
								.convert(ap, (DataStatusEinvoiceDto) o))
						.collect(Collectors.toCollection(ArrayList::new));
				startRow = 1;
				Cells errorDumpCells = workbook.getWorksheets().get(1)
						.getCells();

				workbook.getWorksheets().get(1).getCells().deleteRow(1);

				errorDumpCells.importArray(invoiceHeaders, 0, 0, false);

				List<String> selectedColumnList = Arrays.asList(
						isActiveFieldSel.get(0).getJavaMapp().split(","));
				String[] invoiceColumns = selectedColumnList
						.toArray(new String[selectedList.size()]);

				errorDumpCells.importCustomObjects(responseFromViewObj,
						invoiceColumns, false, startRow, startcolumn,
						responseFromViewObj.size(), true, "yyyy-mm-dd", false);

			}
		}

		if (responseFromUpload != null && responseFromUpload.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.Asp.Nil.upload.report.headers").split(",");
			isHeaderRequired = false;
			startRow = 2;
			Cells errorDumpCells = workbook.getWorksheets().get(2).getCells();
			errorDumpCells.importCustomObjects(responseFromUpload,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromUpload.size(), true, "yyyy-mm-dd", false);
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
