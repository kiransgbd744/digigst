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
import com.ey.advisory.app.data.views.client.Gstr6DistrubutionStatusReportsRespDto;
import com.ey.advisory.app.data.views.client.Gstr6SaveStatusReportsRespDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr6SaveStatusDownloadReqDto;
import com.ey.advisory.app.services.jobs.gstr6.Gstr6DistrubutiionsStatusDownloadDaoImpl;
import com.ey.advisory.app.services.jobs.gstr6.Gstr6SaveStatusDownloadDaoImpl;
import com.ey.advisory.common.CommonUtility;

@Service("Gstr6SaveStatusDownloadServiceImpl")
public class Gstr6SaveStatusDownloadServiceImpl {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6SaveStatusDownloadServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr6SaveStatusDownloadDaoImpl")
	private Gstr6SaveStatusDownloadDaoImpl gstr6SaveStatusDownloadDaoImpl;

	@Autowired
	@Qualifier("Gstr6DistrubutiionsStatusDownloadDaoImpl")
	private Gstr6DistrubutiionsStatusDownloadDaoImpl gstr6DistrubutiionsStatusDownloadDaoImpl;

	public Workbook fetchSaveStatus(
			List<Gstr6SaveStatusDownloadReqDto> reqDtos) {
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Gstr6SaveStatusReportsRespDto> responseFromView = new ArrayList<>();
		responseFromView = gstr6SaveStatusDownloadDaoImpl
				.fetchGst6SaveSections(reqDtos);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"GSTR-6_Saved_Submitted_Records.xlsx");

		if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr6.saved.submit.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromView, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromView.size(), true, "yyyy-mm-dd", false);
		}

		List<Gstr6DistrubutionStatusReportsRespDto> responseFromdist = new ArrayList<>();
		responseFromdist = gstr6DistrubutiionsStatusDownloadDaoImpl
				.fetchDistributionDataByReq(reqDtos);
		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"GSTR-6_Saved_Submitted_Records.xlsx");

		if (responseFromdist != null && responseFromdist.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr6.distribute.submit.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(1).getCells();
			errorDumpCells.importCustomObjects(responseFromdist, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromdist.size(), true, "yyyy-mm-dd", false);
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
