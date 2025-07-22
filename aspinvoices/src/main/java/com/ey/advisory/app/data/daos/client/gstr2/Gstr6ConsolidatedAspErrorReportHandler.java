package com.ey.advisory.app.data.daos.client.gstr2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;
import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;

@Component("Gstr6ConsolidatedAspErrorReportHandler")
public class Gstr6ConsolidatedAspErrorReportHandler {

	@Autowired
	@Qualifier("Gstr6ConsolidatedAspErrorReportsServiceImpl")
	private Gstr6ConsolidatedAspErrorReportsServiceImpl gstr6AspErrorReportsService;

	public Workbook downloadErrorReport(Gstr6SummaryRequestDto setDataSecurity) {
		return gstr6AspErrorReportsService.findError(setDataSecurity, null);

	}

}
