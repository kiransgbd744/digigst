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

@Component("Gstr2xErrorReportHandler")
public class Gstr2xErrorReportHandler {
	@Autowired
	@Qualifier("Gstr2xErrorReportServiceImpl")
	private Gstr2xReportService gstr2xReportService;

	public Workbook downloadGstr2xErrorReport(
			Gstr1VerticalDownloadReportsReqDto criteria) {

		return gstr2xReportService.downloadReports(criteria, null);

	}

}
