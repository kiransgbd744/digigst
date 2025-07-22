package com.ey.advisory.app.services.reports;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6AProcessedDataResponseDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ASummaryDataResponseDto;

/**
 * @author Sasidhar
 *
 * 
 */
@Component("Gstr6AProcessedRecScreenHandler")
public class Gstr6AProcessedRecScreenHandler {

	@Autowired
	@Qualifier("Gstr6AProcessedRecordsScreenServiceImpl")
	private Gstr6AProcessedRecordsScreenServiceImpl gstr6aProcessedScreenDownloadService;

	@Autowired
	@Qualifier("Gstr6AReviewSummaryDownloadServiceImpl")
	private Gstr6AReviewSummaryDownloadServiceImpl gstr6aReviewSummaryDownloadService;

	public Workbook getGstr6aSummTablesReports(List<Gstr6AProcessedDataResponseDto> responseData) throws Exception {
		return gstr6aProcessedScreenDownloadService.findGstr6aProcessedScreenDownload(responseData);
	}

	public Workbook getGstr6aReviewsummReports(List<Gstr6ASummaryDataResponseDto> responseData) throws Exception {

		return gstr6aReviewSummaryDownloadService.getGstr6aReviewsummReports(responseData);

	}

}
