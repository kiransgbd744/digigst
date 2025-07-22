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
 * @author Laxmi.Salukuti
 *
 */
@Component("Gstr1RSumErrorAspReportHandler")
public class Gstr1RSumErrorAspReportHandler {

	@Autowired
	@Qualifier("Gstr1RSumErrorAspServiceImpl")
	private Gstr1ReviewSummReportsService gstr1ReviewSummReportsService;

	public Workbook downloadRErrorAspReport(
			Gstr1ReviwSummReportsReqDto criteria) {

		return gstr1ReviewSummReportsService.findGstr1ReviewSummRecords(criteria,
				null);

	}

}
