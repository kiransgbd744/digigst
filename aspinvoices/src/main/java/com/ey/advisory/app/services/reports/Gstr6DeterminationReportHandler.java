/**
 * 
 */
package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahesh.Golla
 *
 * 
 */

@Slf4j
@Component("Gstr6DeterminationReportHandler")
public class Gstr6DeterminationReportHandler {

	@Autowired
	@Qualifier("Gstr6DeterminationReportServiceImpl")
	private Gstr6DeterminationReportService gstr6DeterminationReportService; 

	public Workbook findGstr6DeterminationReports(
			Anx1ReportSearchReqDto criteria) {
		LOGGER.debug("Entered into findGstr6DeterminationReports class");
		return gstr6DeterminationReportService
				.gstr6DeterminationReports(criteria, null);
	}

}