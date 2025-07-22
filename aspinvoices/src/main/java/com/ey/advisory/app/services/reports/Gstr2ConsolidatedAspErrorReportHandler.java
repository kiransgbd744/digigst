package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;

@Component("Gstr2ConsolidatedAspErrorReportHandler")
public class Gstr2ConsolidatedAspErrorReportHandler {

	@Autowired
	@Qualifier("Gstr2ConsolidatedAspErrorReportsServiceImpl")
	private Gstr2ConsolidatedAspErrorReportsServiceImpl gstr2AspErrorReportsServiceImpl;

	public Workbook downloadErrorReport(Gstr2ProcessedRecordsReqDto searchParams) {
		return gstr2AspErrorReportsServiceImpl.findError(searchParams, null);

	}

}
