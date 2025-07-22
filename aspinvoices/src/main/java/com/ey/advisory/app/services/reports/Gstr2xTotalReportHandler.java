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
@Component("Gstr2xTotalReportHandler")
public class Gstr2xTotalReportHandler {
	@Autowired
	@Qualifier("Gstr2xTotalReportServiceImpl")
	private Gstr2xReportService gstr2xReportService;

	public Workbook downloadGstr2xTotalReport(
			Gstr1VerticalDownloadReportsReqDto criteria) {

		return gstr2xReportService.downloadReports(criteria, null);

	}

}
