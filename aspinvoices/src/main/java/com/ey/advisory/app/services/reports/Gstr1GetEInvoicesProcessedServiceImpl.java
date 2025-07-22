package com.ey.advisory.app.services.reports;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.views.client.GSTR1GetEInvoicesTablesDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1EInvReportsReqDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Gstr1GetEInvoicesProcessedServiceImpl")
public class Gstr1GetEInvoicesProcessedServiceImpl
		implements Gstr1GetEInvoicesProcessedReportsService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1GetEInvoicesProcessedServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr1GetEInvoicesReportDaoImpl")
	private Gstr1GetEInvoicesReportsDao gstr1GetEInvoicesReportDao;

	@Transactional(value = "clientTransactionManager")
	@Override
	public Workbook findGstr1GetEInvoicesRecords(SearchCriteria criteria,
			PageRequest pageReq) {
		Gstr1EInvReportsReqDto request = (Gstr1EInvReportsReqDto) criteria;
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		List<GSTR1GetEInvoicesTablesDto> responseFromView = new ArrayList<>();
		responseFromView = gstr1GetEInvoicesReportDao
				.getGstr1EIReports(request);
		Workbook workbook = new Workbook();
		/*
		 * List<GSTR1GetEInvoicesTablesDto> responseFromView = Lists
		 * .newLinkedList(); reqDtoCriterias.forEach(dto -> {
		 * List<GSTR1GetEInvoicesTablesDto> data = gstr1GetEInvoicesReportDao
		 * .getGstr1EIReports(dto); if (CollectionUtils.isNotEmpty(data)) {
		 * responseFromView.addAll(data); } });
		 */

		LOGGER.debug("Gstr1 Get EInvoices data item level response"
				+ responseFromView);

		if (CollectionUtils.isNotEmpty(responseFromView)) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.einvoices.report.headers").split(",");
			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"Gstr1_E_Invoice_Records.xlsx");
			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromView, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromView.size(), true, "yyyy-mm-dd", false);
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
