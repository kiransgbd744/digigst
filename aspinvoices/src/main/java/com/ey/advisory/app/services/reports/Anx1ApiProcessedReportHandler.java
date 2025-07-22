package com.ey.advisory.app.services.reports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.search.SearchCriteria;

@Component("Anx1ApiProcessedReportHandler")
public class Anx1ApiProcessedReportHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ApiProcessedReportHandler.class);

	@Autowired
	@Qualifier("Anx1ApiProcessedReportsServiceImpl")
	private Anx1ApiProcessedReportsService anx1ApiProcessedReportsService;

	public Workbook downloadApiProcessedReport(
			SearchCriteria criteria) {

		return anx1ApiProcessedReportsService.findApiProcesseRec(criteria,
				null);

	}

}
