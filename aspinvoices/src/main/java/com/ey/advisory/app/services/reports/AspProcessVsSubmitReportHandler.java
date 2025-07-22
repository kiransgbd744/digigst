/**
 * 
 */
package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahesh.Golla
 *
 * 
 */

@Slf4j
@Component("AspProcessVsSubmitReportHandler")
public class AspProcessVsSubmitReportHandler {

	@Autowired
	@Qualifier("AspProcessVsSubmitReportServiceImpl")
	private AspProcessVsSubmitReportService aspProcessVsSubmitReportService;

	public Workbook findProcessVsSubmitReports(
			Gstr1ReviwSummReportsReqDto criteria) {
		LOGGER.debug("Entered into AspProcessVsSubmitReportHandler class");
		return aspProcessVsSubmitReportService
				.AspProcessVsSubmitReports(criteria, null);
	}

}