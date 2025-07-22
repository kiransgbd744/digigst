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
 * @author Sasidhar
 *
 * 
 */

@Service("Anx1ASPSavableHsnAndLineServiceImpl")
public class Anx1ASPSavableHsnAndLineServiceImpl
		implements Anx1AspSavableUploadedService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ASPSavableHsnAndLineServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Anx1ASPHsnOutwardSavableDaoImpl")
	private Anx1AspHsnOutwardSavableSummaryDao aspHsnOutwardSavableSummaryDao;

	@Autowired
	@Qualifier("Anx1ASPHsnInwardSavableDaoImpl")
	private Anx1AspInwardSavableSummaryDao aspInwardSavableSummaryDao;

	@Autowired
	@Qualifier("Anx1AspLineOutwardSavableTransDaoImpl")
	private Anx1AspLineOutwardSavableSummaryDao aspLineOutwardSavableSummaryDao;

	@Autowired
	@Qualifier("Anx1AspLineInwardSavableTransDaoImpl")
	private Anx1AspLineInwardSavableSummaryDao aspLineInwardSavableSummaryDao;

	@Override
	public Workbook findSavableUploaded(SearchCriteria criteria,
			PageRequest pageReq) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"ANX-1_ASP_Transactional_Savable.xlsx");

		List<Object> responseFromHsnOutward = new ArrayList<>();
		responseFromHsnOutward = aspHsnOutwardSavableSummaryDao
				.getAnx1HsnOutwardSavableReports(request);
		LOGGER.debug("ANX1 aspHsnOutwardSavableSummaryDao data response"
				+ responseFromHsnOutward);
		if (responseFromHsnOutward != null
				&& responseFromHsnOutward.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.Savable.Hsn.Outward.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromHsnOutward,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromHsnOutward.size(), true, "yyyy-mm-dd", false);
		}
		List<Object> responseFromHsnInward = new ArrayList<>();
		responseFromHsnInward = aspInwardSavableSummaryDao
				.getAnx1HsnInwardSavableReports(request);
		if (responseFromHsnInward != null && responseFromHsnInward.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.Savable.Hsn.Inward.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(1).getCells();
			errorDumpCells.importCustomObjects(responseFromHsnInward,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromHsnInward.size(), true, "yyyy-mm-dd", false);
		}
		List<Object> responseFromLineOutward = new ArrayList<>();
		responseFromLineOutward = aspLineOutwardSavableSummaryDao
				.getLineOutwardSavableReports(request);
		if (responseFromLineOutward != null
				&& responseFromLineOutward.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.Savable.Line.Outward.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(2).getCells();
			errorDumpCells.importCustomObjects(responseFromLineOutward,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromHsnInward.size(), true, "yyyy-mm-dd", false);
		}
		List<Object> responseFromLineInward = new ArrayList<>();
		responseFromLineInward = aspLineInwardSavableSummaryDao
				.getLineInwardSavableReports(request);

		if (responseFromLineInward != null
				&& responseFromLineInward.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.Savable.Line.Inward.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(3).getCells();
			errorDumpCells.importCustomObjects(responseFromLineInward,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromLineInward.size(), true, "yyyy-mm-dd", false);
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
