/**
 * 
 */
package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("Gstr1ProcessedRecScreenHandler")
public class Gstr1ProcessedRecScreenHandler {

	@Autowired
	@Qualifier("Gstr1ProcessedRecordsScreenServiceImpl")
	private Gstr1ProcessedScreenDownloadService gstr1ProcessedScreenDownloadService;

	@Autowired
	@Qualifier("Gstr1ReviewSummaryDownloadServiceImpl")
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
