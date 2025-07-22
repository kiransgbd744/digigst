package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

	
	@Component("AspB2BASectionReportHandler")
	public class AspB2BASectionReportHandler {

		@Autowired
		@Qualifier("AspB2BASectionReportReportServiceImpl")
		private Gstr1ReviewSummReportsService gstr1ReviewSummReportsService;

		public Workbook downloadRSProcessedReport(
				Gstr1ReviwSummReportsReqDto criteria) {

			return gstr1ReviewSummReportsService
					.findGstr1ReviewSummRecords(criteria, null);

		}

	}

