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

	@Component("Gstr1ASPHSNRateReportHandler")
	public class Gstr1ASPHSNRateReportHandler {

		@Autowired
		@Qualifier("Gstr1ASPHSNRateReportsServiceImpl")
		private Gstr1ASPHsnSummaryReportsService gstr1ASPHsnSummaryReportsService;

		public Workbook downloadGstr1HsnRateReport(
				Gstr1ReviwSummReportsReqDto criteria) {

			return gstr1ASPHsnSummaryReportsService.findReports(criteria,
					null);

		}

	}
