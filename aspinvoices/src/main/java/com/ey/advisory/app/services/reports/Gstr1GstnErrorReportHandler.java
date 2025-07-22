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
@Component("Gstr1GstnErrorReportHandler")
public class Gstr1GstnErrorReportHandler {

	@Autowired
	@Qualifier("Gstr1GstnErrorReportServiceImpl")
	private Gstr1GstnErrorReportService gstr1GstnErrorReportService;

	public Workbook getGstr1InvSavableReports(
			Gstr1ReviwSummReportsReqDto criteria) {

		return gstr1GstnErrorReportService.findGstnErrorReports(criteria, null);

	}
}