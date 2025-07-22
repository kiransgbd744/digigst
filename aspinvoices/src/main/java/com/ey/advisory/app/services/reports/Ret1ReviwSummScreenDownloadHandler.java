/*package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Gstr1ReviwSummReportsReqDto;

@Component("Ret1ReviwSummScreenDownloadHandler")
public class Ret1ReviwSummScreenDownloadHandler {


	@Autowired
	@Qualifier("Ret1ReviewSummScreenDownloadServiceImpl")
	private Ret1ReviewSummScreenDownloadService ret1ReviewSummScreenDownloadService;

	public Workbook findRet1RSummScreenDownload(
			Gstr1ReviwSummReportsReqDto criteria) {

		return ret1ReviewSummScreenDownloadService
				.findReviewSumScreenDownload(criteria, null);

	}


}
*/