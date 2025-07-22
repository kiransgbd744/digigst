package com.ey.advisory.controllers.anexure1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.services.reports.Gstr2ProcessedReportServiceImpl;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;

@Component("Gstr2ProcessedReportHandler")
public class Gstr2ProcessedReportHandler {

	@Autowired
	@Qualifier("Gstr2ProcessedReportServiceImpl")
	private Gstr2ProcessedReportServiceImpl gstr2ProcessedReportServiceImpl;

	public Workbook downloadErrorReport(Gstr2ProcessedRecordsReqDto searchParams) {
		return gstr2ProcessedReportServiceImpl.findError(searchParams, null);

	}

}
