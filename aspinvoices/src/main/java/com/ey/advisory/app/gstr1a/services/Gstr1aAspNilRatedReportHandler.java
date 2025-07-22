package com.ey.advisory.app.gstr1a.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.services.reports.Gstr1AspNilRatedReportsService;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

@Component("Gstr1aAspNilRatedReportHandler")
public class Gstr1aAspNilRatedReportHandler {

	@Autowired
	@Qualifier("Gstr1aAspNilRatedReportsServiceImpl")
	private Gstr1AspNilRatedReportsService gstr1AspNilRatedReportsService;

	public Workbook downloadGstr1NilRatedReport(
			Gstr1ReviwSummReportsReqDto criteria) {

		return gstr1AspNilRatedReportsService.findNilReport(criteria, null);

	}

}
