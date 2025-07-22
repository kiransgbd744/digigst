package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;

@Component("Anx1DataStatusSummaryReportHandler")
public class Anx1DataStatusSummaryReportHandler {

	@Autowired
	@Qualifier("Anx1DataStatusSummaryRecordsServiceImpl")
	private Anx1DataStatusSummaryReportsService anx1DataStatusSummaryReportsService;

	public Workbook downloadDataStatusSummaryReport(Anx1ReportSearchReqDto criteria) {

		return anx1DataStatusSummaryReportsService
				.findDataStatusSummaryRec(criteria, null);

	}

}
