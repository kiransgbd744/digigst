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

@Service("Anx1ProcessedVerticalServiceImpl")
public class Anx1ProcessedVerticalServiceImpl
		implements Anx1ProcessedVerticalService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ProcessedVerticalServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Anx1ProcessedB2cVerticalDaoImpl")
	private Anx1ProcessedVerticalDao anx1ProcessedB2cVerticalDao;

	@Autowired
	@Qualifier("Anx1Processed3H3IVerticalDaoImpl")
	private Anx1ProcessedVerticalDao anx1Processed3H3IVerticalDao;

	@Autowired
	@Qualifier("Anx1ProcessedTable4VerticalDaoImpl")
	private Anx1ProcessedVerticalDao anx1ProcessedTable4VerticalDao;

	@Override
	public Workbook findProcessedVertical(SearchCriteria criteria,
			PageRequest pageReq) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Object> responseFromViewB2c = new ArrayList<>();
		responseFromViewB2c = anx1ProcessedB2cVerticalDao
				.getAnx1ProcessedVerticalDaoReport(request);

		List<Object> responseFromView3h3i = new ArrayList<>();
		responseFromView3h3i = anx1Processed3H3IVerticalDao
				.getAnx1ProcessedVerticalDaoReport(request);

		List<Object> responseFromViewtable4 = new ArrayList<>();
		responseFromViewtable4 = anx1ProcessedTable4VerticalDao
				.getAnx1ProcessedVerticalDaoReport(request);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"ANX-1_ASP_Processed Records_Vertical.xlsx");

		LOGGER.debug("Anx1 Asp Processed Vertical data response"
				+ responseFromViewB2c);

		if (responseFromViewB2c != null && responseFromViewB2c.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.Asp.b2c.vertical.process.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromViewB2c,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewB2c.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromView3h3i != null && responseFromView3h3i.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.Asp.3h3i.vertical.process.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(1).getCells();
			errorDumpCells.importCustomObjects(responseFromView3h3i,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromView3h3i.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromViewtable4 != null
				&& responseFromViewtable4.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.Asp.table4.vertical.process.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(2).getCells();
			errorDumpCells.importCustomObjects(responseFromViewtable4,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewtable4.size(), true, "yyyy-mm-dd", false);
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
