package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;

@Component("Anx1ErrorReportHandler")
public class Anx1ErrorReportHandler {

	@Autowired
	@Qualifier("Anx1ErrorRecordsServiceImpl")
	private Anx1ErrorReportsService anx1ErrorReportsService;

	public Workbook downloadErrorReport(Anx1FileStatusReportsReqDto criteria) {

		return anx1ErrorReportsService.findError(criteria, null);

	}

}
