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
import com.ey.advisory.app.docs.dto.Anx1VerticalDownloadReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Service("Ret1And1AVerticalErrorReportServiceImpl")
public class Ret1And1AVerticalErrorReportServiceImpl
		implements Ret1And1AVerticalProcessedReportService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1And1AVerticalErrorReportServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Ret1And1AVerticalErrorReportsDaoImpl")
	private Ret1And1AVerticalReportsDao ret1And1AVerticalReportsDao;

	@Override
	public Workbook downloadReports(SearchCriteria criteria,
			PageRequest pageReq) {

		Anx1VerticalDownloadReportsReqDto request = (Anx1VerticalDownloadReportsReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		List<Object> responseFromView = new ArrayList<>();
		responseFromView = ret1And1AVerticalReportsDao
				.getRet1VerticalReports(request);

		LOGGER.debug("Ret1And1A vertical Error response" + responseFromView);

		if (responseFromView != null && !responseFromView.isEmpty()) {

			if ((DownloadReportsConstant.RET1AND1A)
					.equalsIgnoreCase(request.getFileType())) {

				String[] userinputsHeaders = commonUtility
						.getProp("ret1and1a.userinputs.error.report.headers")
						.split(",");
				LOGGER.debug("workbook properties reading starting ");

				workbook = createWorkbookWithExcelTemplate("ReportTemplates",
						"RET1And1AUSERINPUTSError.xlsx");

				LOGGER.debug("workbook reading ending");

				Cells processDumpCells = workbook.getWorksheets().get(0)
						.getCells();
				processDumpCells.importCustomObjects(responseFromView,
						userinputsHeaders, isHeaderRequired, startRow, startcolumn,
						responseFromView.size(), true, "yyyy-mm-dd", false);

			} else if ((DownloadReportsConstant.INTEREST)
					.equalsIgnoreCase(request.getFileType())) {
				String[] interestHeaders = commonUtility
						.getProp(
								"ret1and1a.interestandlatefee.error.report.headers")
						.split(",");

				LOGGER.debug("workbook properties reading starting ");

				workbook = createWorkbookWithExcelTemplate("ReportTemplates",
						"INTERESTAndLATEFEEError.xlsx");

				LOGGER.debug("workbook reading ending");

				Cells processDumpCells = workbook.getWorksheets().get(0)
						.getCells();
				processDumpCells.importCustomObjects(responseFromView,
						interestHeaders, isHeaderRequired, startRow, startcolumn,
						responseFromView.size(), true, "yyyy-mm-dd", false);

			} else if ((DownloadReportsConstant.SETOFFANDUTIL)
					.equalsIgnoreCase(request.getFileType())) {
				String[] setoffHeaders = commonUtility
						.getProp("ret1and1a.setoffutil.error.report.headers")
						.split(",");
				LOGGER.debug("workbook properties reading starting ");

				workbook = createWorkbookWithExcelTemplate("ReportTemplates",
						"SETOFFUTILIZATIONError.xlsx");

				LOGGER.debug("workbook reading ending");

				Cells processDumpCells = workbook.getWorksheets().get(0)
						.getCells();
				processDumpCells.importCustomObjects(responseFromView,
						setoffHeaders, isHeaderRequired, startRow, startcolumn,
						responseFromView.size(), true, "yyyy-mm-dd", false);

			} else if ((DownloadReportsConstant.REFUNDS)
					.equalsIgnoreCase(request.getFileType())) {
				String[] refundsHeaders = commonUtility
						.getProp("ret1and1a.refunds.error.report.headers")
						.split(",");
				LOGGER.debug("workbook properties reading starting ");

				workbook = createWorkbookWithExcelTemplate("ReportTemplates",
						"REFUNDSError.xlsx");

				LOGGER.debug("workbook reading ending");

				Cells processDumpCells = workbook.getWorksheets().get(0)
						.getCells();
				processDumpCells.importCustomObjects(responseFromView,
						refundsHeaders, isHeaderRequired, startRow, startcolumn,
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
