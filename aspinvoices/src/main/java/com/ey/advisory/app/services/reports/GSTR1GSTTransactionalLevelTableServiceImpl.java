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
	@Service("GSTR1GSTTransactionalLevelTableServiceImpl")
	public class GSTR1GSTTransactionalLevelTableServiceImpl
			implements Gstr1GstnErrorReportService {

		private static final Logger LOGGER = LoggerFactory
				.getLogger(GSTR1GSTTransactionalLevelTableServiceImpl.class);

		@Autowired
		CommonUtility commonUtility;

		@Autowired
		@Qualifier("GSTR1GSTTransactionalLevelTablesDaoImpl")
		private Gstr1GstnErrorDao gstr1GstnTransDao;

		@Override
		public Workbook findGstnErrorReports(SearchCriteria criteria,
				PageRequest pageReq) {

			Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

			Workbook workbook = new Workbook();
			int startRow = 1;
			int startcolumn = 0;
			boolean isHeaderRequired = false;

			List<Object> responseFromTrans = new ArrayList<>();
			responseFromTrans = gstr1GstnTransDao
					.getGstr1GstnErrorReport(request);

			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"GSTR-1_Transactional_Level_Tables_Saved_Submitted.xlsx");

			if (responseFromTrans != null && responseFromTrans.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp("gstr1.gst.trans.saved.report.headers")
						.split(",");

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

