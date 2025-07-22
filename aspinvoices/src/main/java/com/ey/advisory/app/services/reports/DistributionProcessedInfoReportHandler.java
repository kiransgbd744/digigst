package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.search.SearchCriteria;

@Component("DistributionProcessedInfoReportHandler")
public class DistributionProcessedInfoReportHandler {
	
	@Autowired
	@Qualifier("DistributionProcessedInfoReportsServiceImpl")
	private DistributionReportsService distributionReportsService;

	public void generateCsvForFileStatus(SearchCriteria criteria,
			String fullPath) {

		distributionReportsService.generateFileStatusCsv(criteria, null,
				fullPath);

	}

}


