/**
 * 
 */
package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;
import com.ey.advisory.core.dto.ITC04RequestDto;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("ITCProcessedReportHandler")
public class ITCProcessedReportHandler {
	@Autowired
	@Qualifier("ITCProcessedReportsServiceImpl")
	private ItcReportsService itcReportsService;

	@Autowired
	@Qualifier("ITC04PrSummaryReportsServiceImpl")
	private ITC04PrSummaryReportsServiceImpl prSummaryReportsServiceImpl;

	@Autowired
	@Qualifier("ITC04ReviewSummaryReportsServiceImpl")
	private ITC04ReviewSummaryReportsServiceImpl reviewSummaryReportsServiceImpl;

	public Workbook downloadITCProcessReport(
			Gstr1VerticalDownloadReportsReqDto criteria) {

		return itcReportsService.downloadReports(criteria, null);

	}

	public Workbook downloadITCProcessSummaryData(ITC04RequestDto criteria) {
		return prSummaryReportsServiceImpl.downloadPrsummaryReports(criteria);
	}

	public Workbook downloadITCReviewSummaryData(ITC04RequestDto criteria) {
		return reviewSummaryReportsServiceImpl
				.downloadReviewsummaryReports(criteria);
	}

}
