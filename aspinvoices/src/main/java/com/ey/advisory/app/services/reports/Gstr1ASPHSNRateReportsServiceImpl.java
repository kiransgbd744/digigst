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
import com.ey.advisory.app.data.views.client.Gstr1AspVerticalHsnDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 */
	
	@Service("Gstr1ASPHSNRateReportsServiceImpl")
	public class Gstr1ASPHSNRateReportsServiceImpl
			implements Gstr1ASPHsnSummaryReportsService {

		private static final Logger LOGGER = LoggerFactory
				.getLogger(Gstr1ASPHSNRateReportsServiceImpl.class);

		@Autowired
		CommonUtility commonUtility;

		@Autowired
		@Qualifier("Gstr1AspHsnRateDaoImpl")
		Gstr1AspHsnRateDaoImpl gstr1AspHsnSummaryDao;

		@Override
		public Workbook findReports(SearchCriteria criteria, PageRequest pageReq) {

			Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
			Workbook workbook = new Workbook();
			int startRow = 2;
			int startcolumn = 0;
			boolean isHeaderRequired = false;
			List<Gstr1AspVerticalHsnDto> responseFromHsnSummary = new ArrayList<>();
			responseFromHsnSummary = gstr1AspHsnSummaryDao
					.getGstr1RSReports(request);
			for(Gstr1AspVerticalHsnDto dto:responseFromHsnSummary){
				dto.setSerialNo(String.valueOf(responseFromHsnSummary.indexOf(dto)+1));
			}
			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"GSTR1_Rate_HSN_Summary_Savable.xlsx");
			if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr1 Asp Hsn Savable data response" + responseFromHsnSummary);
			}
			if (responseFromHsnSummary != null
					&& responseFromHsnSummary.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp("gstr1.Asp.vertical.hsn.rate.process.report.headers")
						.split(",");

				Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
				errorDumpCells.importCustomObjects(responseFromHsnSummary,
						invoiceHeaders, isHeaderRequired, startRow, startcolumn,
						responseFromHsnSummary.size(), true, "yyyy-mm-dd", false);
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

