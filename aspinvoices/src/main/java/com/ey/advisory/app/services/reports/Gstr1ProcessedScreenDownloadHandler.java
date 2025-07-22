package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsReqDto;

/**
 * @author Sasidhar
 *
 * 
 */
@Component("Gstr1ProcessedScreenDownloadHandler")
public class Gstr1ProcessedScreenDownloadHandler {

	@Autowired
	@Qualifier("Gstr1ProcessedRecordsScreenServiceImpl")
	private Gstr1ProcessedScreenDownloadService gstr1ProcessedScreenDownloadService;

	public Workbook findProcessedDownload(
			Gstr1ProcessedRecordsReqDto criteria) {

		return gstr1ProcessedScreenDownloadService
				.findProcessedScreenDownload(criteria, null);

	}

}
