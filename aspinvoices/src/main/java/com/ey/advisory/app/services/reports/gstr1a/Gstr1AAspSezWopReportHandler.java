package com.ey.advisory.app.services.reports.gstr1a;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

	@Component("Gstr1AAspSezWopReportHandler")
	public class Gstr1AAspSezWopReportHandler {

		@Autowired
		@Qualifier("Gstr1AAspSezWopServiceImpl")
		private Gstr1AReviewSummReportsService gstr1sezWithTaxReportsService;

		public Workbook downloadSezWopReport(
				Gstr1ReviwSummReportsReqDto criteria) {

			return gstr1sezWithTaxReportsService
					.findGstr1ReviewSummRecords(criteria, null);

		}

	}

