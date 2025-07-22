package com.ey.advisory.app.gstr1a.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.services.reports.Gstr1ReviewSummaryDownloadService;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;


@Component("Gstr1aReviewRateScreenHandler")
public class Gstr1aReviewRateScreenHandler {
	
	@Autowired
	@Qualifier("Gstr1aReviewSumRateDownloadServiceImpl")
	private Gstr1ReviewSummaryDownloadService gstr1ReviewSumRateDownloadService;
	
	
	public Workbook getGstr1RevSummRateTablesReports(
			Gstr1ReviwSummReportsReqDto criteria) {

		return gstr1ReviewSumRateDownloadService
				.findGstr1RevSummTablesReports(criteria, null);

	}
}

