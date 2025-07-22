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

@Component("Anx1ProcessedAspVerticalReportHandler")
public class Anx1ProcessedAspVerticalReportHandler {

	@Autowired
	@Qualifier("Anx1ProcessedVerticalServiceImpl")
	private Anx1ProcessedVerticalService anx1ProcessedVerticalService;

	public Workbook findProcessedVertical(
			Gstr1ReviwSummReportsReqDto criteria) {

		return anx1ProcessedVerticalService.findProcessedVertical(criteria,
				null);

	}

}
