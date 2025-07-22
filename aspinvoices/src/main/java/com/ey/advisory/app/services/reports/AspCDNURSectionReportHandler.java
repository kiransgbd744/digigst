package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

	@Component("AspCDNURSectionReportHandler")
	public class AspCDNURSectionReportHandler {

		@Autowired
		@Qualifier("AspCDNURSectionReportServiceImpl")
		private Gstr1ReviewSummReportsService gstr1ReviewSummReportsService;

		public Workbook downloadRSProcessedReport(
				Gstr1ReviwSummReportsReqDto criteria) {

			return gstr1ReviewSummReportsService
					.findGstr1ReviewSummRecords(criteria, null);

		}

	}
