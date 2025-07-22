package com.ey.advisory.app.gstr1a.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.services.reports.Gstr1ASPAdvRecSavableService;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;


@Component("Gstr1aASPAdvRecSavableHandler")
public class Gstr1aASPAdvRecSavableHandler {

	@Autowired
	@Qualifier("Gstr1aASPAdvRecSavableServiceImpl")
	private Gstr1ASPAdvRecSavableService gstr1ASPAdvRecSavableService;

	public Workbook getGstr1AdvRecSavableReports(
			Gstr1ReviwSummReportsReqDto criteria) {

		return gstr1ASPAdvRecSavableService.
				findGstr1AdvRecSavableReports(criteria, null);

	}

}
