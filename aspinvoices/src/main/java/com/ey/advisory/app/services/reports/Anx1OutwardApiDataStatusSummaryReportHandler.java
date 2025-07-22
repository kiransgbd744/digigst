package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.search.SearchCriteria;

@Component("Anx1OutwardApiDataStatusSummaryReportHandler")
public class Anx1OutwardApiDataStatusSummaryReportHandler {

	@Autowired
	@Qualifier("Anx1ApiOutwardDatastatusSummaryServiceImpl")
	private Anx1ApiOutwardDatastatusSummaryService anx1ApiOutwardDatastatusSummaryService;

	public Workbook downloadDatastatusSummary(SearchCriteria criteria) {

		return anx1ApiOutwardDatastatusSummaryService
				.findApiDatastatusSummary(criteria, null);

	}

}
