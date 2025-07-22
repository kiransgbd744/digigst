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

@Component("Anx2PrSummaryProcessedReportHandler")
public class Anx2PrSummaryProcessedReportHandler {

	@Autowired
	@Qualifier("Anx2PrSummaryServiceImpl")
	private Anx2PrSummaryService Anx2PrSummaryService;

	public Workbook findAnx2PrSummaryReports(
			Gstr1ReviwSummReportsReqDto criteria) {

		return Anx2PrSummaryService.findAnx2PRsummaryReports(criteria, null);

	}

}
