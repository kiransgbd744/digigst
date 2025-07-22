package com.ey.advisory.app.services.reports.gstr1a;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;


	@Component("Gstr1AAspB2BSectionReportHandler")
	public class Gstr1AAspB2BSectionReportHandler {

		@Autowired
		@Qualifier("Gstr1AAspB2BSectionReportReportServiceImpl")
		private Gstr1AReviewSummReportsService gstr1aReviewSummReportsService;

		public Workbook downloadRSProcessedReport(
				Gstr1ReviwSummReportsReqDto criteria) {

			return gstr1aReviewSummReportsService
					.findGstr1ReviewSummRecords(criteria, null);

		}

	}

