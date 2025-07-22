/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("CrossItcProcessReportsServiceImpl")
@Slf4j
public class CrossItcProcessReportsServiceImpl
		implements Gstr1VerticalReportsService {

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("CrossItcProcessReportsDaoImpl")
	private Gstr1VerticalReportsDao cewbProcessedReportsDao;

	@Override
	public Workbook downloadReports(SearchCriteria criteria,
			PageRequest pageReq) {

		Gstr1VerticalDownloadReportsReqDto request = (Gstr1VerticalDownloadReportsReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		List<Object> responseFromView = new ArrayList<>();
		responseFromView = cewbProcessedReportsDao
				.getGstr1VerticalReports(request);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Cross Itc Process response" + responseFromView);
		}

		if (responseFromView != null && !responseFromView.isEmpty()) {

			if ((GSTConstants.CROSS_ITC).equalsIgnoreCase(request.getFileType())) { 

				String[] b2csHeaders = commonUtility
						.getProp("crossitc.vertical.process.report.headers")
						.split(",");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("workbook properties reading starting ");
				}

				workbook = createWorkbookWithExcelTemplate("ReportTemplates",
						"Cross_ITC_Process.xlsx");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("workbook reading ending");
				}

				Cells processDumpCells = workbook.getWorksheets().get(0)
						.getCells();
				processDumpCells.importCustomObjects(responseFromView,
						b2csHeaders, isHeaderRequired, startRow, startcolumn,
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