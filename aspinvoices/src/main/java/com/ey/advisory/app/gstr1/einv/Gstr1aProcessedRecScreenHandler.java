package com.ey.advisory.app.gstr1.einv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.services.reports.Gstr1ProcessedScreenDownloadService;
import com.ey.advisory.app.services.reports.Gstr1ReviewSummaryDownloadService;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

@Component("Gstr1aProcessedRecScreenHandler")
public class Gstr1aProcessedRecScreenHandler {

	@Autowired
	@Qualifier("Gstr1aProcessedRecordsScreenServiceImpl")
	private Gstr1ProcessedScreenDownloadService gstr1ProcessedScreenDownloadService;

	@Autowired
	@Qualifier("Gstr1aReviewSummaryDownloadServiceImpl")
	private Gstr1ReviewSummaryDownloadService gstr1ReviewSummaryDownloadService;
	

	public Workbook getGstr1SummTablesReports(
			Gstr1ReviwSummReportsReqDto criteria) {

		return gstr1ProcessedScreenDownloadService
				.findProcessedScreenDownload(criteria, null);

	}

	public Workbook getGstr1RevSummTablesReports(
			Gstr1ReviwSummReportsReqDto criteria) {

		return gstr1ReviewSummaryDownloadService
				.findGstr1RevSummTablesReports(criteria, null);

	}
	
	

	}
