package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;


	
	@Component("Gstr7VerticalTotalReportHandler")
	public class Gstr7VerticalTotalReportHandler {
		@Autowired
		@Qualifier("Gstr7VerticalTotalReportsServiceImpl")
		private Gstr7VerticalReportsService gstr7VerticalReportsService;

		public Workbook downloadGstr7VerticalTotalReport(
				Gstr1VerticalDownloadReportsReqDto criteria) {

			return gstr7VerticalReportsService.downloadReports(criteria, null);

		}

	}


