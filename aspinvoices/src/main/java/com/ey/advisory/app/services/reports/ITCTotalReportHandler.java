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

@Component("ITCTotalReportHandler")
public class ITCTotalReportHandler {
	@Autowired
	@Qualifier("ItcTotalReportsServiceImpl")
	private ItcReportsService itcReportsService;

	public Workbook downloadITCTotalReport(
			Gstr1VerticalDownloadReportsReqDto criteria) {

		return itcReportsService.downloadReports(criteria, null);

	}

}
