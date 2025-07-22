package com.ey.advisory.app.services.reports.gstr1a;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

	@Component("Gstr1AAspNilSectionReportHandler")
	public class Gstr1AAspNilSectionReportHandler {

		@Autowired
		@Qualifier("Gstr1AAspNilSectionReportServiceImpl")
		private Gstr1AAspNilRatedReportsService gstr1AspNilRatedReportsService;

		public Workbook downloadRSProcessedReport(
				Gstr1ReviwSummReportsReqDto criteria) {

			return gstr1AspNilRatedReportsService
					.findNilReport(criteria, null);

		}

	}

