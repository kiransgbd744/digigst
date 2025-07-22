package com.ey.advisory.app.services.configuremaster.reports;

import java.net.URL;
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
import com.ey.advisory.app.data.views.client.MasterErrorRecordsDto;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

@Service("masterErrorReportsService")
public class MasterErrorReportsServiceImpl
		implements MasterErrorReportsService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger("MasterErrorReportsServiceImpl");
	@Autowired
	@Qualifier("masterErrorReportsDao")
	private MasterErrorReportsDao masterErrorReportsDao;

	@Autowired
	private CommonUtility commonUtility;

	public Workbook downloadMasterErrorReports(final SearchCriteria criteria,
			final PageRequest pageReq) {
		Anx1FileStatusReportsReqDto request = (Anx1FileStatusReportsReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startColumn = 0;
		boolean isHeaderRequired = false;
		List<MasterErrorRecordsDto> recordsDtos = masterErrorReportsDao
				.getMasterError(request);
		if (recordsDtos != null) {
			String[] masterErrorHeaders = commonUtility
					.getProp("master.error.report").split(",");
			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"Master_Error_Details.xlsx");
			if (workbook != null) {
				Cells errorDumpCells = workbook.getWorksheets().get(0)
						.getCells();
				errorDumpCells.importCustomObjects(recordsDtos,
						masterErrorHeaders, isHeaderRequired, startRow,
						startColumn, recordsDtos.size(), true, "yyyy-mm-dd",
						false);
			}
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
