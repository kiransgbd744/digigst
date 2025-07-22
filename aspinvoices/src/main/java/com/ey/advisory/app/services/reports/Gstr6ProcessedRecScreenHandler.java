package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;

/**
 * @author Sasidhar
 *
 * 
 */
@Component("Gstr6ProcessedRecScreenHandler")
public class Gstr6ProcessedRecScreenHandler {

	@Autowired
	@Qualifier("Gstr6ProcessedRecordsScreenServiceImpl")
	private Gstr6ProcessedRecordsScreenServiceImpl gstr6ProcessedScreenDownloadService;

	@Autowired
	@Qualifier("Gstr6ReviewSummaryDownloadServiceImpl")
	private Gstr6ReviewSummaryDownloadService gstr6ReviewSummaryDownloadService;

	public Workbook getGstr6SummTablesReports(Gstr6SummaryRequestDto criteria) {

		return gstr6ProcessedScreenDownloadService.findGstr6ProcessedScreenDownload(criteria);

	}

	public Workbook getGstr6RevSummTablesReports(Annexure1SummaryReqDto criteria) {

		return gstr6ReviewSummaryDownloadService.findGstr6RevSummTablesReports(criteria);

	}

}
