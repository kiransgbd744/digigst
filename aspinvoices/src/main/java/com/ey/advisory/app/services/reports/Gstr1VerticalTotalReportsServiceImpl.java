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
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * 
 * @author Sujith.Nanga
 *
 */
	@Service("Gstr1VerticalTotalReportsServiceImpl")
	public class Gstr1VerticalTotalReportsServiceImpl
			implements Gstr1VerticalReportsService {

		private static final Logger LOGGER = LoggerFactory
				.getLogger(Gstr1VerticalTotalReportsServiceImpl.class);

		@Autowired
		CommonUtility commonUtility;

		@Autowired
		@Qualifier("Gstr1VerticalTotalReportsDaoImpl")
		private Gstr1VerticalReportsDao gstr1VerticalReportsDao;

		@Override
		public Workbook downloadReports(SearchCriteria criteria,
				PageRequest pageReq) {

			Gstr1VerticalDownloadReportsReqDto request = 
					(Gstr1VerticalDownloadReportsReqDto) criteria;
			Workbook workbook = new Workbook();
			int startRow = 1;
			int startcolumn = 0;
			boolean isHeaderRequired = false;
			List<Object> responseFromView = new ArrayList<>();
			responseFromView = gstr1VerticalReportsDao
					.getGstr1VerticalReports(request);

			LOGGER.debug("Gstr1 vertical total response" + responseFromView);

			if (responseFromView != null && !responseFromView.isEmpty()) {

				if ((DownloadReportsConstant.B2CS)
						.equalsIgnoreCase(request.getFileType())) {

					String[] invoiceHeaders = commonUtility
							.getProp("gstr1.vertical.b2c.total.report.headers")
							.split(",");
					LOGGER.debug("workbook properties reading starting ");

					workbook = createWorkbookWithExcelTemplate("ReportTemplates",
							"Gstr1_B2CVerticalTotal.xlsx");

					LOGGER.debug("workbook reading ending");

					Cells errorDumpCells = workbook.getWorksheets().get(0)
							.getCells();
					errorDumpCells.importCustomObjects(responseFromView,
							invoiceHeaders, isHeaderRequired, startRow, startcolumn,
							responseFromView.size(), true, "yyyy-mm-dd", false);

				} else if ((DownloadReportsConstant.ADVANCERECEIVED)
						.equalsIgnoreCase(request.getFileType())) {
					String[] invoiceHeaders = commonUtility
							.getProp("gstr1.vertical.advrec.total.report.headers")
							.split(",");

					LOGGER.debug("workbook properties reading starting ");

					workbook = createWorkbookWithExcelTemplate("ReportTemplates",
							"Gstr1_AdvRecVerticalTotalReport.xlsx");

					LOGGER.debug("workbook reading ending");

					Cells errorDumpCells = workbook.getWorksheets().get(0)
							.getCells();
					errorDumpCells.importCustomObjects(responseFromView,
							invoiceHeaders, isHeaderRequired, startRow, startcolumn,
							responseFromView.size(), true, "yyyy-mm-dd", false);

				} else if ((DownloadReportsConstant.ADVANCEADJUSTMENT)
						.equalsIgnoreCase(request.getFileType())) {
					String[] invoiceHeaders = commonUtility
							.getProp("gstr1.vertical.advadj.total.report.headers")
							.split(",");
					LOGGER.debug("workbook properties reading starting ");

					workbook = createWorkbookWithExcelTemplate("ReportTemplates",
							"Gstr1_AdvAdjVerticalTotalReport.xlsx");

					LOGGER.debug("workbook reading ending");

					Cells errorDumpCells = workbook.getWorksheets().get(0)
							.getCells();
					errorDumpCells.importCustomObjects(responseFromView,
							invoiceHeaders, isHeaderRequired, startRow, startcolumn,
							responseFromView.size(), true, "yyyy-mm-dd", false);

				} else if ((DownloadReportsConstant.INVOICE)
						.equalsIgnoreCase(request.getFileType())) {
					String[] invoiceHeaders = commonUtility
							.getProp("gstr1.vertical.invoice.total.report.headers")
							.split(",");
					LOGGER.debug("workbook properties reading starting ");

					workbook = createWorkbookWithExcelTemplate("ReportTemplates",
							"Gstr1_InvoiceVerticalTotalReport.xlsx");

					LOGGER.debug("workbook reading ending");

					Cells errorDumpCells = workbook.getWorksheets().get(0)
							.getCells();
					errorDumpCells.importCustomObjects(responseFromView,
							invoiceHeaders, isHeaderRequired, startRow, startcolumn,
							responseFromView.size(), true, "yyyy-mm-dd", false);
				}
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


