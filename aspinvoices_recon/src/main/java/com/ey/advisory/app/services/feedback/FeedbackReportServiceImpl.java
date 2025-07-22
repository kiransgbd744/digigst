/**
 * 
 */
package com.ey.advisory.app.services.feedback;

import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.ey.advisory.common.CommonUtility;

/**
 * @author Shashikant.Shukla
 *
 * 
 */

@Service("FeedbackReportServiceImpl")
public class FeedbackReportServiceImpl implements FeedbackReportService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FeedbackReportServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Override
	public Workbook findReportDownload(FeedbackReqReportDto criteria) {

		List<FeedbackReportDto> responseFromViewProcess = criteria.getReport();

		for (FeedbackReportDto feedbackDto : responseFromViewProcess) {
			if (feedbackDto.getIsFileReqQ2() == true) {
				feedbackDto.setAttachmentAvailableQ2("Yes");
			} else {
				feedbackDto.setAttachmentAvailableQ2("No");
			}
		}
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"FeedBack_Report_Download.xlsx");

		LOGGER.debug("FeedBack data response" + responseFromViewProcess);

		if (responseFromViewProcess != null
				&& responseFromViewProcess.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("feedback.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromViewProcess,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewProcess.size(), true, "yyyy-mm-dd", false);
		}

		return workbook;

	}

	private Workbook createWorkbookWithExcelTemplate(String folderName,
			String fileName) {
		Workbook workbook = null;
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			URL templateDir = classLoader.getResource(folderName + "/");
			String templatePath = templateDir.getPath() + fileName;
			LoadOptions options = new LoadOptions(FileFormatType.XLSX);
			CommonUtility.setAsposeLicense();
			workbook = new Workbook(templatePath, options);
			workbook.getSettings()
					.setMemorySetting(MemorySetting.MEMORY_PREFERENCE);
		} catch (Exception e) {
			LOGGER.error("Exception in creating workbook : ", e);
		}
		return workbook;
	}
}