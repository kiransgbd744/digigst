/**
 * 
 */
package com.ey.advisory.app.data.daos.client.gstr2;

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
import com.ey.advisory.app.data.views.client.Gstr6aReportsDownloadRespDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr6SaveStatusDownloadReqDto;
import com.ey.advisory.app.services.jobs.gstr6.Gstr6aSaveStatusDownloadV2DaoImpl;
import com.ey.advisory.common.CommonUtility;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Service("Gstr6aSaveStatusDownloadV2ServiceImpl")
public class Gstr6aSaveStatusDownloadV2ServiceImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(Gstr6SaveStatusDownloadV2ServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr6aSaveStatusDownloadV2DaoImpl")
	private Gstr6aSaveStatusDownloadV2DaoImpl gstr6aSaveStatusDownloadDaoImpl;

	public Workbook fetchSaveStatus(List<Gstr6SaveStatusDownloadReqDto> reqDtos) {
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Gstr6aReportsDownloadRespDto> responseFromView = new ArrayList<>();
		responseFromView = gstr6aSaveStatusDownloadDaoImpl.fetchGst6aSaveSections(reqDtos);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates", "GSTR-6A_DownloadReport.xlsx");

		if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility.getProp("gstr6a.api.report.download.headers").split(",");

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
