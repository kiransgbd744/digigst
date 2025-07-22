package com.ey.advisory.app.services.reports.gstr1a;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

	@Component("Gstr1AAspAdvRecAmenSecReportHandler")
	public class Gstr1AAspAdvRecAmenSecReportHandler {

	@Autowired
	@Qualifier("Gstr1AAspAdvRecAmenSecServiceImpl")
	private Gstr1AASPAdvRecSavableService gstr1ASPAdvRecSavableService;

	public Workbook getGstr1AdvRecSavableReports(
			Gstr1ReviwSummReportsReqDto criteria) {

		return gstr1ASPAdvRecSavableService.
				findGstr1AdvRecSavableReports(criteria, null);

	}

}

