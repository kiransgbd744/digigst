package com.ey.advisory.app.gstr1a.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.services.reports.Gstr1ReviewSummReportsService;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;


@Component("Gstr1aRSHsnRateLevelProcessedReportHandler")
public class Gstr1aRSHsnRateLevelProcessedReportHandler {

	@Autowired
	@Qualifier("Gstr1aRSHsnRateLevelProcessedServiceImpl")
	private Gstr1ReviewSummReportsService gstr1hsnRatelevelReportsService;

	public Workbook downloadRSHsnProcessedReport(
			Gstr1ReviwSummReportsReqDto criteria) {

		return gstr1hsnRatelevelReportsService
				.findGstr1ReviewSummRecords(criteria, null);

	}

}
