package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr2AProcessedRecordsReqDto;

/**
 * @author Sasidhar
 *
 * 
 */
@Component("Gstr2AProcessedRecScreenHandler")
public class Gstr2AProcessedRecScreenHandler {

	@Autowired
	@Qualifier("Gstr2aProcessedRecordsScreenServiceImpl")
	private Gstr2aProcessedRecordsScreenServiceImpl gstr2aProcessedRecordsScreenServiceImpl;

	@Autowired
	@Qualifier("Gstr2aReviewSummaryDownloadServiceImpl")
	private Gstr2aReviewSummaryDownloadServiceImpl gstr2aReviewSummaryDownloadServiceImpl;

	public Workbook getGstr2aSumTablesReports(
			Gstr2AProcessedRecordsReqDto criteria) {

		return gstr2aProcessedRecordsScreenServiceImpl
				.findProcessedScreenDownload(criteria);

	}

	public Workbook getGstr2aRevSummTablesReports(
			Gstr2AProcessedRecordsReqDto criteria) {

		return gstr2aReviewSummaryDownloadServiceImpl
				.findGstr2aRevSummTablesReports(criteria);

	}

}
