/**
 * 
 */
package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Anx1VerticalDownloadReportsReqDto;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("Ret1And1AVerticalProcessedReportHandler")
public class Ret1And1AVerticalProcessedReportHandler {
	@Autowired
	@Qualifier("Ret1And1AVerticalProcessedReportServiceImpl")
	private Ret1And1AVerticalProcessedReportService ret1And1AVerticalProcessedReportService;

	public Workbook downloadRet1VerticalProcessedReport(
			Anx1VerticalDownloadReportsReqDto criteria) {

		return ret1And1AVerticalProcessedReportService.downloadReports(criteria,
				null);

	}

}
