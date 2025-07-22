package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.search.SearchCriteria;

@Component("GSTR3BProcessedReportHandler")
	public class GSTR3BProcessedReportHandler {
		@Autowired
		@Qualifier("Gstr3BProcessedReportsServiceImpl")
		private GSTR3BProcessedReportService gSTR3BProcessedReportService;

		public void generateCsvForFileStatus(SearchCriteria criteria, String fullPath) {

			gSTR3BProcessedReportService.generateProcessedCsv(criteria, null, fullPath);

		}

	}


