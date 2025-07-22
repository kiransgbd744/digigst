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

@Component("Anx1ProcessedAspB2CReportHandler")
public class Anx1ProcessedAspB2CReportHandler {

	@Autowired
	@Qualifier("Anx1ASPB2CSavableSummaryServiceImpl")
	private Anx1ASPB2CSavableService anx1ASPB2CSavableService;

	public Workbook findAnx1B2CSavableReports(
			Gstr1ReviwSummReportsReqDto criteria) {

		return anx1ASPB2CSavableService.findAnx1B2CSavableReports(criteria,
				null);

	}

}
