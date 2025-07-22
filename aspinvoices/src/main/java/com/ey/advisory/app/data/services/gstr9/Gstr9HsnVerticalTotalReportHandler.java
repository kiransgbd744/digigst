package com.ey.advisory.app.data.services.gstr9;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;
import com.ey.advisory.app.services.reports.Gstr7VerticalReportsService;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Gstr9HsnVerticalTotalReportHandler")
public class Gstr9HsnVerticalTotalReportHandler {
	@Autowired
	@Qualifier("Gstr9HsnVerticalTotalReportsServiceImpl")
	private Gstr7VerticalReportsService gstr9VerticalReportsService;

	public Workbook downloadGstr7VerticalTotalReport(
			Gstr1VerticalDownloadReportsReqDto criteria) {

		return gstr9VerticalReportsService.downloadReports(criteria, null);

	}

}



