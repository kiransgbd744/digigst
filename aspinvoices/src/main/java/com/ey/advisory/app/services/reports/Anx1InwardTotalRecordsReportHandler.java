package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;

@Component("Anx1InwardTotalRecordsReportHandler")
public class Anx1InwardTotalRecordsReportHandler {
	
	@Autowired
	@Qualifier("Anx1InwardTotalRecReportsServiceImpl")
	private Anx1InwardTotalRecReportsService anx1InwardTotalRecReportsService;

	public Workbook downloadInwardTotalRecordsReport(Anx1FileStatusReportsReqDto criteria) {

		return anx1InwardTotalRecReportsService.findInwardTotalRecords(criteria, null);

	}

	
}


