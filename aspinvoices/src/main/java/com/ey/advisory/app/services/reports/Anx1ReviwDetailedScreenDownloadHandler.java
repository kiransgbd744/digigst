/**
 * 
 */
package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * @author Sujith.Nanga
 *
 * 
 */
@Component("Anx1ReviwDetailedScreenDownloadHandler")
public class Anx1ReviwDetailedScreenDownloadHandler {

	@Autowired
	@Qualifier("Anx1ReviewDetailedDownloadServiceImpl")
	private Anx1ReviewSummScreenDownloadDetailedService anx1ReviewSummScreenDownloadDetailedService;

	public Workbook findReviewSumScreenDetailDownload(
			Annexure1SummaryReqDto criteria) {

		return anx1ReviewSummScreenDownloadDetailedService
				.findReviewSumScreenDetailDownload(criteria, null);

	}

}
