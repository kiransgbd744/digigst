package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

	@Component("AspSecHsnSummaryReportHandler")
	public class AspSecHsnSummaryReportHandler {

		@Autowired
		@Qualifier("AspSecHsnSummaryReportsServiceImpl")
		private Gstr1ASPHsnSummaryReportsService gstr1ASPHsnSummaryReportsService;

		public Workbook downloadGstr1HsnSummaryReport(
				Gstr1ReviwSummReportsReqDto criteria) {

			return gstr1ASPHsnSummaryReportsService.findReports(criteria,
					null);

		}

	}
