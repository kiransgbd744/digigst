package com.ey.advisory.app.services.reports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.core.search.SearchCriteria;

@Component("Anx1InwardApiProcessedInfoReportHandler")
public class Anx1InwardApiProcessedInfoReportHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1InwardApiProcessedInfoReportHandler.class);

	@Autowired
	@Qualifier("Anx1InwardApiProcessedInfoReportsServiceImpl")
	private Anx1InwardApiProcessedInfoReportsService anx1ApiInwardProcessedInfoReportsService;

	public Workbook downloadApiProcessedInfoReport(
			SearchCriteria criteria) {

		return anx1ApiInwardProcessedInfoReportsService.findInwardApiProcesseInfoRec(criteria,
				null);

	}

}
