package com.ey.advisory.app.services.reports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;

@Component("Anx1ProcessedReportHandler")
public class Anx1ProcessedReportHandler {


	@Autowired
	@Qualifier("Anx1ProcessedRecordsServiceImpl")
	private Anx1ProcessedReportsService anx1ProcessedReportsService;

	public Workbook downloadProcessedReport(Anx1FileStatusReportsReqDto criteria) {

		return anx1ProcessedReportsService.findProcesseRec(criteria, null);

	}

}
