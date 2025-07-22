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

@Component("ANX1EntityLevelSummaryReportHandler")
public class ANX1EntityLevelSummaryReportHandler {

	@Autowired
	@Qualifier("ANX1EntityLevelSummaryServiceImpl")
	private ANX1EntityLevelSummaryService aNX1EntityLevelSummaryService;

	public Workbook downloadEntityLevelSummary(
			Gstr1ReviwSummReportsReqDto criteria) {

		return aNX1EntityLevelSummaryService.findEntityLevelSummary(criteria,
				null);

	}

}
