package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.search.SearchCriteria;


	@Component("DistributionProcessedReportHandler")
	public class DistributionProcessedReportHandler {
		@Autowired
		@Qualifier("DistributionProcessedReportsServiceImpl")
		private DistributionProcessedReportsService distributionProcessedReportsService;

		public void generateProcessedFileStatus(SearchCriteria criteria,
				String fullPath) {

			distributionProcessedReportsService.generateProcessedCsv(criteria, null,
					fullPath);

		}

	}


