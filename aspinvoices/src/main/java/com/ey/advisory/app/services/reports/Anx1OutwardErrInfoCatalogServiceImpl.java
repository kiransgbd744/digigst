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
import com.ey.advisory.app.data.views.client.Anx1CatalogErrInfoResponseDto;
import com.ey.advisory.app.docs.dto.Anx1CatalogErrInfoReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("Anx1OutwardErrInfoCatalogServiceImpl")
public class Anx1OutwardErrInfoCatalogServiceImpl
		implements Anx1OutwardErrInfoCatalogService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1OutwardErrInfoCatalogServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Anx1OutwardErrInfoCatalogDaoImpl")
	private Anx1OutwardErrInfoCatalogDao anx1OutwardErrInfoCatalogDao;

	@Override
	public Workbook findErrInfoCatalog(SearchCriteria criteria,
			PageRequest pageReq) {
		Anx1CatalogErrInfoReportsReqDto request = (Anx1CatalogErrInfoReportsReqDto) criteria;

		String type = request.getType();
		String dataType = request.getDataType();

		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		List<Anx1CatalogErrInfoResponseDto> errorinfoResonse = new ArrayList<>();
		errorinfoResonse = anx1OutwardErrInfoCatalogDao
				.getErrInfoCatalog(request);

		if ((type.equalsIgnoreCase(DownloadReportsConstant.ERR)
				&& dataType.equalsIgnoreCase(DownloadReportsConstant.OUTWARD))
				|| (type.equalsIgnoreCase(DownloadReportsConstant.ERR)
						&& dataType.equalsIgnoreCase(
								DownloadReportsConstant.INWARD))
				|| (type.equalsIgnoreCase(DownloadReportsConstant.ERR)
						&& dataType.equalsIgnoreCase(
								DownloadReportsConstant.OTHERS))) {

			if (errorinfoResonse != null && errorinfoResonse.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp(
								"anx1.inward.outward.error.catalog.report.headers")
						.split(",");
				workbook = createWorkbookWithExcelTemplate("ReportTemplates",
						"Anx1_ErrorCatalog.xlsx");
				Cells errorDumpCells = workbook.getWorksheets().get(0)
						.getCells();
				errorDumpCells.importCustomObjects(errorinfoResonse,
						invoiceHeaders, isHeaderRequired, startRow, startcolumn,
						errorinfoResonse.size(), true, "yyyy-mm-dd", false);
			}
		}
		if ((type.equalsIgnoreCase(DownloadReportsConstant.INFO)
				&& dataType.equalsIgnoreCase(DownloadReportsConstant.OUTWARD))
				|| (type.equalsIgnoreCase(DownloadReportsConstant.INFO)
						&& dataType.equalsIgnoreCase(
								DownloadReportsConstant.INWARD))
				|| (type.equalsIgnoreCase(DownloadReportsConstant.INFO)
						&& dataType.equalsIgnoreCase(
								DownloadReportsConstant.OTHERS))) {

			if (errorinfoResonse != null && errorinfoResonse.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp(
								"anx1.inward.outward.info.catalog.report.headers")
						.split(",");
				workbook = createWorkbookWithExcelTemplate("ReportTemplates",
						"Anx1_InformationCatalog.xlsx");
				Cells errorDumpCells = workbook.getWorksheets().get(0)
						.getCells();
				errorDumpCells.importCustomObjects(errorinfoResonse,
						invoiceHeaders, isHeaderRequired, startRow, startcolumn,
						errorinfoResonse.size(), true, "yyyy-mm-dd", false);
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
