package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("CewbTotalReportHandler")
public class CewbTotalReportHandler {
	@Autowired
	@Qualifier("CewbTotalReportsServiceImpl")
	private Gstr1VerticalReportsService gstr1VerticalReportsService;

	public Workbook downloadCewbTotalReport(
			Gstr1VerticalDownloadReportsReqDto criteria) {

		return gstr1VerticalReportsService.downloadReports(criteria, null);

	}

}
