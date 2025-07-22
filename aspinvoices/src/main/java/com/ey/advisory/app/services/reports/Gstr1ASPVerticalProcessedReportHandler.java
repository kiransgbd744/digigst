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

@Component("Gstr1ASPVerticalProcessedReportHandler")
public class Gstr1ASPVerticalProcessedReportHandler {

	@Autowired
	@Qualifier("Gstr1ASPVerticalProcessReportsServiceImpl")
	private Gstr1ASPVerticalProcessReportsService gstr1ASPVerticalProcessReportsService;

	public Workbook downloadGstr1VerticalProcessedReport(
			Gstr1ReviwSummReportsReqDto criteria) {

		return gstr1ASPVerticalProcessReportsService.findReports(criteria,
				null);

	}

}
