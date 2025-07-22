package com.ey.advisory.app.services.reports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.search.SearchCriteria;
@Component("Anx1ApiTotalRecordsReportHandler")
public class Anx1ApiTotalRecordsReportHandler {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ApiTotalRecordsReportHandler.class);

	@Autowired
	@Qualifier("Anx1ApiTotalReportsServiceImpl")
	private Anx1ApiTotalReportsService anx1ApiTotalReportsService;

	public Workbook downloadApiTotalRecordsReport(
			SearchCriteria criteria) {

		return anx1ApiTotalReportsService.findApiTotalRec(criteria,
				null);

	}

}



