/**
 * 
 */
package com.ey.advisory.app.services.reports.gstr1a;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.gstr1a.einv.Gstr1AAspProcessVsSubmitReportService;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 * 
 */

@Slf4j
@Component("Gstr1AAspProcessVsSubmitReportHandler")
public class Gstr1AAspProcessVsSubmitReportHandler {

	@Autowired
	@Qualifier("Gstr1AAspProcessVsSubmitReportServiceImpl")
	private Gstr1AAspProcessVsSubmitReportService aspProcessVsSubmitReportService;

	public Workbook findProcessVsSubmitReports(
			Gstr1ReviwSummReportsReqDto criteria) {
		LOGGER.debug("Entered into Gstr1AAspProcessVsSubmitReportHandler class");
		return aspProcessVsSubmitReportService
				.AspProcessVsSubmitReports(criteria, null);
	}

}