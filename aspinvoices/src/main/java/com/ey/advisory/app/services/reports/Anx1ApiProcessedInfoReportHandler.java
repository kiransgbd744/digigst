package com.ey.advisory.app.services.reports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.core.search.SearchCriteria;

@Component("Anx1ApiProcessedInfoReportHandler")
public class Anx1ApiProcessedInfoReportHandler {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ApiProcessedInfoReportHandler.class);
	
	@Autowired
	@Qualifier("Anx1ApiProcessedInfoReportsServiceImpl")
	private Anx1ApiProcessedInfoReportsService anx1ApiProcessedInfoReportsService;

	public Workbook downloadApiProcessedInfoReport(
			SearchCriteria criteria) {

		return anx1ApiProcessedInfoReportsService.findApiProcesseInfoRec(criteria,
				null);

	}

}
