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

@Component("Anx1GSTNErrorReportHandler")
public class Anx1GSTNErrorReportHandler {

	@Autowired
	@Qualifier("Anx1GstnErrorReportServiceImpl")
	private Anx1GstnErrorReportService anx1GstnErrorReportService;

	public Workbook getAnx1GstnReports(Gstr1ReviwSummReportsReqDto criteria) {

		return anx1GstnErrorReportService.findGstnErrorReports(criteria, null);

	}
}
