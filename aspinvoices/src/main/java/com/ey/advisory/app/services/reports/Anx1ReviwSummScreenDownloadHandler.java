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
 * @author Laxmi.Salukuti
 *
 */
@Component("Anx1ReviwSummScreenDownloadHandler")
public class Anx1ReviwSummScreenDownloadHandler {

	@Autowired
	@Qualifier("Anx1ReviewSummScreenDownloadServiceImpl")
	private Anx1ReviewSummScreenDownloadService anx1ReviewSummScreenDownloadService;

	public Workbook findRSummScreenDownload(Annexure1SummaryReqDto criteria) {

		return anx1ReviewSummScreenDownloadService
				.findReviewSumScreenDownload(criteria, null);

	}

}