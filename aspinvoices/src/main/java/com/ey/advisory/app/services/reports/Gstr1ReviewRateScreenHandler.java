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
 */

@Component("Gstr1ReviewRateScreenHandler")
public class Gstr1ReviewRateScreenHandler {
	
	@Autowired
	@Qualifier("Gstr1ReviewSumRateDownloadServiceImpl")
	private Gstr1ReviewSummaryDownloadService gstr1ReviewSumRateDownloadService;
	
	
	public Workbook getGstr1RevSummRateTablesReports(
			Gstr1ReviwSummReportsReqDto criteria) {

		return gstr1ReviewSumRateDownloadService
				.findGstr1RevSummTablesReports(criteria, null);

	}
}

