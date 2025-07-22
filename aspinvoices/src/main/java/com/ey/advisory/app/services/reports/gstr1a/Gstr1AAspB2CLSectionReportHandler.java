package com.ey.advisory.app.services.reports.gstr1a;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

	@Component("Gstr1AAspB2CLSectionReportHandler")
	public class Gstr1AAspB2CLSectionReportHandler {

		@Autowired
		@Qualifier("Gstr1AAspB2CLSectionReportReportServiceImpl")
		private Gstr1AReviewSummReportsService gstr1ReviewSummReportsService;

		public Workbook downloadRSProcessedReport(
				Gstr1ReviwSummReportsReqDto criteria) {

			return gstr1ReviewSummReportsService
					.findGstr1ReviewSummRecords(criteria, null);

		}

	}

