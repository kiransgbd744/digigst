package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

@Component("Ret1ReviewSumScreenDownloadHandler")
public class Ret1ReviewSumScreenDownloadHandler {

	@Autowired
	@Qualifier("Ret1ReviewSummScreenDownloadServiceImpl")
	private Ret1ReviewSummScreenDownloadService ret1ReviewSummScreenDownloadService;

	public Workbook findRet1RSummScreen(Annexure1SummaryReqDto criteria) {

		return ret1ReviewSummScreenDownloadService
				.findReviewSumScreenDownload(criteria, null);

	}

}
