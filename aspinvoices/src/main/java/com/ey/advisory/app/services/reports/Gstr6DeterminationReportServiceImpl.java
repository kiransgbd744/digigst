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
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahesh.Golla
 *
 * 
 */

@Service("Gstr6DeterminationReportServiceImpl")
@Slf4j
public class Gstr6DeterminationReportServiceImpl
		implements Gstr6DeterminationReportService {

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr6DeterminationReportDaoImpl")
	private Gstr6DeterminationDao gstr6DeterminationDao;

	@Override
	public Workbook gstr6DeterminationReports(SearchCriteria criteria,
			PageRequest pageReq) {

		Anx1ReportSearchReqDto request = (Anx1ReportSearchReqDto) criteria; 
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Object> repGstr6Det = new ArrayList<>();
		repGstr6Det = gstr6DeterminationDao.gstr6DeterminationReports(request);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"Gstr6Determination.xlsx");

		if (repGstr6Det != null && repGstr6Det.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("asp.gstr6.determination.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(repGstr6Det, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn, repGstr6Det.size(),
					true, "yyyy-mm-dd", false);
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
