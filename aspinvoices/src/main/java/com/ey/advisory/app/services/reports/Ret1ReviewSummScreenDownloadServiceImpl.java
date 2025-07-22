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
import com.ey.advisory.app.docs.dto.simplified.RefundSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1LateFeeSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1SummaryRespDto;
import com.ey.advisory.app.docs.dto.simplified.TaxPaymentSummaryDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

@Service("Ret1ReviewSummScreenDownloadServiceImpl")
public class Ret1ReviewSummScreenDownloadServiceImpl
		implements Ret1ReviewSummScreenDownloadService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1ReviewSummScreenDownloadServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Ret1DetailReviewSummDownloadDaoImpl")
	private Ret1ReviewSummaryScreenDownloadDao ret1ReviewSummaryScreenDownloadDao;

	@Override
	public Workbook findReviewSumScreenDownload(SearchCriteria criteria,
			PageRequest pageReq) {
		Annexure1SummaryReqDto request = (Annexure1SummaryReqDto) criteria;
		Workbook workbook = new Workbook();
		int detailsStartRow = 5;
		int interestAndLateStartRow = 109;
		int taxPaymentStartRow = 129;
		int refundStartRow = 72;
		int startcolumn = 1;
		boolean isHeaderRequired = false;

		List<Ret1SummaryRespDto> DetailsResponse = ret1ReviewSummaryScreenDownloadDao
				.getRet1ReviewSummScreenDownload(request);

		List<Ret1LateFeeSummaryDto> InterestAndLateResponse = ret1ReviewSummaryScreenDownloadDao
				.getRet1ReviewSummaryInterestAndLateFeeDao(request);

		List<TaxPaymentSummaryDto> TaxPaymentResponse = ret1ReviewSummaryScreenDownloadDao
				.getReviewSummTaxPaymentDownload(request);

		List<RefundSummaryDto> RefundResponse = ret1ReviewSummaryScreenDownloadDao
				.getReviewSummRefundDownload(request);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"RET-1_ReviewSummary_Screen_Download.xlsx");

		LOGGER.debug("Ret1 Review Summary screen download Response"
				+ DetailsResponse + InterestAndLateResponse + TaxPaymentResponse
				+ RefundResponse);

		if (DetailsResponse != null && DetailsResponse.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp(
							"ret1.reviewsumm.detailsresponse.download.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(DetailsResponse, invoiceHeaders,
					isHeaderRequired, detailsStartRow, startcolumn,
					DetailsResponse.size(), true, "yyyy-mm-dd", false);
		}

		if (InterestAndLateResponse != null
				&& InterestAndLateResponse.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp(
							"ret1.reviewsumm.interestandlate.download.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(InterestAndLateResponse,
					invoiceHeaders, isHeaderRequired, interestAndLateStartRow,
					startcolumn, InterestAndLateResponse.size(), true,
					"yyyy-mm-dd", false);
		}

		if (TaxPaymentResponse != null && TaxPaymentResponse.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp(
							"ret1.reviewsumm.taxPayment.download.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(TaxPaymentResponse,
					invoiceHeaders, isHeaderRequired, taxPaymentStartRow,
					startcolumn, TaxPaymentResponse.size(), true, "yyyy-mm-dd",
					false);
		}
		if (RefundResponse != null && RefundResponse.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("ret1.reviewsumm.refund.download.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.deleteBlankRows();
			errorDumpCells.importCustomObjects(RefundResponse, invoiceHeaders,
					isHeaderRequired, refundStartRow, startcolumn,
					RefundResponse.size(), true, "yyyy-mm-dd", false);
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
