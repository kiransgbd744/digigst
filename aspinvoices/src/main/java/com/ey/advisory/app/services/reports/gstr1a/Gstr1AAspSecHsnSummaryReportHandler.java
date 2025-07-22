package com.ey.advisory.app.services.reports.gstr1a;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

	@Component("Gstr1AAspSecHsnSummaryReportHandler")
	public class Gstr1AAspSecHsnSummaryReportHandler {

		@Autowired
		@Qualifier("Gstr1AAspSecHsnSummaryReportsServiceImpl")
		private Gstr1AASPHsnSummaryReportsService gstr1ASPHsnSummaryReportsService;

		public Workbook downloadGstr1HsnSummaryReport(
				Gstr1ReviwSummReportsReqDto criteria) {

			return gstr1ASPHsnSummaryReportsService.findReports(criteria,
					null);

		}

	}
