/**
 * 
 */
package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;

/**
 * @author Sujith.Nanga
 *
 * 
 */
@Component("Gstr2xProcessedReportHandler")
public class Gstr2xProcessedReportHandler {
	@Autowired
	@Qualifier("Gstr2xProcessedReportServiceImpl")
	private Gstr2xReportService gstr2xReportService;

	public Workbook downloadGstr2xProcessReport(
			Gstr1VerticalDownloadReportsReqDto criteria) {

		return gstr2xReportService.downloadReports(criteria, null);

	}

}
