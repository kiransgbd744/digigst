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

	@Service("AspB2BSectionReportReportServiceImpl")
	public class AspB2BSectionReportReportServiceImpl
			implements Gstr1ReviewSummReportsService {

		private static final Logger LOGGER = LoggerFactory
				.getLogger(AspB2BSectionReportReportServiceImpl.class);

		@Autowired
		CommonUtility commonUtility;

		@Autowired
		@Qualifier("AspB2BSectionReportDaoImpl")
		private Gstr1ReviewSummaryReportsDao gstr1ReviewSummaryReportsDao;

		@Override
		public Workbook findGstr1ReviewSummRecords(SearchCriteria criteria,
				PageRequest pageReq) {
			Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
			Workbook workbook = new Workbook();
			int startRow = 2;
			int startcolumn = 0;
			boolean isHeaderRequired = false;
			List<Object> responseFromView = new ArrayList<>();
			responseFromView = gstr1ReviewSummaryReportsDao
					.getGstr1RSReports(request);

			LOGGER.debug("Gstr1 processed data item level response" + responseFromView);

			if (responseFromView != null && responseFromView.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp("anx1.api.new.report.headers").split(",");
				workbook = createWorkbookWithExcelTemplate("ReportTemplates",
						"Gstr1_Asp_Processed_TransactionalReport.xlsx");
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

