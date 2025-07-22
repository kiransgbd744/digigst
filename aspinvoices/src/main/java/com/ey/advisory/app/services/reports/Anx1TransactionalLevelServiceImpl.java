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

@Service("Anx1TransactionalLevelServiceImpl")
public class Anx1TransactionalLevelServiceImpl
		implements Anx1TransacttionalLevelService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1TransactionalLevelServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Anx1TransLevelSummaryDaoImpl")
	private Anx1TransLevelSummaryDao anx1TransLevelSummaryDao;

	@Override
	public Workbook findTransLevelSummary(SearchCriteria criteria,
			PageRequest pageReq) {

		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Object> responseFromTrans = new ArrayList<>();
		responseFromTrans = anx1TransLevelSummaryDao
				.getAnx1TransReport(request);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"ANX-1_GSTN_Transactional Level Tables Saved_Submitted.xlsx");

		if (responseFromTrans != null && responseFromTrans.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.trans.saved.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromTrans,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromTrans.size(), true, "yyyy-mm-dd", false);
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
