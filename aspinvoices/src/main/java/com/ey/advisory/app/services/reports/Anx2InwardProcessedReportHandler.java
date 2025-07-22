package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Anx2InwardErrorRequestDto;


	@Component("Anx2InwardProcessedReportHandler")
	public class Anx2InwardProcessedReportHandler {

		@Autowired
		@Qualifier("Anx2InwardProcessedReportsServiceImpl")
		private Anx2InwardProcessedReportsServiceImpl anx2ProcessedReportsService;

		public Workbook downloadProcessedReport(Anx2InwardErrorRequestDto criteria) {

			return anx2ProcessedReportsService.findProcessedRec(criteria, null);

		}

	}




