package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.search.SearchCriteria;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("ITC04DataStatusReportHandler")
public class ITC04DataStatusReportHandler {

	
	@Autowired
	@Qualifier("ITC04DataStatusProcessedReportsServiceImpl")
	private Anx1ApiProcessedReportsService itc04ProcessedReportsService;
	
	@Autowired
	@Qualifier("ITC04DataStatusErrorReportsServiceImpl")
	private Anx1ApiProcessedReportsService itc04ErrorReportsService;
	
	@Autowired
	@Qualifier("ITC04DataStatusTotalReportsServiceImpl")
	private Anx1ApiProcessedReportsService itc04TotalReportsService;

	public Workbook downloadApiProcessedReport(
			SearchCriteria criteria) {

		return itc04ProcessedReportsService.findApiProcesseRec(criteria,
				null);

	}
	

	public Workbook downloadDataStatusErrorReport(
			SearchCriteria criteria) {

		return itc04ErrorReportsService.findApiProcesseRec(criteria,
				null);

	}
	
	public Workbook downloadDataStatusTotalRecordsReport(
			SearchCriteria criteria) {

		return itc04TotalReportsService.findApiProcesseRec(criteria,
				null);

	}


	
	
}
