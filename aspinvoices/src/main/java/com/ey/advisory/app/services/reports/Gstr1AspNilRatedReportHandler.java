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
@Component("Gstr1AspNilRatedReportHandler")
public class Gstr1AspNilRatedReportHandler {

	@Autowired
	@Qualifier("Gstr1AspNilRatedReportsServiceImpl")
	private Gstr1AspNilRatedReportsService gstr1AspNilRatedReportsService;

	public Workbook downloadGstr1NilRatedReport(
			Gstr1ReviwSummReportsReqDto criteria) {

		return gstr1AspNilRatedReportsService.findNilReport(criteria, null);

	}

}
