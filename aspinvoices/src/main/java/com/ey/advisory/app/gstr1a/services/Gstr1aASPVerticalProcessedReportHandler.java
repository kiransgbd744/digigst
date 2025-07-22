package com.ey.advisory.app.gstr1a.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.services.reports.Gstr1ASPVerticalProcessReportsService;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;



@Component("Gstr1aASPVerticalProcessedReportHandler")
public class Gstr1aASPVerticalProcessedReportHandler {

	@Autowired
	@Qualifier("Gstr1aASPVerticalProcessReportsServiceImpl")
	private Gstr1ASPVerticalProcessReportsService gstr1ASPVerticalProcessReportsService;

	public Workbook downloadGstr1VerticalProcessedReport(
			Gstr1ReviwSummReportsReqDto criteria) {

		return gstr1ASPVerticalProcessReportsService.findReports(criteria,
				null);

	}

}
