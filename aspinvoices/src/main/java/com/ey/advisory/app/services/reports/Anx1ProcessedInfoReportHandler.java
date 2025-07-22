package com.ey.advisory.app.services.reports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;

@Component("Anx1ProcessedInfoReportHandler")
public class Anx1ProcessedInfoReportHandler {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ProcessedInfoReportHandler.class);

	@Autowired
	@Qualifier("Anx1ProcessedInfoRecordsServiceImpl")
	private Anx1ProcessedInfoReportsService anx1ProcessedInfoReportsService;

	public Workbook downloadProcessedInfoReport(Anx1FileStatusReportsReqDto criteria) {

		return anx1ProcessedInfoReportsService.findProcessedInfoRec(criteria,
				null);

	}
}
