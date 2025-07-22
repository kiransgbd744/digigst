package com.ey.advisory.app.services.reports;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
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
import com.ey.advisory.app.docs.dto.GstnConsolidatedErrorReqDto;
import com.ey.advisory.app.docs.dto.GstnConsolidatedReqDto;
import com.ey.advisory.app.docs.dto.einvoice.DataStatusEinvoiceDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;


	@Service("GstnConsolidatedErrorReportsServiceImpl")
	public class GstnConsolidatedErrorReportsServiceImpl
			implements GstnConsolidatedReportsService {

		private static final Logger LOGGER = LoggerFactory
				.getLogger(GstnConsolidatedErrorReportsServiceImpl.class);

		@Autowired
		CommonUtility commonUtility;

		@Autowired
		@Qualifier("GstnConsolidatedErrorReportsDaoImpl")
		private GstnConsolidatedReportsDao gstnConsolidatedReportsDao;

		@Override
		public Workbook generateGstnReports(SearchCriteria criteria,
				PageRequest pageReq) {
			GstnConsolidatedErrorReqDto request = (GstnConsolidatedErrorReqDto) criteria;

			Workbook workbook = new Workbook();
			int startRow = 1;
			int startcolumn = 0;
			boolean isHeaderRequired = false;

			List<DataStatusEinvoiceDto> responseFromView = new ArrayList<>();
			responseFromView = gstnConsolidatedReportsDao.getGstnConsolidatedReports(request);

			if (responseFromView != null && responseFromView.size() > 0) {
				String[] invoiceHeaders = commonUtility.getProp("gstr1.api.new.report.headers").split(",");
				workbook = createWorkbookWithExcelTemplate("ReportTemplates", "Data_Status_Report_Download.xlsx");
				Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
				errorDumpCells.importCustomObjects(responseFromView, invoiceHeaders, isHeaderRequired, startRow,
						startcolumn, responseFromView.size(), true, "yyyy-mm-dd", false);
			}
			return workbook;
		}

		private Workbook createWorkbookWithExcelTemplate(String folderName, String fileName) {
			Workbook workbook = null;
			try {
				ClassLoader classLoader = getClass().getClassLoader();
				URL template_Dir = classLoader.getResource(folderName + "/");
				String templatePath = template_Dir.getPath() + fileName;
				LoadOptions options = new LoadOptions(FileFormatType.XLSX);
				CommonUtility.setAsposeLicense();
				workbook = new Workbook(templatePath, options);
				workbook.getSettings().setMemorySetting(MemorySetting.MEMORY_PREFERENCE);
			} catch (Exception ex) {
				LOGGER.error("Exception in creating workbook : ", ex);
			}
			return workbook;
		}

	}