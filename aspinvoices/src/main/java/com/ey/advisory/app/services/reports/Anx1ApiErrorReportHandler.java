package com.ey.advisory.app.services.reports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.search.SearchCriteria;

@Component("Anx1ApiErrorReportHandler")
public class Anx1ApiErrorReportHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ApiErrorReportHandler.class);

	@Autowired
	@Qualifier("Anx1ApiErrorReportsServiceImpl")
	private Anx1ApiErrorReportsService anx1ApiErrorReportsService;

	public Workbook downloadApiErrorReport(SearchCriteria criteria) {

		return anx1ApiErrorReportsService.findApiErrorRec(criteria, null);

	}

}
