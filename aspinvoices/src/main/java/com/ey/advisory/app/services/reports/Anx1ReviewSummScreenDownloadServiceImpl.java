/**
 * 
 */
package com.ey.advisory.app.services.reports;

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
import com.ey.advisory.app.data.views.client.Anx1RSScreenEcomdownloadDto;
import com.ey.advisory.app.data.views.client.Anx1RSScreenInwarddownloadDto;
import com.ey.advisory.app.data.views.client.Anx1RSScreenOutwarddownloadDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("Anx1ReviewSummScreenDownloadServiceImpl")
public class Anx1ReviewSummScreenDownloadServiceImpl
		implements Anx1ReviewSummScreenDownloadService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ReviewSummScreenDownloadServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Anx1ReviewSummOutwardScreenDownloadDaoImpl")
	private Anx1ReviewSummaryScreenDownloadDao anx1OutwardScreenDownloadDao;

	@Autowired
	@Qualifier("Anx1ReviewSummInwardScreenDownloadDaoImpl")
	private Anx1ReviewSummaryInwardScreenDownloadDao anx1InwardScreenDownloadDao;

	@Autowired
	@Qualifier("Anx1ReviewSummEcomScreenDownloadDaoImpl")
	private Anx1ReviewSummaryScreenEcomDownloadDao anx1EcomScreenDownloadDao;

	@Override
	public Workbook findReviewSumScreenDownload(SearchCriteria criteria,
			PageRequest pageReq) {
		Annexure1SummaryReqDto request = (Annexure1SummaryReqDto) criteria;
		Workbook workbook = new Workbook();
		int outwardStartRow = 3;
		int inwardStartRow = 22;
		int ecomStartRow = 39;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Anx1RSScreenOutwarddownloadDto> outwardRSresponse = anx1OutwardScreenDownloadDao
				.getReviewSummScreenDownload(request);

		List<Anx1RSScreenInwarddownloadDto> inwardRSresponse = anx1InwardScreenDownloadDao
				.getReviewSummInvScreenDownload(request);

		List<Anx1RSScreenEcomdownloadDto> ecomRSresponse = anx1EcomScreenDownloadDao
				.getReviewSummScreenEcomDownload(request);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"ANX-1_ReviewSummary_Screen_Download.xlsx");

		LOGGER.debug("Anx1 Review Summary screen download outward Response"
				+ outwardRSresponse);

		if (outwardRSresponse != null && outwardRSresponse.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp(
							"anx1.reviewsummscreen.outward.ecom.download.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(outwardRSresponse,
					invoiceHeaders, isHeaderRequired, outwardStartRow,
					startcolumn, outwardRSresponse.size(), true, "yyyy-mm-dd",
					false);
		}

		if (inwardRSresponse != null && inwardRSresponse.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp(
							"anx1.reviewsummscreen.inward.download.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(inwardRSresponse, invoiceHeaders,
					isHeaderRequired, inwardStartRow, startcolumn,
					inwardRSresponse.size(), true, "yyyy-mm-dd", false);
		}

		if (ecomRSresponse != null && ecomRSresponse.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp(
							"anx1.reviewsummscreen.ecom.download.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(ecomRSresponse, invoiceHeaders,
					isHeaderRequired, ecomStartRow, startcolumn,
					ecomRSresponse.size(), true, "yyyy-mm-dd", false);
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
