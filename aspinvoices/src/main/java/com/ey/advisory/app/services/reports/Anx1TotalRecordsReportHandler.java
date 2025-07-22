package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;

@Component("Anx1TotalRecordsReportHandler")
public class Anx1TotalRecordsReportHandler {

	@Autowired
	@Qualifier("Anx1TotalRecReportsServiceImpl")
	private Anx1TotalRecReportsService anx1TotalRecReportsService;

	public Workbook downloadTotalRecordsReport(Anx1FileStatusReportsReqDto criteria) {

		return anx1TotalRecReportsService.findTotalRecords(criteria, null);

	}

	
}
