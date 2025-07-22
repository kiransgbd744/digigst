package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.search.SearchCriteria;

@Component("GSTR3BTotalReportHandler")
public class GSTR3BTotalReportHandler {
	@Autowired
	@Qualifier("Gstr3BTotalReportsServiceImpl")
	private GSTR3BTotalReportService gSTR3BTotalReportService;

	public void generateCsvForFileStatus(SearchCriteria criteria, String fullPath) {

		gSTR3BTotalReportService.generateTotalCsv(criteria, null, fullPath);

	}

}
