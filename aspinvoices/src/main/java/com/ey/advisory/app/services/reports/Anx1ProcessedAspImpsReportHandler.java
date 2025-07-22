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
@Component("Anx1ProcessedAspImpsReportHandler")
public class Anx1ProcessedAspImpsReportHandler {

	@Autowired
	@Qualifier("Anx1ASPIMPSSavableSummaryServiceImpl")
	private Anx1ASPIMPSSavableService anx1ASPIMPSSavableService;

	public Workbook findAnx1IMPSSavableReports(
			Gstr1ReviwSummReportsReqDto criteria) {

		return anx1ASPIMPSSavableService.findAnx1IMPSSavableReports(criteria,
				null);

	}

}
