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
import com.ey.advisory.app.data.repositories.client.customisedreport.CustomisedFieldSelRepo;
import com.ey.advisory.app.report.convertor.AspSyncCustomisedReportConvertor;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Service("Gstr1AdvRecSecServiceImpl")
public class Gstr1AdvRecSecServiceImpl implements Gstr1ASPAdvRecSavableService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1AdvRecSecServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr1AdvRecSecTotalSummaryDaoImpl")
	private Gstr1AdvRecSavableDao gstr1AdvRectotalDao;

	@Autowired
	@Qualifier("Gstr1AdvRecSecSummaryDaoImpl")
	private Gstr1AdvRecSavableDao gstr1AdvRecsummarySDao;

	@Autowired
	@Qualifier("Gstr1ASPAdvRecSavableTransactionalDaoImpl")
	private Gstr1AdvRecSavableDao gstr1AdvRectransactionalDao;

	@Autowired
	CustomisedFieldSelRepo custFieldSeleRepo;

	@Autowired
	@Qualifier("AspSyncCustomisedReportConvertor")
	AspSyncCustomisedReportConvertor aspUploadReptConverter;

	@Override
	public Workbook findGstr1AdvRecSavableReports(SearchCriteria criteria,
			PageRequest pageReq) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Object> responseFromViewTotal = new ArrayList<>();
		responseFromViewTotal = gstr1AdvRectotalDao
				.getGstr1AdvRecSavableReports(request);
		List<Object> responseFromViewSummary = new ArrayList<>();
		responseFromViewSummary = gstr1AdvRecsummarySDao
				.getGstr1AdvRecSavableReports(request);

		Long entityId = request.getEntityId().get(0);

//		List<DataStatusEinvoiceDto> responseFromViewObj = new ArrayList<>();

		List<Object> responseFromViewTrans = gstr1AdvRectransactionalDao
				.getGstr1AdvRecSavableReports(request);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"GSTR-1_ASP_Advance_Received_Savable.xlsx");
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1 Asp B2CS Savable data response"
					+ responseFromViewTotal);
		}
		if (responseFromViewTotal != null && responseFromViewTotal.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.Asp.vertical.adv.process.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromViewTotal,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewTotal.size(), true, "yyyy-mm-dd", false);
		}

//		List<CustomisedFieldSelEntity> isActiveFieldSel = custFieldSeleRepo
//				.findByEntityIdAndReportTypeAndIsActiveTrue(entityId,
//						APIConstants.GSTR1_CUST_INV_REPORT);

		if (responseFromViewTrans != null && responseFromViewTrans.size() > 0) {

//			if (isActiveFieldSel == null || isActiveFieldSel.isEmpty()) {

				String msg = String
						.format("B2CS Gstr1_Asp_Processed_TransactionalReport :"
								+ " No Active column List found for Entity Id %s  "
								+ "and ReportType 'GSTR1_TRANS_LEVEL' Generating "
								+ "Default Report ", entityId);
				LOGGER.debug(msg);
				String[] invoiceCols = commonUtility
						.getProp("anx1.api.advrecadj.report.headers")
						.split(",");

				Cells errorDumpCells = workbook.getWorksheets().get(1)
						.getCells();
				startRow = 2;
				errorDumpCells.importCustomObjects(responseFromViewTrans,
						invoiceCols, isHeaderRequired, startRow, startcolumn,
						responseFromViewTrans.size(), true, "yyyy-mm-dd",
						false);

//			} else {
//
//				List<String> selectedList = Arrays.asList(
//						isActiveFieldSel.get(0).getHeaderMapping().split(","));
//
//				Map<String, Object> ap = new HashMap<>();
//				ap.put("selectedList", selectedList);
//
//				String[] invoiceHeaders = selectedList
//						.toArray(new String[selectedList.size()]);
//
//				responseFromViewObj = responseFromViewTrans.stream()
//						.map(o -> (DataStatusEinvoiceDto) aspUploadReptConverter
//								.convert(ap, (DataStatusEinvoiceDto) o))
//						.collect(Collectors.toCollection(ArrayList::new));
//				startRow = 2;
//				Cells errorDumpCells = workbook.getWorksheets().get(1)
//						.getCells();
//
//				workbook.getWorksheets().get(1).getCells().deleteRow(1);
//
//				errorDumpCells.importArray(invoiceHeaders, 1, 0, false);
//
//				List<String> selectedColumnList = Arrays.asList(
//						isActiveFieldSel.get(0).getJavaMapp().split(","));
//				String[] invoiceColumns = selectedColumnList
//						.toArray(new String[selectedList.size()]);
//
//				errorDumpCells.importCustomObjects(responseFromViewObj,
//						invoiceColumns, false, startRow, startcolumn,
//						responseFromViewObj.size(), true, "yyyy-mm-dd", false);
//
//			}
		}

		if (responseFromViewSummary != null
				&& responseFromViewSummary.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.Asp.vertical.adv.process.report.headers")
					.split(",");
			startRow = 1;
			Cells errorDumpCells = workbook.getWorksheets().get(2).getCells();
			errorDumpCells.importCustomObjects(responseFromViewSummary,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewSummary.size(), true, "yyyy-mm-dd", false);
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
