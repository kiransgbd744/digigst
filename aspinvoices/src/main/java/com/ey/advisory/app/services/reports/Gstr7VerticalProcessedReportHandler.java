package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;


	@Component("Gstr7VerticalProcessedReportHandler")
	public class Gstr7VerticalProcessedReportHandler {
		@Autowired
		@Qualifier("Gstr7VerticalProcessedServiceImpl")
		private Gstr7VerticalReportsService gstr7VerticalReportsService;

		public Workbook downloadGstr7VerticalProcessedReport(
				Gstr1VerticalDownloadReportsReqDto criteria) {

			return gstr7VerticalReportsService.downloadReports(criteria, null);

		}

	}


