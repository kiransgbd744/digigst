package com.ey.advisory.app.gstr1a.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.services.reports.Gstr1ASPHsnSummaryReportsService;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

@Component("Gstr1aASPHSNRateReportHandler")
public class Gstr1aASPHSNRateReportHandler {

	@Autowired
	@Qualifier("Gstr1aASPHSNRateReportsServiceImpl")
	private Gstr1ASPHsnSummaryReportsService gstr1ASPHsnSummaryReportsService;

	public Workbook downloadGstr1HsnRateReport(
			Gstr1ReviwSummReportsReqDto criteria) {

		return gstr1ASPHsnSummaryReportsService.findReports(criteria, null);

	}

}
