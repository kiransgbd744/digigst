package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;


	@Component("Gstr6ProcessedReportHandler")
	public class Gstr6ProcessedReportHandler {

		@Autowired
		@Qualifier("Gstr6ProcessedServiceImpl")
		private Gstr6ProcessedReportsService gstr6ProcessedReportsService;

		public Workbook findGstr6Processed(
				Gstr1ReviwSummReportsReqDto criteria) {

			return gstr6ProcessedReportsService
					.findGstr6Processed(criteria, null);

		}

	}


