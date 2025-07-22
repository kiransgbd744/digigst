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
import org.springframework.transaction.annotation.Transactional;

import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Service("Gstr2AConsolidatedServiceImpl")
public class Gstr2AConsolidatedServiceImpl
		implements Gstr2AspErrorReportsService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2AConsolidatedServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr2AconsolidatedReportDaoImpl")
	private Gstr2AconsolidatedReportDaoImpl gstr2AconsolidatedReportDaoImpl;

	@Transactional(value = "clientTransactionManager")
	@Override
	public Workbook findError(SearchCriteria criteria,
			PageRequest pageReq) {
		Gstr2ProcessedRecordsReqDto request = (Gstr2ProcessedRecordsReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		List<Object> responseFromView = new ArrayList<>();
		responseFromView = gstr2AconsolidatedReportDaoImpl
				.getGstr1RSReports(request);

		LOGGER.debug(
				"Gstr1 processed data item level response" + responseFromView);

		if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr2a.new.get.report.headers").split(",");
			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"Gstr2Get2AConsolidated.xlsx");
			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			workbook.getWorksheets().get(0).getCells().convertStringToNumericValue();
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
