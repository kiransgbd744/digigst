package com.ey.advisory.app.services.reports.gstr1a;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

	@Component("Gstr1AAspSezWithTaxReportHandler")
	public class Gstr1AAspSezWithTaxReportHandler {

		@Autowired
		@Qualifier("Gstr1AAspSezWithTaxServiceImpl")
		private Gstr1AReviewSummReportsService gstr1sezWithTaxReportsService;

		public Workbook downloadSezWithTaxReport(
				Gstr1ReviwSummReportsReqDto criteria) {

			return gstr1sezWithTaxReportsService
					.findGstr1ReviewSummRecords(criteria, null);

		}

	}

