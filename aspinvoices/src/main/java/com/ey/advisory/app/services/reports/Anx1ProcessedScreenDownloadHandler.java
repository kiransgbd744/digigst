/**
 * 
 */
package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Anx1ProcessedRecordsReqDto;

/**
 * @author Sujith.Nanga
 *
 * 
 */
@Component("Anx1ProcessedScreenDownloadHandler")
public class Anx1ProcessedScreenDownloadHandler {

	@Autowired
	@Qualifier("Anx1ProcessedScreenDownloadServiceImpl")
	private Anx1ProcessedScreenDownloadService anx1ProcessedScreenDownloadService;

	public Workbook findProcessedDownload(Anx1ProcessedRecordsReqDto criteria) {

		return anx1ProcessedScreenDownloadService
				.findProcessedScreenDownload(criteria, null);

	}

}
