package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;


	@Component("Gstr1VerticalProcessedInfoReportHandler")
	public class Gstr1VerticalProcessedInfoReportHandler {
		@Autowired
		@Qualifier("Gstr1VerticalProcessedInfoReportsServiceImpl")
		private Gstr1VerticalReportsService gstr1VerticalReportsService;

		public Workbook downloadGstr1VerticalProcessedInfoReport(
				Gstr1VerticalDownloadReportsReqDto criteria) {

			return gstr1VerticalReportsService.downloadReports(criteria, null);

		}

	}


