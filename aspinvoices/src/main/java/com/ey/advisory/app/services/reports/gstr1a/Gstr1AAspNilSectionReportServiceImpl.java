package com.ey.advisory.app.services.reports.gstr1a;

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

	@Service("Gstr1AAspNilSectionReportServiceImpl")
	public class Gstr1AAspNilSectionReportServiceImpl
			implements Gstr1AAspNilRatedReportsService {

		private static final Logger LOGGER = LoggerFactory
				.getLogger(Gstr1AAspNilSectionReportServiceImpl.class);

		@Autowired
		CommonUtility commonUtility;

		@Autowired
		@Qualifier("Gstr1AAspNilSecRatedTransactionalDaoImpl")
		Gstr1AOutwardVerticalProcessNilDao gstr1NilRatedTransactionalDao;

		@Autowired
		@Qualifier("Gstr1AAspSecRatedTotalSummaryDaoImpl")
		Gstr1AOutwardVerticalProcessNilDao gstr1NilRatedTotalSummDao;
		
		@Autowired
		@Qualifier("Gstr1AAspNilRatedSummaryUploadDaoImpl")
		Gstr1AOutwardVerticalProcessNilDao gstr1NilRatedUploadSummDao;

		@Override
		public Workbook findNilReport(SearchCriteria criteria, PageRequest pageReq) {

			Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
			Workbook workbook = new Workbook();
			int startRow = 2;
			int startcolumn = 0;
			boolean isHeaderRequired = false;

			List<Object> responseFromNilRatedTotalSumm = new ArrayList<>();
			responseFromNilRatedTotalSumm = gstr1NilRatedTotalSummDao
					.getGstr1RSReports(request);

			List<Object> responseFromNilTransactional = new ArrayList<>();
			responseFromNilTransactional = gstr1NilRatedTransactionalDao
					.getGstr1RSReports(request);
			
			List<Object> responseFromUpload = new ArrayList<>();
			responseFromUpload = gstr1NilRatedUploadSummDao
					.getGstr1RSReports(request);


			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"GSTR1_ASP_NIL_EXEMPT_NON_Savable.xlsx");

			LOGGER.debug("Gstr1 Asp Nil Savable data response"
					+ responseFromNilRatedTotalSumm);

			if (responseFromNilRatedTotalSumm != null
					&& responseFromNilRatedTotalSumm.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp("gstr1.Asp.Nil.Exp.report.headers")
						.split(",");

				Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
				errorDumpCells.importCustomObjects(responseFromNilRatedTotalSumm,
						invoiceHeaders, isHeaderRequired, startRow, startcolumn,
						responseFromNilRatedTotalSumm.size(), true, "yyyy-mm-dd",
						false);
			}

			if (responseFromNilTransactional != null
					&& responseFromNilTransactional.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp("anx1.api.new.report.headers")
						.split(",");

				Cells errorDumpCells = workbook.getWorksheets().get(1).getCells();
				errorDumpCells.importCustomObjects(responseFromNilTransactional,
						invoiceHeaders, isHeaderRequired, startRow, startcolumn,
						responseFromNilTransactional.size(), true, "yyyy-mm-dd",
						false);
			}
			
			if (responseFromUpload != null
					&& responseFromUpload.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp("gstr1.Asp.Nil.upload.report.headers")
						.split(",");

				Cells errorDumpCells = workbook.getWorksheets().get(2).getCells();
				errorDumpCells.importCustomObjects(responseFromUpload,
						invoiceHeaders, isHeaderRequired, startRow, startcolumn,
						responseFromUpload.size(), true, "yyyy-mm-dd",
						false);
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
