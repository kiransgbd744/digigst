package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

@Component("AspB2CLASectionReportHandler")
public class AspB2CLASectionReportHandler {
	
	@Autowired
	@Qualifier("AspB2CLASectionReportReportServiceImpl")
	private Gstr1ReviewSummReportsService gstr1ReviewSummReportsService;

	public Workbook downloadRSProcessedReport(
			Gstr1ReviwSummReportsReqDto criteria) {

		return gstr1ReviewSummReportsService
				.findGstr1ReviewSummRecords(criteria, null);

	}

}


