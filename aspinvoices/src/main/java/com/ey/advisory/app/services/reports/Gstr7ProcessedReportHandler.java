package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

@Component("Gstr7ProcessedReportHandler")
public class Gstr7ProcessedReportHandler {

	@Autowired
	@Qualifier("Gstr7ProcessedServiceImpl")
	private Gstr7ProcessedReportsService gstr7ProcessedReportsService;

	public Workbook downloadProcessedReport(Gstr1ReviwSummReportsReqDto criteria) {

		return gstr7ProcessedReportsService.findGstr7ReviewSummRecords(criteria, null);

	}

}
