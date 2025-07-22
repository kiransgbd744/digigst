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

@Service("Anx1AspInwardProcessedUploadedServiceImpl")
public class Anx1AspInwardProcessedUploadedServiceImpl
		implements Anx1AspInwardProcessedUploadedService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1AspInwardProcessedUploadedServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Anx1ProcessedUploadedDaoImpl")
	private Anx1ProcessedUploadedDao anx1ProcessedUploadedDao;

	@Autowired
	@Qualifier("Anx1AspInwardProcessedUploadedDaoImpl")
	private Anx1AspInwardProcessedUploadedDao aspInwardProcessedUploadedDao;

	@Override
	public Workbook findInwardProcessedUploaded(SearchCriteria criteria,
			PageRequest pageReq) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Object> responseFromViewInward = new ArrayList<>();
		responseFromViewInward = aspInwardProcessedUploadedDao
				.getAnx1InwardProcessedUploadedDaoReport(request);
		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"ANX-1_Inward_ASP_Processed_Records_Uploaded.xlsx");

		if (responseFromViewInward != null
				&& responseFromViewInward.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.Asp.inward.process.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(1).getCells();
			errorDumpCells.importCustomObjects(responseFromViewInward,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewInward.size(), true, "yyyy-mm-dd", false);
		}

		return workbook;
	}

	@SuppressWarnings("unused")
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
