package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

	@Component("AspSezWithTaxReportHandler")
	public class AspSezWithTaxReportHandler {

		@Autowired
		@Qualifier("AspSezWithTaxServiceImpl")
		private Gstr1ReviewSummReportsService gstr1sezWithTaxReportsService;

		public Workbook downloadSezWithTaxReport(
				Gstr1ReviwSummReportsReqDto criteria) {

			return gstr1sezWithTaxReportsService
					.findGstr1ReviewSummRecords(criteria, null);

		}

	}

