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
 * @author Laxmi.Salukuti
 *
 */
@Component("Gstr1RSHsnRateLevelProcessedReportHandler")
public class Gstr1RSHsnRateLevelProcessedReportHandler {

	@Autowired
	@Qualifier("Gstr1RSHsnRateLevelProcessedServiceImpl")
	private Gstr1ReviewSummReportsService gstr1hsnRatelevelReportsService;

	public Workbook downloadRSHsnProcessedReport(
			Gstr1ReviwSummReportsReqDto criteria) {

		return gstr1hsnRatelevelReportsService
				.findGstr1ReviewSummRecords(criteria, null);

	}

}
