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
import com.ey.advisory.app.data.views.client.Anx1inwardapiErrorRecordsDto;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Siva.Nandam
 *
 */
@Service("Anx1InwardErrorReportsServiceImpl")
public class Anx1InwardErrorReportsServiceImpl implements Anx1ErrorReportsService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1InwardErrorReportsServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Anx1InwardRawFileErrorReportsDaoImpl")
	private Anx1InwardRawFileErrorReportsDaoImpl anx1ErrorReportsDao;

	@Override
	public Workbook findError(SearchCriteria criteria, PageRequest pageReq) {
		Anx1FileStatusReportsReqDto request = (Anx1FileStatusReportsReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		List<Anx1inwardapiErrorRecordsDto> responseFromView = new ArrayList<>();
		responseFromView = anx1ErrorReportsDao.getInwardErrorReports(request);

		if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.inward.api.error.report.headers").split(",");
			LOGGER.debug("workbook properties reading starting ");
			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"Anx1_InwardAPIErrorReport.xlsx");
			LOGGER.debug("workbook reading ending");
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
			workbook = new Workbook(templatePath,options);
			workbook.getSettings()
					.setMemorySetting(MemorySetting.MEMORY_PREFERENCE);
		} catch (Exception ex) {
			LOGGER.error("Exception in creating workbook : ", ex);
		}
		return workbook;
	}

}
