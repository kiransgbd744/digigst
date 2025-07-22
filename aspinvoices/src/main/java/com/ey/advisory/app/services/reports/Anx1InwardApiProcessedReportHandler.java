package com.ey.advisory.app.services.reports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.search.SearchCriteria;

@Component("Anx1InwardApiProcessedReportHandler")
public class Anx1InwardApiProcessedReportHandler {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1InwardApiProcessedReportHandler.class);

	@Autowired
	@Qualifier("Anx1InwardApiProcessedReportsServiceImpl")
	private Anx1InwardApiProcessedReportsService anx1ApiInwardProcessedReportsService;

	public Workbook downloadApiProcessedReport(
			SearchCriteria criteria) {

		return anx1ApiInwardProcessedReportsService.findInwardApiProcesseRec(criteria,
				null);

	}

}



