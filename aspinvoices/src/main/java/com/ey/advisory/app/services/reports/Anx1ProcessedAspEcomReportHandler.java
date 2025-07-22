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

@Component("Anx1ProcessedAspEcomReportHandler")
public class Anx1ProcessedAspEcomReportHandler {

	@Autowired
	@Qualifier("Anx1ASPEcomSavableServiceImpl")
	private Anx1ASPEcomSavableService anx1ASPEcomSavableService;

	public Workbook findAnx1EcomSavableReports(
			Gstr1ReviwSummReportsReqDto criteria) {

		return anx1ASPEcomSavableService.findAnx1EcomSavableReports(criteria,
				null);

	}

}
