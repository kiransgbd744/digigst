package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Gstr2bVsGstr3bProcessSummaryReqDto;

/**
 * @author Shashikant.Shukla
 *
 * 
 */
@Component("Gstr2bvs3bProcessedRecScreenHandler")
public class Gstr2bvs3bProcessedRecScreenHandler {

	@Autowired
	@Qualifier("Gstr2bvs3bProcessedRecordsScreenServiceImpl")
	private Gstr2bvs3bProcessedRecordsScreenServiceImpl gstr2bvs3bProcessedRecordsScreenServiceImpl;

	@Autowired
	@Qualifier("Gstr2bvs3bReviewSummaryDownloadServiceImpl")
	private Gstr2bvs3bReviewSummaryDownloadServiceImpl gstr2bvs3bReviewSummaryDownloadServiceImpl;

	public Workbook getGstr2bvs3bSumTablesReports(
			Gstr2bVsGstr3bProcessSummaryReqDto criteria) throws Exception {

		return gstr2bvs3bProcessedRecordsScreenServiceImpl
				.findGstr2bvs3bProcessedScreenDownload(criteria);

	}

	public Workbook getGstr2bvs3bRevSumTablesReports(
			Gstr2bVsGstr3bProcessSummaryReqDto criteria) throws Exception {

		return gstr2bvs3bReviewSummaryDownloadServiceImpl
				.findGstr2avs3bRevSummTablesReports(criteria);

	}

}
